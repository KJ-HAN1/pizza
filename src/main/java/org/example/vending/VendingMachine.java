package org.example.vending;

import org.example.Config;
import org.example.Topping.ToppingInventory;
import org.example.Topping.ToppingType;
import org.example.payment.Cash;
import org.example.payment.CashRegister;
import org.example.pizza.Pizza;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {
    private static final String ADMIN_PW = Config.ADMIN_PW;
    private static final LocalDate EVENT_START = Config.EVENT_START;
    private int totalSalesCount = 0;
    private int totalSalesAmount = 0;

    private final List<Pizza> menu;
    private final ToppingInventory inventory;
    private final CashRegister cashRegister;
    private final Scanner scanner;

    public VendingMachine(List<Pizza> menu, ToppingInventory inventory, CashRegister cashRegister, Scanner scanner) {
        this.menu = menu;
        this.inventory = inventory;
        this.cashRegister = cashRegister;
        this.scanner = scanner;
    }

    private void printMenu() {
        System.out.println("\n----- 피자 자판기 ------");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i + 1) + ") " + menu.get(i).getName() + " : " + menu.get(i).getPrice() + "$");
        }
        System.out.println("0) 종료\n99) 관리자 모드\n선택> ");
    }

    //이벤트 진행 현재일 기준으로 날짜 계산
    private boolean eventPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        LocalDate endEvent = EVENT_START.plusMonths(1);

        return !now.isBefore(EVENT_START) && !now.isAfter(endEvent);
    }

    private void processOrder(Pizza selected) {
        if (!inventory.hasStock(selected)) {
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
            
            List<ToppingType> availableToppings = inventory.listAvailableToppings();
            for (int i = 0; i < availableToppings.size(); i++) {
                /*
                * totalStock : 총 수량
                * baseNeed : 선택한 피자 기본 토핑
                * alreadyReserved : 동일한 추가 토핑 추가
                * availableForExtra : 총 수량에서 추가한거 뺀거 수량
                * */
                int totalStock = inventory.getStock(availableToppings.get(i));
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
                int totalStock = inventory.getStock(chosen);
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

        Cash pay = new Cash(cashRegister);
        if (!pay.pay(totalPrice)) {
            System.out.println("결제 실패.");
            return;
        }

        inventory.consume(selected);

        //결제 성공시 재고 차감
        ToppingType[] keys = extras.keySet().toArray(new ToppingType[0]);
        for (int i = 0; i < keys.length; i++) {
            int cnt = extras.get(keys[i]);
            for (int j = 0; j < cnt; j++) {
                inventory.consume(keys[i]);
            }
        }

        // 치즈폭탄 이벤트 50프로 확률로 치즈 추가
        if (eventPeriod()) {
            System.out.println("이벤트 중임 ㅇㅇㅇㅇㅇㅇㅇㅇ");
            int baseCheeseCount = 0;
            for (int i = 0; i < baseToppings.size(); i++) {
                if (baseToppings.get(i) == ToppingType.CHEESE) baseCheeseCount++;
            }

            int extraCheeseCount = extras.getOrDefault(ToppingType.CHEESE, 0);
            int totalCheeseUsed = baseCheeseCount + extraCheeseCount;

            int remainingCheeseStock = inventory.getStock(ToppingType.CHEESE);

            // 피자에 치즈가 있고, 추가토핑까지 포함해 치즈 재고 1개 이상이면 이벤트 가능
            if (totalCheeseUsed >= 1 && remainingCheeseStock >= 1) {
                if (Math.random() < 0.5) {
                    inventory.consume(ToppingType.CHEESE);
                    System.out.println("🎇🎇🎇치즈💣 이벤트 당첨! 무료 치즈 토핑 추가!🎇🎇🎇");
                }
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
        System.out.println(cashRegister);
        System.out.println(inventory);
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

            Pizza selected = menu.get(choice - 1);
            processOrder(selected);
        }
        System.out.println("감사합니다.");
    }
}