package model;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private List<Customer> customers;
    private List<Account> accounts;

    public Bank() {
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public void registerCustomer(Customer c) {
        if (c != null && !customers.contains(c)) {
            customers.add(c);
            System.out.println("Customer registerd: " + c.getCustomerId());
        }
    }

    public void addAccount(Account a) {
        if (a != null && !accounts.contains(a)) {
            accounts.add(a);
            System.out.println("Account added: " + a.getAccountNumber());
        }
    }

    public Account findAccount(String n) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(n)) {
                return account;
            }
        }
        return null;
    }

    public void payInterest() {
        int interestPaidCount = 0;
        for (Account account: accounts) {
            if (account instanceof Interest interestAccount) {
                System.out.println("Paying interest at rate: " + (interestAccount.getMonthlyInterestRate() * 100) + "% on account: " + account.getAccountNumber());
                interestAccount.calculateInterest();
                interestPaidCount++;
            }
        }
        System.out.println("Interest paid to " + interestPaidCount + "interest-bearing accounts");
    }

        public Customer authenticateCustomer(String customerId, String pin) {
           for (Customer customer : customers) {
               if (customer.getCustomerId().equals(customerId) && customer.authenticate(pin)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomer(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getCustomer().getCustomerId().equals(customerId)) {
                customerAccounts.add(account);
            }
        }
        return customerAccounts;
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    @Override
    public String toString() {
        return "Bank{" + "customers=" + customers.size() + ", accounts=" + accounts.size() + '}';

    }
}