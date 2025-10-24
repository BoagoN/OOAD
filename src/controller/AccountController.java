package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import model.interfaces.Withdrawable;

import java.util.*;

public class AccountController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> colAccountNumber;
    @FXML private TableColumn<Account, String> colType;
    @FXML private TableColumn<Account, Double> colBalance;
    @FXML private TableColumn<Account, String> colBranch;

    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField branchField;
    @FXML private TextField initialDepositField;
    @FXML private Label accountMessageLabel;

    @FXML private HBox employerFields;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;


    private Customer loggedInCustomer;
    private LoginController loginController;

    @FXML
    public void initialize() {
        accountTypeCombo.getItems().addAll("Savings", "Cheque", "Investment");

        colAccountNumber.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountNumber()));
        colType.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountType()));
        colBalance.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getBalance()));
        colBranch.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBranch()));


            accountTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (employerFields != null) {
                    boolean isCheque = "Cheque".equals(newVal);
                    employerFields.setVisible(isCheque);
                    employerFields.setManaged(isCheque);
                }
            });
    }

    public void setLoggedInCustomer(Customer customer) {
        this.loggedInCustomer = customer;
        String name = (customer instanceof IndividualCustomer)
                ? ((IndividualCustomer) customer).getFullName()
                : ((CorporateCustomer) customer).getCompanyName();
        welcomeLabel.setText("Welcome, " + name + "!");
        refreshAccountTable();
    }

    public void setLoginController(LoginController controller) {
        this.loginController = controller;
    }

    @FXML
    private void handleOpenAccount() {
        if (loggedInCustomer == null) {
            accountMessageLabel.setText("Please log in first.");
            return;
        }

        String type = accountTypeCombo.getValue();
        String branch = branchField.getText();
        String initialText = initialDepositField.getText();

        if (type == null || branch.isEmpty() || initialText.isEmpty()) {
            accountMessageLabel.setText("Please fill in all account details.");
            return;
        }


        double initialDeposit;
        try {
            initialDeposit = Double.parseDouble(initialText);
            if (initialDeposit < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            accountMessageLabel.setText("Invalid deposit amount.");
            return;
        }

        String accNum = "ACC" + (int)(Math.random() * 10000);
        Account newAccount;

        try {
            switch (type) {
                case "Savings":
                    newAccount = new SavingsAccount(accNum, branch, loggedInCustomer);
                    newAccount.deposit(initialDeposit);
                    break;
                case "Cheque":
                    String employerName = employerNameField.getText();
                    String employerAddress = employerAddressField.getText();

                    if (employerName.isEmpty() || employerAddress.isEmpty()) {
                        accountMessageLabel.setText("Please enter Employer Details");
                        return;
                    }

                    newAccount = new ChequeAccount(accNum, branch, loggedInCustomer, employerName, employerAddress);
                    newAccount.deposit(initialDeposit);
                    break;
                case "Investment":
                    newAccount = new InvestmentAccount(accNum, branch, loggedInCustomer, initialDeposit);
                    break;
                default:
                    accountMessageLabel.setText("Unknown account type.");
                    return;
            }

            loggedInCustomer.openAccount(newAccount);
            refreshAccountTable();
            accountMessageLabel.setText("✅ " + type + " Account opened successfully!");

        } catch (Exception e) {
            accountMessageLabel.setText("❌ " + e.getMessage());
        }
    }

    private void refreshAccountTable() {
        accountTable.getItems().clear();
        accountTable.getItems().addAll(loggedInCustomer.getAccounts());
    }

    @FXML
    private void handleDeposit() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Error", "Please select an account first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit Funds");
        dialog.setHeaderText("Deposit Money into Account" + selectedAccount.getAccountNumber());
        dialog.setContentText("Enter amount to deposit: ");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
            double amount = Double.parseDouble(amountStr);
            selectedAccount.deposit(amount);
            refreshAccountTable();
            showAlert("Deposit Successful", "Deposited P" + amount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please try again");
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });

    }

    @FXML
    private void handleWithdraw() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Error!","Please select an account first.");
            return;
        }

        if (!(selectedAccount instanceof Withdrawable)) {
            showAlert("Error!","Withdrawals are not allowed on this account type.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw Funds");
        dialog.setHeaderText("Withdraw Money from Account " + selectedAccount.getAccountNumber());
        dialog.setContentText("Enter amount to withdraw: ");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);

                Withdrawable withdrawableAccount = (Withdrawable) selectedAccount;
                withdrawableAccount.withdraw(amount);

                refreshAccountTable();
                showAlert("Withdrawal Successfull", "Withdrawn P" + amount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input","Please try again");
            } catch (Exception e) {
                showAlert("Error",e.getMessage());
            }
        });
    }

    @FXML
    private void handleViewTransactions() {

    }

    @FXML
    private void handleLogout() {
        showAlert("Logout", "Logging out...");
        accountTable.getScene().getWindow().hide();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void handleAccountTypeSelection(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAccountTypeSelection() {
        String selectedType = accountTypeCombo.getValue();
        boolean isCheque = "Cheque".equals(selectedType);

        employerFields.setVisible(isCheque);
        employerFields.setManaged(isCheque);
    }

}
