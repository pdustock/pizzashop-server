package com.pizzashop.server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class IngredientsWithAmount implements Serializable {
        private String inputIngredients;
        private String quantities;

    public IngredientsWithAmount() {
    }

    public IngredientsWithAmount(String inputIngredients, String quantities) {
            this.inputIngredients = inputIngredients;
            this.quantities = quantities;
        }
    }