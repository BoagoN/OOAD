package dao;

import model.Customer;
import model.IndividualCustomer;
import model.CorporateCustomer;
import dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, email, pin, customer_type, first_name, last_name, cellphone, " +
                "company_name, registration_number, telephone, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPin());

            if (customer instanceof IndividualCustomer) {
                IndividualCustomer ind = (IndividualCustomer) customer;
                stmt.setString(4, "INDIVIDUAL");
                stmt.setString(5, ind.getFirstName());
                stmt.setString(6, ind.getLastName());
                stmt.setString(7, ind.getCellphoneNumber());
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
            } else if (customer instanceof CorporateCustomer) {
                CorporateCustomer corp = (CorporateCustomer) customer;
                stmt.setString(4, "CORPORATE");
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setString(8, corp.getCompanyName());
                stmt.setString(9, corp.getRegistrationNumber());
                stmt.setString(10, corp.getTelephoneNumber());
                stmt.setString(11, corp.getAddress());
            }

            stmt.executeUpdate();
        }
    }

    public Customer getCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public Customer getCustomerById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        String customerType = rs.getString("customer_type");
        String customerId = rs.getString("customer_id");
        String email = rs.getString("email");
        String pin = rs.getString("pin");

        if ("INDIVIDUAL".equals(customerType)) {
            return new IndividualCustomer(
                    customerId, email, pin,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("cellphone")
            );
        } else if ("CORPORATE".equals(customerType)) {
            return new CorporateCustomer(
                    customerId, email, pin,
                    rs.getString("company_name"),
                    rs.getString("registration_number"),
                    rs.getString("telephone"),
                    rs.getString("address")
            );
        }
        return null;
    }
}