package com.pizzashop.server.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Pizza implements Serializable {
    public Pizza(List<IngredientsWithAmount> ingredientsWithAmount) {
        this.ingredientsWithAmount = ingredientsWithAmount;
    }

    public Pizza() {
    }

    private List<IngredientsWithAmount> ingredientsWithAmount;
}