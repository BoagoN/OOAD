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

import dao.CustomerDAO;
import java.sql.SQLException;

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

        try {
            Customer customer = customerDAO.getCustomerByEmail(email);
            if (customer != null && customer.validatePin(pin)) {
                messageLabel.setText("Login Successful!");
                loadAccountView(customer);
            } else {
                messageLabel.setText("Invalid Credentials");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //REGISTRATION
    @FXML
    private void handleRegistration() {
        String email = regEmailField.getText();
        String pin = regPinField.getText();

        if (email.isEmpty() || pin.isEmpty()) {
            registerMessageLabel.setText("Email and PIN are required.");
            return;
        }

        try {
            if (customerDAO.emailExists(email)) {
                registerMessageLabel.setText("This email already exists");
                return;
            }

            Customer newCustomer;
            String customerId = "CUST" + System.currentTimeMillis();

            if (individualRadio.isSelected()) {
                newCustomer = new IndividualCustomer(customerId,email,pin,firstNameField.getText(),lastNameField.getText(),cellField.getText());

            } else if (corporateRadio.isSelected()) {
                newCustomer = new CorporateCustomer(customerId,email,pin,companyNameField.getText(),regNumField.getText(),telField.getText(),addressField.getText());

            } else {
                registerMessageLabel.setText("Please select customer type");
                return;
            }

            customerDAO.addCustomer(newCustomer);
            registerMessageLabel.setText("Registration Successful! You may now Log in");
            showLoginPane();

        } catch (SQLException e) {
            registerMessageLabel.setText("Registration Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private CustomerDAO customerDAO = new CustomerDAO();


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

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Admin Login");
        passwordDialog.setHeaderText("Enter Admin Password");
        passwordDialog.setContentText("Password:");

        passwordDialog.showAndWait().ifPresent(password -> {
            if ("admin123".equals(password)) {
                loadAdminView();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid admin password.");
                alert.showAndWait();
            }
        });
    }

    private void loadAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
            VBox adminRoot = loader.load();

            Scene scene = new Scene(adminRoot);
            Stage adminStage = new Stage();
            adminStage.setTitle("Bank Admin Dashboard");
            adminStage.setScene(scene);
            adminStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot load admin dashboard");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}