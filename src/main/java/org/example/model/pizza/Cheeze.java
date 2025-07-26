package org.example.model.pizza;

import org.example.model.Topping.ToppingType;

import java.util.List;

import static org.example.model.Topping.ToppingType.*;

public class Cheeze implements Pizza {
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
