package com.pizzashop.server.service;

import com.pizzashop.server.dto.Order;
import com.pizzashop.server.dto.Oven;
import com.pizzashop.server.dto.Pizza;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class QueueConsumer {

	@Autowired
	Oven oven;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	QueueProducer producer;
	@Autowired
	OrderService pizzaService;

	@JmsListener(destination = "pizza.queue", concurrency = "${oven.count}-${oven.count}")
	public void onMessage(Order order) {
		log.debug("JMS message received, order:{}", order);
		pizzaService.beforeCooking(order);
		oven.cookPizza();
		log.debug("JMS message processed, pizza:{}", order);
		pizzaService.afterCooking(order);
	}
}
