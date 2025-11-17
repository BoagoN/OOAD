package model;

import model.interfaces.InterestEarning;

public class SavingsAccount extends Account implements InterestEarning {
    private final double INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, "Savings", branch, customer);
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println(accountType + "Account: " + accountNumber + "Balance: " + balance);
    }
}