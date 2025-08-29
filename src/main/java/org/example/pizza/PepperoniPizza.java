package org.example.pizza;

import org.example.topping.ToppingType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.topping.ToppingType.*;

@Component
public class PepperoniPizza implements PizzaTemplate {
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
