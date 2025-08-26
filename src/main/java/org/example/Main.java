package org.example;

import org.example.promotion.Promotion;
import org.example.topping.ToppingInventory;
import org.example.payment.CashRegister;
import org.example.pizza.*;
import org.example.vending.VendingMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Pizza> menu = List.of(new Cheese(), new Pepperoni(), new Combination());
        ToppingInventory inventory = new ToppingInventory();
        CashRegister cashRegister = new CashRegister();
        Scanner scanner = new Scanner(System.in);
        List<Promotion> promotions = new ArrayList<>();

        VendingMachine vm = new VendingMachine(menu, inventory, cashRegister, scanner, promotions);
        vm.run();
    }
}