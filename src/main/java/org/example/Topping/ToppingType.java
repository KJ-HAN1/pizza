package org.example.Topping;

public enum ToppingType {
    CHEESE(3000,true),
    SAUCE(1000,false),
    DOUGH(500,true),
    PEPPERONI(3000,true),
    OLIVE(2000,true),
    HAM(3300,true),
    ONION(2500,true),
    TOMATO(2000,true),
    MEAT(3500,true);

    private final int cost;
    private final boolean stockManage;

    ToppingType(int cost, boolean stockManage) {
        this.cost = cost;
        this.stockManage =stockManage;
    }

    public int getCost() {
        return cost;
    }
    public boolean isStockManage() {return stockManage;}
}
