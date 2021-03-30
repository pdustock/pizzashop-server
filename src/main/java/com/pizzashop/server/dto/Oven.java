package com.pizzashop.server.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Oven {
	@Value("${oven.cooktime.seconds}")
	long cooktime;

	public void cookPizza() {
		log.debug("===>>> Start cooking pizza in parellel");
		try {
			log.debug("cook time is:{} seconds, and conifgurable in application.properties oven.cooktime.seconds",
					cooktime);
			Thread.sleep(cooktime * 1000);
		} catch (InterruptedException e) {
		}
		log.debug("Pizza is done");
	}

}
