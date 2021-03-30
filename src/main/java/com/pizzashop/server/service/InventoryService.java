package com.pizzashop.server.service;

import com.pizzashop.server.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryService {

	private Map<String, Integer> inventoryMap = new HashMap<>();

	public Map<String, Integer> getInventory() {
		return this.inventoryMap;
	}
	public void setInventoryMap(Map<String, Integer> inventoryMap) {
		this.inventoryMap = inventoryMap;
	}

	@PostConstruct
	public void populateInventory() throws NumberFormatException, IOException {

		ClassPathResource file = new ClassPathResource("Sr._Java_Developer_Testing-_Ingredients.csv");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

			String line = null;
			line = reader.readLine();// skip header
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				inventoryMap.put(parts[0], Integer.valueOf(parts[1]));
			}
		}

	}

	public synchronized Optional<String> deductInventory(Order order) {
		log.debug("before order:{}, the inventory is:{}", order.getOrderId(), this.inventoryMap);

		Map<String, Integer> workingMap = cloneMap(this.inventoryMap);
		StringBuilder outOfStockIngredients = new StringBuilder();
		List<PizzasWithAmount> pizzasWithAmounts = order.getPizzasWithAmounts();
		for (PizzasWithAmount pizzasWithAmount : pizzasWithAmounts) {
			int howMany = Integer.parseInt(pizzasWithAmount.getAmount());
			Pizza pizza = pizzasWithAmount.getPizza();
			List<IngredientsWithAmount> ingredientsWithAmountList = pizza.getIngredientsWithAmount();

			for (IngredientsWithAmount ingredientsWithAmount : ingredientsWithAmountList) {
				String ingredient = ingredientsWithAmount.getInputIngredients();
				int quantity = Integer.parseInt(ingredientsWithAmount.getQuantities());
				int leftIngredientAmount = workingMap.get(ingredient) - howMany*quantity;
				if (leftIngredientAmount < 0) {
					outOfStockIngredients.append(ingredient + " ordered:"
							+ howMany*quantity+ " storage just have:" + workingMap.get(ingredient)  + ";");
					break;
				} else {
					workingMap.put(ingredient, leftIngredientAmount);
				}
			}
			if (outOfStockIngredients.length() > 0)
				break;
		}
		if (outOfStockIngredients.length() > 0) {
			log.debug("after order:{}, the inventory is:{}", order.getOrderId(), this.inventoryMap);
		}  else{
			setInventoryMap(workingMap);
		}
		return Optional.of(outOfStockIngredients.toString());

	}

	private Map<String, Integer> cloneMap(Map<String, Integer> map) {
		Map<String, Integer> cloned = map.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		return cloned;
	}

}
