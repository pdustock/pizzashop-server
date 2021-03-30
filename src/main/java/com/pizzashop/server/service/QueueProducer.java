package com.pizzashop.server.service;


import com.pizzashop.server.dto.Order;
import com.pizzashop.server.dto.Pizza;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

@Service
@Slf4j
public class QueueProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishPizzaMessage(Order order) {
        jmsTemplate.send("pizza.queue", new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(order);
                return objectMessage;
            }
        });
        log.debug("pizza published:{}", order);
    }
}
