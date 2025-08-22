package org.example.pizza;

import org.example.Topping.ToppingType;

import java.util.List;

import static org.example.Topping.ToppingType.*;

public class Pepperoni implements Pizza {
    @Override
    public String getName() {
        return "Pepperoni Pizza";
    }

    @Override
    public int getPrice() {
        return 15000;
    }

    @Override
    public List<ToppingType> getToppings() {
        return List.of(
                SAUCE,
                DOUGH,
                ONION,
                PEPPERONI
        );
    }
}
