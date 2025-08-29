package org.example.pizza;

import org.example.topping.ToppingType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.topping.ToppingType.*;

@Component
public class CombinationPizza implements PizzaTemplate {
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
