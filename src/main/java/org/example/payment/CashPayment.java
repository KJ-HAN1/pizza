package org.example.payment;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;

@Component
public class CashPayment {
    private final CashManagement cashManagement;

    public CashPayment(CashManagement register) {
        this.cashManagement = register;
    }

    /*
     * 현금 결제 처리. 투입 금액을 레지스터에 적립하고,
     * 잔돈 계산·반환까지 수행.
     */
    public boolean pay(int amountDue) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("현금을 투입하세요 (필요 금액: %d원): ", amountDue);
        int inserted = scanner.nextInt();

        if(inserted < 0){
            System.out.println("올바른 금액을 투입하세요.");
            return false;
        }
        if (inserted < amountDue) {
            System.out.println("투입 금액이 부족합니다.");
            return false;
        }

        int change = inserted - amountDue;
        Map<Integer,Integer> changeMap = null;
        if (change > 0) {
            changeMap = cashManagement.returnChange(change);
            if (changeMap == null) {
                System.out.println("잔돈부족으로 결제 취소, 현금 반환됨.");
                return false;
            }
        }

        // 기기에 투입 금액 적립
        cashManagement.addAmount(inserted);

        if (change > 0) {
            System.out.printf("결제 완료. 잔돈 %d원 반환: %s%n", change, changeMap);
        } else {
            System.out.println("결제 완료");
        }
        return true;
    }
}