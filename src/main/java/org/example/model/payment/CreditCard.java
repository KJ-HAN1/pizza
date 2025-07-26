package org.example.model.payment;

public class CreditCard implements Pay{
    @Override
    public boolean pay(int amountDue) {
        System.out.printf("신용카드로 %d원 결제 중... 승인되었습니다.%n", amountDue);
        return true;
    }
}
