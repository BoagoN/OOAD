package model;

import java.time.LocalDateTime;


public class Transaction {
    private String transactionType; // Deposit, Withdrawal, Interest
    private double amount;
    private LocalDateTime timestamp;
    private String accountNumber;

    public Transaction(String transactionType, double amount, String accountNumber) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + transactionType +
                " of P" + amount + " on Account " + accountNumber;
    }
}