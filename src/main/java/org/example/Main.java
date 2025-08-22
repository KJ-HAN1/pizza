package org.example;

import org.example.Topping.ToppingInventory;
import org.example.payment.CashRegister;
import org.example.pizza.*;
import org.example.vending.VendingMachine;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Pizza> menu = List.of(new Cheeze(), new Pepperoni(), new Combination());
        ToppingInventory inventory = new ToppingInventory();
        CashRegister cashRegister = new CashRegister();
        Scanner scanner = new Scanner(System.in);

        VendingMachine vm = new VendingMachine(menu, inventory, cashRegister, scanner);
        vm.run();
    }
}