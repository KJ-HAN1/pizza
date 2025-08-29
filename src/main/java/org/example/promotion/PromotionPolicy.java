package org.example.promotion;

import org.example.pizza.PizzaTemplate;
import org.example.topping.ToppingInventory;


public interface PromotionPolicy {
    void apply(PizzaTemplate pizza, ToppingInventory inventory);
    boolean isActive();
}
