package org.example;

import org.example.promotion.PromotionPolicy;
import org.example.topping.ToppingInventory;
import org.example.payment.CashManagement;
import org.example.pizza.*;
import org.example.vending.PizzaMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<PizzaTemplate> menu = List.of(new CheesePizza(), new PepperoniPizza(), new CombinationPizza());
        ToppingInventory inventory = new ToppingInventory();
        CashManagement cashRegister = new CashManagement();
        Scanner scanner = new Scanner(System.in);
        List<PromotionPolicy> promotions = new ArrayList<>();

        PizzaMachine vm = new PizzaMachine(menu, inventory, cashRegister, scanner, promotions);
        vm.run();
    }
}