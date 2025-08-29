package org.example.vending;

import org.example.Config;
import org.example.promotion.ToppingPromotionPolicy;
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
    private final ToppingInventory toppingInventory;
    private final CashManagement cashManagement;
    private final Scanner scanner;
    private final List<ToppingPromotionPolicy> promotions;


    public PizzaMachine(List<PizzaTemplate> menu, ToppingInventory inventory,
                        CashManagement cashRegister, Scanner scanner, List<ToppingPromotionPolicy> promotions) {
        this.menu = menu;
        this.toppingInventory = inventory;
        this.cashManagement = cashRegister;
        this.scanner = scanner;
        this.promotions = promotions;
    }

    private void printMenu() {
        System.out.println("\n----- 피자 자판기 ------");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i + 1) + ") " + menu.get(i).getName() + " : " + menu.get(i).getPrice() + "$");
        }
        System.out.println("0) 종료\n99) 관리자 모드\n선택> ");
    }


    private void processOrder(PizzaTemplate selected) {
        if (!toppingInventory.hasStock(selected)) {
            System.out.println("재고가 부족합니다.");
            return;
        }

        //토핑추가를 위한 총금액
        int totalPrice = selected.getPrice();

        //토핑추가에 재고 계산용
        Map<ToppingType, Integer> baseRequired = new EnumMap<>(ToppingType.class);
        List<ToppingType> baseToppings = selected.getToppings();
        for (int i = 0; i < baseToppings.size(); i++) {
            if (baseToppings.get(i).isStockManage()) {
                baseRequired.put(baseToppings.get(i), baseRequired.getOrDefault(baseToppings.get(i), 0) + 1);
            }
        }

        Map<ToppingType, Integer> extras = new EnumMap<>(ToppingType.class);

        while (true) {
            System.out.println("\n1) 토핑 추가  2) 결제");
            int toppingChoice = scanner.nextInt();
            if (toppingChoice == 2) break;
            if (toppingChoice != 1) {
                System.out.println("잘못된 선택입니다.");
                continue;
            }
            System.out.println("추가할 토핑 선택:");
            
            List<ToppingType> availableToppings = toppingInventory.listAvailableToppings();
            for (int i = 0; i < availableToppings.size(); i++) {
                /*
                * totalStock : 총 수량
                * baseNeed : 선택한 피자 기본 토핑
                * alreadyReserved : 동일한 추가 토핑 추가
                * availableForExtra : 총 수량에서 추가한거 뺀거 수량
                * */
                int totalStock = toppingInventory.getStock(availableToppings.get(i));
                int baseNeed = baseRequired.getOrDefault(availableToppings.get(i), 0);
                int alreadyReserved = extras.getOrDefault(availableToppings.get(i), 0);
                int availableForExtra = availableToppings.get(i).isStockManage() ? Math.max(0, totalStock - baseNeed - alreadyReserved) : Integer.MAX_VALUE;
                String availText = availableToppings.get(i).isStockManage() ? (availableForExtra + "개 남음") : "  ∞  ";
                System.out.printf("%d) %s (단가 %d원, %s)%n", i + 1, availableToppings.get(i), availableToppings.get(i).getCost(), availText);
            }
            System.out.println("0) 토핑 선택 종료");

            //토핑 선택
            int choiceTopping = scanner.nextInt() - 1;
            if (choiceTopping == -1) continue;
            if (choiceTopping < 0 || choiceTopping >= availableToppings.size()) {
                System.out.println("올바른 메뉴를 선택하세요.");
                continue;
            }

            ToppingType chosen = availableToppings.get(choiceTopping);

            if (chosen.isStockManage()) {
                int totalStock = toppingInventory.getStock(chosen);
                int baseNeed = baseRequired.getOrDefault(chosen, 0);
                int alreadyReserved = extras.getOrDefault(chosen, 0);

                if (totalStock - baseNeed - alreadyReserved <= 0) {
                    System.out.println("재고 부족: 해당 토핑을 추가할 수 없습니다.");
                    continue;
                }
            }

            extras.put(chosen, extras.getOrDefault(chosen, 0) + 1);
            totalPrice += chosen.getCost();

            System.out.printf("%s 토핑 추가 예약 완료 (가격 +%d원)\n", chosen, chosen.getCost());
        }

        CashPayment pay = new CashPayment(cashManagement);
        if (!pay.pay(totalPrice)) {
            System.out.println("결제 실패.");
            return;
        }

        toppingInventory.consume(selected);

        //결제 성공시 재고 차감
        ToppingType[] keys = extras.keySet().toArray(new ToppingType[0]);
        for (int i = 0; i < keys.length; i++) {
            int cnt = extras.get(keys[i]);
            for (int j = 0; j < cnt; j++) {
                toppingInventory.consume(keys[i]);
            }
        }

        for (ToppingPromotionPolicy promotion : promotions) {
            if(promotion.shouldApply(selected, toppingInventory)) {
                toppingInventory.consume(promotion.getRewardTopping());
                System.out.printf("🎇🎇 %s💣 이벤트 당첨! 무료 치즈 토핑 추가! 🎇🎇\n",promotion.getRewardTopping());
            }
        }

        totalSalesCount++;
        totalSalesAmount += totalPrice;

        int extraCount = 0;
        ToppingType[] extrasKeys = extras.keySet().toArray(new ToppingType[0]);
        for (int i = 0; i < extrasKeys.length; i++) {
            extraCount += extras.get(extrasKeys[i]);
        }

        System.out.printf("%s 나왔습니다. 총 결제금액: %d원 (추가 토핑 %d개)%n",
                selected.getName(), totalPrice, extraCount);
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