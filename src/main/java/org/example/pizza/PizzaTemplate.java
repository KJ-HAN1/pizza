/**
 * 현재 치즈, 페퍼로니, 콤비네이션 피자만 제공
 *
*/
package org.example.pizza;

import org.example.topping.ToppingType;

import java.util.List;

public interface PizzaTemplate {
    String getName();
    int getPrice();
    List<ToppingType> getToppings();
}
