package org.example.model.payment;

import java.util.Map;
import java.util.Scanner;

public class Cash implements Pay {
    private final CashRegister register;

    public Cash(CashRegister register) {
        this.register = register;
    }

    /*
     * 현금 결제 처리. 투입 금액을 레지스터에 적립하고,
     * 잔돈 계산·반환까지 수행.
     */
    @Override
    public boolean pay(int amountDue) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("현금을 투입하세요 (필요 금액: %d원): ", amountDue);
        int inserted = scanner.nextInt();

        if (inserted < amountDue) {
            System.out.println("투입 금액이 부족합니다.");
            return false;
        }

        // 기기에 먼저 투입 금액 적립
        register.addAmount(inserted);

        int change = inserted - amountDue;
        if (change > 0) {
            if (!register.hasSufficientChange(change)) {
                System.out.println("자판기에 잔돈이 부족합니다. 현금 반환 후 결제 취소됩니다.");
                return false;
            }
            Map<Integer,Integer> changeMap = register.dispenseChange(change);
            System.out.printf("결제 완료. 잔돈 %d원 반환: %s%n",
                    change, changeMap.toString());
        } else {
            System.out.println("결제 완료");
        }
        return true;
    }
}