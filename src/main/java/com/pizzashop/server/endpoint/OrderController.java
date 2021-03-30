package com.pizzashop.server.endpoint;


import com.pizzashop.server.dto.Order;
import com.pizzashop.server.service.InventoryService;
import com.pizzashop.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    InventoryService inventoryService;

    @PostMapping("order")
    public ResponseEntity<String> processOrder(@RequestBody Order order) {
        log.debug("Order received:{}", order.getOrderId());
        Optional<String> enoughInventory = orderService.processOrder(order);
        if (enoughInventory.toString().equals("Optional[]")) {
            return ResponseEntity.ok().build();
        } else {
            String message = "Out of order. Details:" + enoughInventory.get().toString();
            log.error(message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }
    }

}
