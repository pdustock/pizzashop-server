package com.pizzashop.server;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@EnableJms
@Configuration
public class ActiveMQConfiguration {
	@Bean
	public Queue createOvenQueue() {
		return new ActiveMQQueue("pizza.queue");
	}

}
