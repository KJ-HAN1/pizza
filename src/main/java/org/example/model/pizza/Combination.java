package org.example.model.pizza;

import org.example.model.Topping.ToppingType;

import java.util.List;

import static org.example.model.Topping.ToppingType.*;

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
