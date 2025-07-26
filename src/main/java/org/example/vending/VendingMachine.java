package org.example.vending;

import org.example.model.Topping.ToppingInventory;
import org.example.model.payment.Cash;
import org.example.model.payment.CashRegister;
import org.example.model.payment.CreditCard;
import org.example.model.payment.Pay;
import org.example.model.pizza.*;

import java.util.List;
import java.util.Scanner;

public class VendingMachine {
    private static final String ADMIN_PW = "1234";
    private int totalSalesCount = 0;
    private int totalSalesAmount = 0;

    private final List<Pizza> menu = List.of(
            new Cheeze(), new Pepperoni(), new Combination()
    );
    private final ToppingInventory inventory = new ToppingInventory();
    private final CashRegister cashRegister = new CashRegister();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n----- 피자 자판기 ------");
            System.out.println("1) 치즈  2) 페퍼로니  3) 콤비네이션");
            System.out.println("4) 관리자 모드  0) 종료");
            System.out.print("선택> ");
            int choice = scanner.nextInt();

            if (choice == 0) break;
            if (choice < 1 || choice > 3) continue;
            if (choice == 4) {
                enterAdminMode();
                continue;
            }

            Pizza selected = menu.get(choice-1);
            if (!inventory.hasStock(selected)) {
                System.out.println("재고가 부족합니다.");
                continue;
            }

            System.out.print("1) 현금  2) 카드> ");
            int payType = scanner.nextInt();
            Pay pay = (payType == 1) ? new Cash(cashRegister): new CreditCard();

            if (!pay.pay(selected.getPrice())) {
                System.out.println("결제 실패.");
                continue;
            }

            inventory.consume(selected);
            totalSalesCount++;
            totalSalesAmount += selected.getPrice();

            inventory.consume(selected);
            System.out.printf("%s 나왔습니다.%n", selected.getName());
        }
        System.out.println("감사합니다.");
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
}