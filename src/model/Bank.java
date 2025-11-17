package model;

import dao.CustomerDAO;
import dao.AccountDAO;
import model.interfaces.InterestEarning;

import java.sql.SQLException;
import java.util.*;

public class Bank {
    private Map<String, Customer> customers = new HashMap<>();
    private CustomerDAO customerDAO = new CustomerDAO();
    private AccountDAO accountDAO = new AccountDAO();


    public void loadAllCustomers() throws SQLException {
        customers.clear();
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        for (Customer customer : allCustomers) {
            customers.put(customer.getEmail(), customer);

            List<Account> customerAccounts = accountDAO.getAccountsByCustomerId(customer.getCustomerId());
            for (Account account : customerAccounts) {
                customer.openAccount(account);
            }
        }
    }

    public void registerCustomer(Customer customer) throws SQLException {
        customerDAO.addCustomer(customer);
        customers.put(customer.getEmail(), customer);
    }

    public Customer authenticateCustomer(String email, String pin) {
        try {
            Customer c = customerDAO.getCustomerByEmail(email);
            if (c != null && c.validatePin(pin)) {
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean emailExists(String email) {
        try {
            return customerDAO.emailExists(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<Customer> getAllCustomers() {
        try {
            loadAllCustomers(); // Refresh from database
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers.values();
    }

    public void payMonthlyInterest() throws SQLException {
        List<Account> allAccounts = getAllAccounts();
        int interestPaidCount = 0;

        for (Account account : allAccounts) {
            if (account instanceof model.interfaces.InterestEarning) {
                InterestEarning interestAccount = (InterestEarning) account;
                double interest = interestAccount.calculateInterest();

                if (interest > 0) {
                    account.deposit(interest);
                    accountDAO.updateAccountBalance(account.getAccountNumber(), account.getBalance());
                    interestPaidCount++;
                    System.out.println("Paid interest of P" + interest + " to " + account.getAccountNumber());
                }
            }
        }
        System.out.println("Interest payment completed. Paid to " + interestPaidCount + " accounts.");
    }

    private List<Account> getAllAccounts() throws SQLException {
        List<Account> allAccounts = new ArrayList<>();
        for (Customer customer : getAllCustomers()) {
            allAccounts.addAll(customer.getAccounts());
        }
        return allAccounts;
    }
}