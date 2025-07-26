/*
* 현재 현금/카드 결재만 지원
* 다양한 결재 제공을 위해 인터페이스로 확장 열어둠
*/
package org.example.model.payment;

public interface Pay {
    boolean pay(int amount);
}
