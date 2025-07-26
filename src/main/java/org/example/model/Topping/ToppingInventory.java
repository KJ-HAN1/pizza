package org.example.model.Topping;

import org.example.model.pizza.Pizza;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ToppingInventory {
    private final Map<ToppingType, Integer> stock = new EnumMap<>(ToppingType.class);

    public ToppingInventory() {
        ToppingType[] types = ToppingType.values();
        for (int i = 0; i < types.length; i++) {
            stock.put(types[i], 10);
        }
    }

    public boolean hasStock(Pizza pizza) {
        List<ToppingType> toppings = pizza.getToppings();
        for (int i = 0; i < toppings.size(); i++) {
            if (stock.getOrDefault(toppings.get(i), 0) <= 0) {
                return false;
            }
        }
        return true;
    }

    public void consume(Pizza pizza) {
        List<ToppingType> toppings = pizza.getToppings();
        for (int i = 0; i < toppings.size(); i++) {
            stock.put(toppings.get(i), stock.get(toppings.get(i)) - 1);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- 재고 현황 ---\n");
        List<ToppingType> types = new ArrayList<>(stock.keySet());
        for (int i = 0; i < types.size(); i++) {
            ToppingType type = types.get(i);
            int count = stock.get(type);
            sb.append(String.format("%-10s : %3d개 (단가 %5d원)%n",
                    type, count, type.getCost()));
        }
        sb.append("--------------------------\n");
        return sb.toString();
    }
}
