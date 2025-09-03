package org.example;

import org.example.promotion.ToppingPromotionPolicy;
import org.example.topping.ExtraToppingSelector;
import org.example.topping.ToppingInventory;
import org.example.payment.CashManagement;
import org.example.pizza.*;
import org.example.vending.PizzaMachine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        List<PizzaTemplate> menu = List.of(new CheesePizza(), new PepperoniPizza(), new CombinationPizza());
        List<ToppingPromotionPolicy> promotions = new ArrayList<>();
        ToppingInventory inventory = new ToppingInventory();
        CashManagement cashRegister = new CashManagement();
        ExtraToppingSelector extraToppingSelector = new ExtraToppingSelector(inventory);
        Scanner scanner = new Scanner(System.in);


        PizzaMachine vm = new PizzaMachine(menu, promotions, inventory, cashRegister, extraToppingSelector, scanner);
        vm.run();
    }
}