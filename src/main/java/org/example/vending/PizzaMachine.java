package org.example.vending;

import org.example.Config;
import org.example.promotion.ToppingPromotionPolicy;
import org.example.topping.ExtraToppingSelector;
import org.example.topping.ToppingInventory;
import org.example.topping.ToppingType;
import org.example.payment.CashPayment;
import org.example.payment.CashManagement;
import org.example.pizza.PizzaTemplate;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class PizzaMachine {
    private static final String ADMIN_PW = Config.ADMIN_PW;
    private int totalSalesCount = 0;
    private int totalSalesAmount = 0;

    private final List<PizzaTemplate> menu;
    private final List<ToppingPromotionPolicy> promotions;
    private final ToppingInventory toppingInventory;
    private final CashManagement cashManagement;
    private final ExtraToppingSelector extraToppingSelector;
    private final Scanner scanner;


    public PizzaMachine(
            List<PizzaTemplate> menu,
            List<ToppingPromotionPolicy> promotions,
            ToppingInventory inventory,
            CashManagement cashRegister,
            ExtraToppingSelector extraToppingSelector,
            Scanner scanner
            ) {
        this.menu = menu;
        this.promotions = promotions;
        this.toppingInventory = inventory;
        this.cashManagement = cashRegister;
        this.extraToppingSelector = extraToppingSelector;
        this.scanner = scanner;
    }

    private void enterAdminMode() {
        System.out.print("관리자 비밀번호 입력> ");
        String pw = scanner.next();
        if (!ADMIN_PW.equals(pw)) {
            System.out.println("비밀번호 오류.");
            return;
        }
        System.out.println("관리자 모드 접근");
        System.out.println(cashManagement);
        System.out.println(toppingInventory);
        System.out.printf("총 판매 개수 : %d개%n", totalSalesCount);
        System.out.printf("총 판매액     : %d원%n", totalSalesAmount);
    }

    private void printMenu() {
        System.out.println("\n----- 피자 자판기 ------");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i + 1) + ") " + menu.get(i).getName() + " : " + menu.get(i).getPrice() + "$");
        }
        System.out.println("0) 종료\n99) 관리자 모드\n선택> ");
    }

    private void printAvailableToppings(
            List<ToppingType> availableToppings,
            Map<ToppingType, Integer> baseRequired,
            Map<ToppingType, Integer> extras)
    {
        for (int i = 0; i < availableToppings.size(); i++) {
            ToppingType topping = availableToppings.get(i);

            int totalStock = toppingInventory.getStock(topping);
            int baseNeed = baseRequired.getOrDefault(topping, 0);
            int alreadyReserved = extras.getOrDefault(topping, 0);
            int availableForExtra;
            if (topping.isStockManage()) {
                availableForExtra = Math.max(0, totalStock - baseNeed - alreadyReserved);
            } else {
                availableForExtra = Integer.MAX_VALUE; // 무제한 토핑
            }

            String stockText = topping.isStockManage() ? (availableForExtra + "개 남음") : "  ∞  ";
            System.out.printf("%d) %s (단가 %d원, %s)\n", i + 1, topping, topping.getCost(), stockText);
        }
        System.out.println("0) 토핑 선택 종료");
    }


    private void processOrder(PizzaTemplate selected) {
        if (!toppingInventory.hasStock(selected)) {
            System.out.println("재고가 부족합니다.");
            return;
        }

        //토핑추가에 재고 계산용
        Map<ToppingType, Integer> baseRequired = new EnumMap<>(ToppingType.class);
        for (ToppingType baseTopping : selected.getToppings()) {
            if (baseTopping.isStockManage()) {
                baseRequired.put(baseTopping, baseRequired.getOrDefault(baseTopping, 0) + 1);
            }
        }

        // 토핑 추가 선택
        Map<ToppingType, Integer> selectExtraTopping = new EnumMap<>(ToppingType.class);

        while (true) {
            System.out.println("\n1) 토핑 추가  2) 결제");
            int toppingChoice = scanner.nextInt();
            if (toppingChoice == 2) break;
            if (toppingChoice != 1) {
                System.out.println("잘못된 선택입니다.");
                continue;
            }
            printAvailableToppings(toppingInventory.listAvailableToppings(), baseRequired, selectExtraTopping);

            //토핑 선택
            System.out.println("추가할 토핑 선택:");
            int choiceTopping = scanner.nextInt() - 1;
            if (choiceTopping == -1) continue;
            if (choiceTopping < 0 || choiceTopping >= toppingInventory.listAvailableToppings().size()) {
                System.out.println("올바른 메뉴를 선택하세요.");
                continue;
            }

            if(!extraToppingSelector.tryAddTopping(choiceTopping, baseRequired, selectExtraTopping)) {
                System.out.println("재고 부족: 해당 토핑을 추가할 수 없습니다.");
                continue;
            }

            ToppingType chosen = toppingInventory.listAvailableToppings().get(choiceTopping);
            System.out.printf("%s 토핑 추가 예약 완료 (가격 +%d원)\n", chosen, chosen.getCost());
        }

        CashPayment pay = new CashPayment(cashManagement);
        if (!pay.pay(selected.getPrice()+ extraToppingSelector.getTotalExtraPrice())) {
            System.out.println("결제 실패.");
            return;
        }

        toppingInventory.consume(selected);

        //결제 성공시 재고 차감
        ToppingType[] keys = selectExtraTopping.keySet().toArray(new ToppingType[0]);
        for (ToppingType key : keys) {
            int cnt = selectExtraTopping.get(key);
            for (int j = 0; j < cnt; j++) {
                toppingInventory.consume(key);
            }
        }

        for (ToppingPromotionPolicy promotion : promotions) {
            if(promotion.shouldApply(selected, toppingInventory)) {
                toppingInventory.consume(promotion.getRewardTopping());
                System.out.printf("🎇🎇 %s💣 이벤트 당첨! 무료 치즈 토핑 추가! 🎇🎇\n",promotion.getRewardTopping());
            }
        }

        totalSalesCount++;
        totalSalesAmount += selected.getPrice()+ extraToppingSelector.getTotalExtraPrice();

        System.out.printf(
            "%s 나왔습니다. 총 결제금액: %d원 (추가 토핑 %d개)%n",
            selected.getName(),
            selected.getPrice()+extraToppingSelector.getAndResetTotalExtraPrices(),
            extraToppingSelector.getAndResetTotalCountExtraToppings()
        );
    }

    public void run() {
        while (true) {
            printMenu();
            int choice = scanner.nextInt();

            if (choice == 0) break;
            if (choice == 99) {
                enterAdminMode();
                continue;
            }
            if (choice < 1 || choice > menu.size()) {
                System.out.println("잘못된 입력입니다.");
                continue;
            }

            PizzaTemplate selected = menu.get(choice - 1);
            processOrder(selected);
        }
        System.out.println("감사합니다.");
    }
}