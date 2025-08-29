package org.example.topping;

import org.example.pizza.PizzaTemplate;

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

    public boolean hasStock(PizzaTemplate pizza) {
        List<ToppingType> toppings = pizza.getToppings();
        for (int i = 0; i < toppings.size(); i++) {
            if (toppings.get(i).isStockManage()) {
                if (stock.getOrDefault(toppings.get(i), 0) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void consume(PizzaTemplate pizza) {
        List<ToppingType> toppings = pizza.getToppings();
        for (int i = 0; i < toppings.size(); i++) {
            if (toppings.get(i).isStockManage()) {
                stock.put(toppings.get(i), stock.get(toppings.get(i)) - 1);
            }
        }
    }
    public void consume(ToppingType topping) {
        if (topping.isStockManage()) {
            stock.put(topping, stock.get(topping) - 1);
        }
    }

    public List<ToppingType> listAvailableToppings() {
        List<ToppingType> available = new ArrayList<>();
        for (ToppingType type : stock.keySet()) {
            if (type.isStockManage()) {
                if (stock.get(type) > 0) {
                    available.add(type);
                }
            } else {
                available.add(type);
            }
        }
        return available;
    }

    public int getStock(ToppingType topping) {
        return stock.getOrDefault(topping, 0);
    }

    public boolean checkStock(ToppingType topping) {
        return stock.get(topping) > 1;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("--- 재고 현황 ---\n");
        for (ToppingType type : stock.keySet()) {
            if (type.isStockManage()) {
                output.append(String.format("%-10s : %3d개 (단가 %5d원)%n",
                        type, stock.get(type), type.getCost()));
            } else {
                output.append(String.format("%-10s :   ∞   (단가 %5d원)%n",
                        type, type.getCost()));
            }
        }
        output.append("--------------------------\n");
        return output.toString();
    }
}
