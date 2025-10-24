package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private VBox loginPane;
    @FXML private VBox registerPane;

    // Login
    @FXML private TextField emailField;
    @FXML private PasswordField pinField;
    @FXML private Label messageLabel;

    // Registration
    @FXML private RadioButton individualRadio;
    @FXML private RadioButton corporateRadio;
    @FXML private VBox individualFields;
    @FXML private VBox corporateFields;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPinField;
    @FXML private TextField firstNameField, lastNameField, cellField;
    @FXML private TextField companyNameField, regNumField, telField, addressField;
    @FXML private Label registerMessageLabel;

    private ToggleGroup group;
    private Map<String, Customer> customerMap = new HashMap<>();
    private Stage stage;

    @FXML
    public void initialize() {
        group = new ToggleGroup();
        individualRadio.setToggleGroup(group);
        corporateRadio.setToggleGroup(group);

        individualRadio.setOnAction(e -> toggleCustomerFields(true));
        corporateRadio.setOnAction(e -> toggleCustomerFields(false));
    }

    private void toggleCustomerFields(boolean isIndividual) {
        individualFields.setVisible(isIndividual);
        individualFields.setManaged(isIndividual);
        corporateFields.setVisible(!isIndividual);
        corporateFields.setManaged(!isIndividual);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //NAVIGATION
    @FXML
    private void showRegistrationPane() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
    }

    @FXML
    private void showLoginPane() {
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        loginPane.setVisible(true);
        loginPane.setManaged(true);
    }

    //LOGIN
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String pin = pinField.getText();

        if (email.isEmpty() || pin.isEmpty()) {
            messageLabel.setText("⚠ Please enter both Email and PIN.");
            return;
        }

        Customer customer = customerMap.get(email);
        if (customer != null && customer.validatePin(pin)) {
            messageLabel.setText("✅ Login successful!");
            loadAccountView(customer);
        } else {
            messageLabel.setText("❌ Invalid credentials.");
        }
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admin Login");
        alert.setHeaderText(null);
        alert.setContentText("🔐 Admin login feature coming soon.");
        alert.showAndWait();
    }


    //REGISTRATION
    @FXML
    private void handleRegistration() {
        String email = regEmailField.getText();
        String pin = regPinField.getText();

        if (email.isEmpty() || pin.isEmpty()) {
            registerMessageLabel.setText("⚠ Email and PIN are required.");
            return;
        }

        if (customerMap.containsKey(email)) {
            registerMessageLabel.setText("⚠ Email already registered.");
            return;
        }

        Customer newCustomer;
        if (individualRadio.isSelected()) {
            newCustomer = new IndividualCustomer(
                    "CUST" + (customerMap.size() + 1),
                    email, pin,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    cellField.getText()
            );
        } else if (corporateRadio.isSelected()) {
            newCustomer = new CorporateCustomer(
                    "CORP" + (customerMap.size() + 1),
                    email, pin,
                    companyNameField.getText(),
                    regNumField.getText(),
                    telField.getText(),
                    addressField.getText()
            );
        } else {
            registerMessageLabel.setText("⚠ Please select Individual or Corporate.");
            return;
        }

        customerMap.put(email, newCustomer);
        registerMessageLabel.setText("✅ Registration successful! Please log in.");
        showLoginPane();
    }

    // SWITCH TO ACCOUNT VIEW
    private void loadAccountView(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AccountView.fxml"));
            VBox accountRoot = loader.load();

            AccountController accountController = loader.getController();
            accountController.setLoggedInCustomer(customer);
            accountController.setLoginController(this);

            Scene scene = new Scene(accountRoot);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Customer> getCustomerMap() {
        return customerMap;
    }
}
