package org.example.model.pizza;

import org.example.model.Topping.ToppingType;

import java.util.List;

import static org.example.model.Topping.ToppingType.*;

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
                CHEESE,
                SAUCE,
                DOUGH,
                ONION,
                PEPPERONI
        );
    }
}
