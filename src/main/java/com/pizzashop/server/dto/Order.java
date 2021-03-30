package com.pizzashop.server.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Order implements Serializable {
	private String orderId;
	private String callbackUrl;
	private List<PizzasWithAmount> pizzasWithAmounts;

	public String getOrderId() {
		return orderId;
	}

	public Order() {
	}
}
