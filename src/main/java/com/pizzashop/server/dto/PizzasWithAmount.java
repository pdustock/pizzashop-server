package com.pizzashop.server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PizzasWithAmount implements Serializable {
    public PizzasWithAmount(Pizza pizza, String amount) {
        this.pizza = pizza;
        this.amount = amount;
    }

    public PizzasWithAmount() {
    }

    private Pizza pizza;
    private String amount;

}
