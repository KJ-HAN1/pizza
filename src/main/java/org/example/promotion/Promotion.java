package org.example.promotion;

import org.example.pizza.Pizza;
import org.example.topping.ToppingInventory;


public interface Promotion {
    void apply(Pizza pizza, ToppingInventory inventory);
    boolean isActive();
}
