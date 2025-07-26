package org.example.model.Topping;

public enum ToppingType {
    CHEESE(3000),
    SAUCE(1000),
    DOUGH(500),
    PEPPERONI(3000),
    OLIVE(2000),
    HAM(3300),
    ONION(2500),
    TOMATO(2000),
    MEAT(3500);


    private final int cost;

    ToppingType(int cost) {
        this.cost = cost;
    }
    public int getCost() {
        return cost;
    }
}
