package model;

import model.interfaces.Withdrawable;

public class ChequeAccount extends Account implements Withdrawable {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, String branch, Customer customer, String employerName, String employerAddress) {
        super (accountNumber, "Cheque", branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void withdraw(double amount) {
        if (amount >= balance)
            balance -= amount;
        else System.out.println("Insufficient Funds!");
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("Cheque Account: " + accountNumber + "Balance: P" + balance);
    }
}