package model;

public abstract class Account {
    protected String accountNumber;
    protected String accountType;
    protected double balance;
    protected String branch;
    protected Customer customer;

    public Account(String accountNumber, String accountType, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.00;
    }

    public String getAccountType() {
        return accountType;
    }

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public abstract void displayAccountDetails();
}