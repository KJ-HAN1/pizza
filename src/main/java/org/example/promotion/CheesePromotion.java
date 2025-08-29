package org.example.promotion;

import org.example.Config;
import org.example.pizza.PizzaTemplate;
import org.example.topping.ToppingInventory;

import java.time.LocalDate;

import static org.example.topping.ToppingType.CHEESE;

public class CheesePromotion implements PromotionPolicy {
    private final LocalDate startPromotionDate;
    private final LocalDate endPromotionDate;

    public CheesePromotion() {
        this.startPromotionDate = Config.EVENT_START;
        this.endPromotionDate = startPromotionDate.plusMonths(1);
    }

    @Override
    public boolean isActive() {
        LocalDate now = LocalDate.now();

        return !now.isBefore(startPromotionDate) && !now.isAfter(endPromotionDate);
    }

    private boolean hasCheese(PizzaTemplate pizza) {
        return pizza.getToppings().contains(CHEESE);
    }

    // 치즈 토핑이 있고, 재고가 남아 있다면 50% 확률로 이벤트 발동
    @Override
    public void apply(PizzaTemplate pizza, ToppingInventory inventory) {
        if(isActive() && hasCheese(pizza) && inventory.checkStock(CHEESE)) {
            if (Math.random() < 0.5) {
                inventory.consume(CHEESE);
                System.out.println("🎇🎇 치즈💣 이벤트 당첨! 무료 치즈 토핑 추가! 🎇🎇");
            }
        }
    }

}
