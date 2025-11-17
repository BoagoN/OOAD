package controller;

import dao.TransactionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.sql.SQLException;
import java.util.Collection;

public class AdminController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerType;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, Integer> colAccountCount;

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> colAccNumber;
    @FXML private TableColumn<Account, String> colAccType;
    @FXML private TableColumn<Account, Double> colBalance;
    @FXML private TableColumn<Account, String> colBranch;

    @FXML private Label totalCustomersLabel;
    @FXML private Label totalAccountsLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label statusLabel;

    private Bank bank = new Bank();
    private TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    public void initialize() {
        setupCustomerTable();
        setupAccountTable();
        refreshData();
    }

    private void setupCustomerTable() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerType.setCellValueFactory(new PropertyValueFactory<>("customerType"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colName.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer instanceof IndividualCustomer) {
                return new javafx.beans.property.SimpleStringProperty(((IndividualCustomer) customer).getFullName());
            } else {
                return new javafx.beans.property.SimpleStringProperty(((CorporateCustomer) customer).getCompanyName());
            }
        });
        colAccountCount.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAccounts().size()).asObject()
        );

        // Add selection listener
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showCustomerAccounts(newSelection);
                    }
                });
    }

    private void setupAccountTable() {
        colAccNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colAccType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
    }

    private void showCustomerAccounts(Customer customer) {
        ObservableList<Account> accounts = FXCollections.observableArrayList(customer.getAccounts());
        accountTable.setItems(accounts);
    }

    @FXML
    private void refreshData() {
        try {
            bank.loadAllCustomers();
            Collection<Customer> allCustomers = bank.getAllCustomers();

            // Update customer table
            ObservableList<Customer> customers = FXCollections.observableArrayList(allCustomers);
            customerTable.setItems(customers);

            // Update statistics
            updateStatistics(allCustomers);

            statusLabel.setText("✅ Data refreshed successfully");

        } catch (SQLException e) {
            statusLabel.setText("❌ Error refreshing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics(Collection<Customer> customers) throws SQLException {
        int totalCustomers = customers.size();
        int totalAccounts = 0;
        double totalBalance = bank.getTotalBankBalance();

        for (Customer customer : customers) {
            totalAccounts += customer.getAccounts().size();
        }

        totalCustomersLabel.setText(String.valueOf(totalCustomers));
        totalAccountsLabel.setText(String.valueOf(totalAccounts));
        totalBalanceLabel.setText("P" + String.format("%.2f", totalBalance));
    }

    @FXML
    private void handlePayInterest() {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Pay Monthly Interest");
            confirmAlert.setHeaderText("Pay interest to all eligible accounts?");
            confirmAlert.setContentText("This will calculate and pay monthly interest to all Savings and Investment accounts.");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        bank.payMonthlyInterest();
                        statusLabel.setText("✅ Monthly interest paid successfully");
                        refreshData(); // Refresh to show updated balances

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Interest Payment Complete");
                        successAlert.setHeaderText("Monthly interest has been paid");
                        successAlert.setContentText("All eligible accounts have received their monthly interest.");
                        successAlert.showAndWait();

                    } catch (SQLException e) {
                        statusLabel.setText("❌ Error paying interest: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewCustomerDetails() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer first.");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Customer Details:\n\n");

        if (selectedCustomer instanceof IndividualCustomer) {
            IndividualCustomer ind = (IndividualCustomer) selectedCustomer;
            details.append("Type: Individual\n");
            details.append("Name: ").append(ind.getFullName()).append("\n");
            details.append("Email: ").append(ind.getEmail()).append("\n");
            details.append("Phone: ").append(ind.getCellphoneNumber()).append("\n");
        } else {
            CorporateCustomer corp = (CorporateCustomer) selectedCustomer;
            details.append("Type: Corporate\n");
            details.append("Company: ").append(corp.getCompanyName()).append("\n");
            details.append("Reg Number: ").append(corp.getRegistrationNumber()).append("\n");
            details.append("Email: ").append(corp.getEmail()).append("\n");
            details.append("Phone: ").append(corp.getTelephoneNumber()).append("\n");
            details.append("Address: ").append(corp.getAddress()).append("\n");
        }

        details.append("\nAccounts: ").append(selectedCustomer.getAccounts().size()).append("\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Details");
        alert.setHeaderText("Customer Information");
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Admin logout successful.");
        alert.showAndWait();

        // Close the admin window
        customerTable.getScene().getWindow().hide();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}