package org.example.topping;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtraToppingSelector {
    private final ToppingInventory toppingInventory;
    private int totalExtraPrice = 0;
    private int totalCountExtraToppings = 0;

    public ExtraToppingSelector(ToppingInventory toppingInventory) {
        this.toppingInventory = toppingInventory;
    }

    public boolean tryAddTopping(
            int selectedChoice,
            Map<ToppingType, Integer> baseRequired,
            Map<ToppingType, Integer> selectExtraTopping
            ) {
        if (selectedChoice < 0 || selectedChoice > toppingInventory.listAvailableToppings().size()) return false;
        ToppingType selected = toppingInventory.listAvailableToppings().get(selectedChoice);

        int totalStock = toppingInventory.getStock(selected);
        int baseNeed = baseRequired.getOrDefault(selected, 0);
        int alreadyReserved = selectExtraTopping.getOrDefault(selected, 0);

        if(selected.isStockManage() && totalStock - baseNeed - alreadyReserved <= 0) return false;

        selectExtraTopping.put(selected, selectExtraTopping.getOrDefault(selected, 0) + 1);
        totalCountExtraToppings++;
        totalExtraPrice += selected.getCost();
        return true;
    }

    public int getTotalExtraPrice() {
        return totalExtraPrice;
    }

    public int getAndResetTotalExtraPrices() {
        int totalPrice = totalExtraPrice;
        totalExtraPrice = 0;
        return totalPrice;
    }

    public int getAndResetTotalCountExtraToppings(){
        int totalTopping = totalCountExtraToppings;
        totalCountExtraToppings = 0;
        return totalTopping;
    }

}
