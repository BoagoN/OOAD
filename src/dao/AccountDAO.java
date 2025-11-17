package dao;

import model.*;
import model.interfaces.InterestEarning;
import model.interfaces.Withdrawable;
import dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, account_type, balance, branch, customer_id, " +
                "employer_name, employer_address, opening_deposit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getAccountType());
            stmt.setDouble(3, account.getBalance());
            stmt.setString(4, account.getBranch());
            stmt.setString(5, account.getCustomer().getCustomerId());

            if (account instanceof ChequeAccount) {
                ChequeAccount cheque = (ChequeAccount) account;
                stmt.setString(6, cheque.getEmployerName());
                stmt.setString(7, cheque.getEmployerAddress());
                stmt.setNull(8, Types.DOUBLE);
            } else if (account instanceof InvestmentAccount) {
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setDouble(8, account.getBalance()); // Opening deposit
            } else {
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.DOUBLE);
            }

            stmt.executeUpdate();
        }
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT a.*, c.customer_type, c.email, c.pin, c.first_name, c.last_name, c.cellphone, " + "c.company_name, c.registration_number, c.telephone, c.address " + "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id WHERE a.account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        }
        return null;
    }

    public List<Account> getAccountsByCustomerId(String customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql ="SELECT a.*, c.customer_type, c.email, c.pin, c.first_name, c.last_name, c.cellphone, " + "c.company_name, c.registration_number, c.telephone, c.address " + "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id " + "WHERE a.customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        return accounts;
    }

    public void updateAccountBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    public void deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {

        String customerType = rs.getString("customer_type");
        String customerId = rs.getString("customer_id");
        String email = rs.getString("email");
        String pin = rs.getString("pin");

        Customer customer;

        if ("INDIVIDUAL".equals(customerType)) {
            customer = new IndividualCustomer(customerId, email, pin, rs.getString("first_name"), rs.getString("last_name"), rs.getString("cellphone"));

        } else if ("CORPORATE".equals(customerType)) {
            customer = new CorporateCustomer(customerId, email, pin, rs.getString("company_name"), rs.getString("registration_number"), rs.getString("telephone"), rs.getString("address"));

        } else {
            throw new IllegalArgumentException("Unknown customer type: " + customerType);
        }

        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");

        switch (accountType) {
            case "Savings":
                SavingsAccount savings = new SavingsAccount(accountNumber, branch, customer);
                savings.deposit(balance);
                return savings;

            case "Cheque":
                ChequeAccount cheque = new ChequeAccount(
                        accountNumber, branch, customer,
                        rs.getString("employer_name"),
                        rs.getString("employer_address")
                );
                cheque.deposit(balance);
                return cheque;

            case "Investment":
                InvestmentAccount investment = new InvestmentAccount(
                        accountNumber, branch, customer,
                        rs.getDouble("opening_deposit")
                );
                if (investment.getBalance() != balance) {
                    // This handles subsequent deposits/withdrawals
                    double difference = balance - investment.getBalance();
                    if (difference > 0) {
                        investment.deposit(difference);
                    } else {
                        investment.withdraw(Math.abs(difference));
                    }
                }
                return investment;

            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }
}