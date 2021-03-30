package com.pizzashop.server.service;

import com.pizzashop.server.dto.Order;
import com.pizzashop.server.dto.Pizza;
import com.pizzashop.server.dto.PizzasWithAmount;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class OrderService {
    @Value("${oven.count} ")
    private int ovenCount;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    QueueProducer queueProducer;
    @Autowired
    private RestTemplate restTemplate;

    private ConcurrentMap<String, Integer> orderCounts = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> orderURLs = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> countDowns = new ConcurrentHashMap<>();

    public Optional<String> processOrder(Order order) {

        Optional<String> enoughInventory = inventoryService.deductInventory(order);

        if (enoughInventory.toString().equals("Optional[]")) {
            int total = this.addToOrder(order);
            log.debug("Count pizzas {}", total);
            for (int i = 0; i < total; i++) {
                queueProducer.publishPizzaMessage(order);
            }
        }
        return enoughInventory;
    }

    public int countTotalPizzasInOrder(Order order) {
        int count = 0;
        List<PizzasWithAmount> pizzasWithAmounts = order.getPizzasWithAmounts();
        for (PizzasWithAmount pizzasWithAmount : pizzasWithAmounts) {
            count = count + Integer.parseInt(pizzasWithAmount.getAmount());
        }
        return new Integer(count);
    }

    private int addToOrder(Order order) {
        Integer totalPizzasInOrder = countTotalPizzasInOrder(order);
        orderCounts.put(order.getOrderId(), totalPizzasInOrder);
        orderURLs.put(order.getOrderId(), order.getCallbackUrl());
        countDowns.put(order.getOrderId(), false);
        return totalPizzasInOrder.intValue();
    }

    public synchronized void afterCooking(Order order) {
        int pizzaCount = this.orderCounts.get(order.getOrderId());
        log.debug("    afterCooking pizza count: {}", pizzaCount);
        if (pizzaCount <= 1) {
            // order is done, call client
            completeOrder(this.orderURLs.get(order.getOrderId()), order.getOrderId());
            removeOrder(order.getOrderId());
        } else {
            // not done, decrease the count of pizza to cook
            this.orderCounts.put(order.getOrderId(), this.orderCounts.get(order.getOrderId()) - 1);
        }
    }

    private void removeOrder(String orderId) {
        this.countDowns.remove(orderId);
        this.orderCounts.remove(orderId);
        this.orderURLs.remove(orderId);
    }

    public synchronized void beforeCooking(Order order) {
        int pizzaCount = this.orderCounts.get(order.getOrderId());
        boolean countDown = this.countDowns.get(order.getOrderId());
        log.debug("    beforeCooking order count: {}, ovenCount:{}, countDown:{}",
                this.orderCounts.get(order.getOrderId()), ovenCount, countDown);
        if (pizzaCount <= ovenCount && countDown == false) {
            startCountDown(this.orderURLs.get(order.getOrderId()), order.getOrderId());
            this.countDowns.put(order.getOrderId(), true);
        }
    }

    private void completeOrder(String url, String orderId) {
        StringBuilder callbackurl = new StringBuilder();
        callbackurl.append("http://localhost:").append(url).append("/api/");
        restTemplate.exchange(callbackurl.toString()+"orderComplete", HttpMethod.GET, null, String.class).getBody();
        log.debug("##order {} is complete, call back at:{}", orderId, url);
    }

    private void startCountDown(String url, String orderId) {
        StringBuilder callbackurl = new StringBuilder();
        callbackurl.append("http://localhost:").append(url).append("/api/");
        restTemplate.exchange(callbackurl.toString()+ "startCountDown", HttpMethod.GET, null, String.class).getBody();
        log.debug("##this is the last pizza to cook for order {}, start counting down at:{}", orderId, url);
    }

}
