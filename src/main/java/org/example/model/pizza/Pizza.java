/**
 * 현재 치즈, 페퍼로니, 콤비네이션 피자만 제공
 *
*/
package org.example.model.pizza;

import org.example.model.Topping.ToppingType;

import java.util.List;

public interface Pizza {
    String getName();
    int getPrice();
    List<ToppingType> getToppings();
}
