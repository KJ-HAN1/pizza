package org.example.promotion;

import org.example.pizza.PizzaTemplate;
import org.example.topping.ToppingInventory;
import org.example.topping.ToppingType;


public interface ToppingPromotionPolicy {
    boolean shouldApply(PizzaTemplate pizza, ToppingInventory inventory);
    boolean isActive();
    ToppingType getRewardTopping();
}
