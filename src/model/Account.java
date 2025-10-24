package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String accountType;
    protected double balance;
    public String branch;
    protected Customer customer;
    protected List<Transaction> transactions = new ArrayList<>();


    public Account(String accountNumber, String accountType, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.00;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getBranch() {
        return branch;
    }

    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Insufficient amount");
            balance += amount;
    }

    public double getBalance() {
        return balance;
    }


    public abstract void displayAccountDetails();


}