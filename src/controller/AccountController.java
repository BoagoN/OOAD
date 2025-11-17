package controller;

import dao.AccountDAO;
import dao.TransactionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.*;
import model.interfaces.Withdrawable;

import java.sql.SQLException;
import java.util.List;

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
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

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

            // Save to database first
            accountDAO.addAccount(newAccount);

            // Record initial deposit transaction
            if (initialDeposit > 0 && !"Investment".equals(type)) {
                Transaction depositTransaction = new Transaction("DEPOSIT", initialDeposit, accNum);
                transactionDAO.addTransaction(depositTransaction);
            }

            // Add to customer's local list and refresh UI
            loggedInCustomer.openAccount(newAccount);
            refreshAccountTable();

            // Clear form fields
            branchField.clear();
            initialDepositField.clear();
            employerNameField.clear();
            employerAddressField.clear();
            accountTypeCombo.setValue(null);

            accountMessageLabel.setText("✅ " + type + " Account opened successfully!");

        } catch (SQLException e) {
            accountMessageLabel.setText("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            accountMessageLabel.setText("❌ " + e.getMessage());
        }
    }

    private void refreshAccountTable() {
        if (loggedInCustomer != null) {
            try {
                List<Account> accounts = accountDAO.getAccountsByCustomerId(loggedInCustomer.getCustomerId());
                accountTable.getItems().clear();
                accountTable.getItems().addAll(accounts);
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to load accounts: " + e.getMessage());
                e.printStackTrace();
            }
        }
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
        dialog.setHeaderText("Deposit Money into Account " + selectedAccount.getAccountNumber());
        dialog.setContentText("Enter amount to deposit: ");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);

                // Update in memory
                selectedAccount.deposit(amount);

                // Update in database
                accountDAO.updateAccountBalance(selectedAccount.getAccountNumber(), selectedAccount.getBalance());

                // Record transaction
                Transaction transaction = new Transaction("DEPOSIT", amount, selectedAccount.getAccountNumber());
                transactionDAO.addTransaction(transaction);

                refreshAccountTable();
                showAlert("Deposit Successful", "Deposited P" + amount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid amount");
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to record deposit: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleWithdraw() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Error", "Please select an account first.");
            return;
        }

        if (!(selectedAccount instanceof Withdrawable)) {
            showAlert("Error", "Withdrawals are not allowed on this account type.");
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

                // Update in database
                accountDAO.updateAccountBalance(selectedAccount.getAccountNumber(), selectedAccount.getBalance());

                // Record transaction
                Transaction transaction = new Transaction("WITHDRAWAL", amount, selectedAccount.getAccountNumber());
                transactionDAO.addTransaction(transaction);

                refreshAccountTable();
                showAlert("Withdrawal Successful", "Withdrawn P" + amount);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid amount");
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to record withdrawal: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleViewTransactions() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Error", "Please select an account first.");
            return;
        }

        try {
            List<Transaction> transactions = transactionDAO.getTransactionsByAccount(selectedAccount.getAccountNumber());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction History");
            alert.setHeaderText("Transactions for Account: " + selectedAccount.getAccountNumber());

            StringBuilder content = new StringBuilder("Transaction History:\n\n");
            if (transactions.isEmpty()) {
                content.append("No transactions found.");
            } else {
                for (Transaction t : transactions) {
                    content.append(t.toString()).append("\n");
                }
            }

            alert.setContentText(content.toString());
            alert.getDialogPane().setPrefSize(600, 400);
            alert.showAndWait();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load transactions: " + e.getMessage());
        }
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

    @FXML
    private void handleAccountTypeSelection() {
        String selectedType = accountTypeCombo.getValue();
        boolean isCheque = "Cheque".equals(selectedType);

        employerFields.setVisible(isCheque);
        employerFields.setManaged(isCheque);
    }
}