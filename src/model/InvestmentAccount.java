package model;

import model.interfaces.Withdrawable;
import model.interfaces.InterestEarning;

public class InvestmentAccount extends Account implements Withdrawable, InterestEarning {
    private final double INTEREST_RATE = 0.05;
    private final double MIN_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, String branch, Customer customer, double initialDeposit) {
        super(accountNumber, "Investment", branch, customer);
        if (initialDeposit >= MIN_OPENING_DEPOSIT)
            this.balance = initialDeposit;
        else
            throw new IllegalArgumentException("Minimum opening balance is P500.00");
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient Funds! Current balance: P" + balance);
        }
        balance -= amount;
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Investment Account: " + accountNumber + "Balance: P" + balance);
    }
}