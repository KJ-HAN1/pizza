package org.example.pizza;

import org.example.topping.ToppingType;

import java.util.List;

import static org.example.topping.ToppingType.*;

public class Combination implements Pizza {
    @Override
    public String getName() {
        return "Combination Pizza";
    }

    @Override
    public int getPrice() {
        return 18000;
    }

    @Override
    public List<ToppingType> getToppings() {
        return List.of(
                CHEESE,
                SAUCE,
                DOUGH,
                ONION,
                MEAT,
                OLIVE,
                TOMATO
        );
    }
}
