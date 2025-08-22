package org.example.payment;


import java.util.*;

/**
 * 자판기 내부에 준비된 현금(권종별 지폐/동전) 재고와
 * 잔돈 계산·지급 로직을 담당.
 */
public class CashRegister {
    private static final List<Integer> MONEYTYPE =
            List.of(10000, 5000, 1000, 500, 100);

    private final Map<Integer, Integer> stock = new TreeMap<>(Comparator.reverseOrder());

    public CashRegister() {
        //초기 재고는 종류별로 10개씩 채움
        for (int i = 0; i < MONEYTYPE.size(); i++) {
            stock.put(MONEYTYPE.get(i), 10);
        }
    }

    public void addAmount(int amount) {
        for (int i = 0; i < MONEYTYPE.size(); i++) {
            int count = amount / MONEYTYPE.get(i);
            if (count > 0) {
                stock.put(MONEYTYPE.get(i), stock.get(MONEYTYPE.get(i)) + count);
                amount -= MONEYTYPE.get(i) * count;
            }
        }
    }

    /**
     * 잔돈을 계산하여 (권종별 개수 맵) 반환.
     * 계산 후 재고에서 차감.
     */
    public Map<Integer,Integer> returnChange(int changeAmount) {
        Map<Integer,Integer> result = new LinkedHashMap<>();
        //잔돈 반환이 가능한지 check
        Map<Integer,Integer> tempStock = new HashMap<>(stock);

        for (int i = 0; i < MONEYTYPE.size(); i++) {
            int avail = tempStock.get(MONEYTYPE.get(i));
            int use = Math.min(avail, changeAmount / MONEYTYPE.get(i));
            if (use > 0) {
                tempStock.put(MONEYTYPE.get(i), avail - use);
                result.put(MONEYTYPE.get(i), use);
                changeAmount -= use * MONEYTYPE.get(i);
            }
        }
        if (changeAmount > 0) {
            return null;
        }
        stock.putAll(tempStock);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- Cash Register ---\n");
        List<Integer> denominations = new ArrayList<>(stock.keySet());
        for (int i = 0; i < denominations.size(); i++) {
            int denomination = denominations.get(i);
            int count = stock.get(denomination);
            sb.append(String.format("%6d원 : %2d개%n", denomination, count));
        }
        sb.append("-------------------------------\n");
        return sb.toString();
    }
}