package org.example.promotion;

import org.example.Config;
import org.example.pizza.PizzaTemplate;
import org.example.topping.ToppingInventory;
import org.example.topping.ToppingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.example.topping.ToppingType.CHEESE;

@Component
public class CheesePromotion implements ToppingPromotionPolicy {
    private final LocalDate startPromotionDate;
    private final LocalDate endPromotionDate;
    private final float chance;

    public CheesePromotion() {
        this.startPromotionDate = Config.EVENT_START;
        this.endPromotionDate = startPromotionDate.plusMonths(1);
        this.chance = 0.5f;
    }

    @Override
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startPromotionDate) && !now.isAfter(endPromotionDate);
    }

    private boolean isCheesePromotionWon(){
        return Math.random() < chance;
    }

    private boolean canApply(PizzaTemplate pizza, ToppingInventory inventory) {
        return isActive() && pizza.getToppings().contains(CHEESE)
                && inventory.checkStock(CHEESE) && isCheesePromotionWon();
    }

    @Override
    public boolean shouldApply(PizzaTemplate pizza, ToppingInventory inventory) {
        return canApply(pizza, inventory);
    }

    @Override
    public ToppingType getRewardTopping() {
        return CHEESE;
    }

}
