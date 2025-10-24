package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected String customerId;
    protected String email;
    protected String pin;
    protected List<Account> accounts;

    public Customer(String customerId, String email, String pin) {
        this.customerId = customerId;
        this.email = email;
        this.pin = pin;
        this.accounts = new ArrayList<>();
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public void changePin(String currentPin, String newPin) {
        if (this.pin.equals(currentPin)) {
            this.pin = newPin;
            System.out.println("PIN changed successfully.");
        } else {
            System.out.println("Incorrect PIN, please tyr again.");
        }
    }

    public void openAccount(Account account) {
        accounts.add(account);
    }

    public Account getAccounts(String accountNumber) {
        for (Account account: accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void deposit(String accountNumber, double amount) {
        Account account = getAccounts(accountNumber);
        if (account != null) {
            account.deposit(amount);
            System.out.println("Deposit successful. New Balance: P" + account.getBalance());
        } else {
            System.out.println("Account not found. Deposit failed");
        }
    }

    public void withdraw (String accountNumber, double amount) {
        Account account = getAccounts(accountNumber);
        if (account != null) {
            if (account instanceof model.interfaces.Withdrawable) {
                ((model.interfaces.Withdrawable) account).withdraw(amount);
            } else {
                System.out.println("Withdrawals not allowed on this account type.");
            }

        } else {
            System.out.println("Account not found.");
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected void setPin(String newPin) {
        this.pin = newPin;
    }

    public abstract void displayCustomerDetails();
}