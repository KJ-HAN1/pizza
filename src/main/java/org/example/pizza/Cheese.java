package org.example.pizza;

import org.example.topping.ToppingType;

import java.util.List;

import static org.example.topping.ToppingType.*;

public class Cheese implements Pizza {
    @Override
    public String getName() {
        return "Cheeze Pizza";
    }

    @Override
    public int getPrice() {
        return 12000;
    }

    @Override
    public List<ToppingType> getToppings() {
        return List.of(
                CHEESE,
                SAUCE,
                DOUGH,
                ONION
        );
    }
}
