import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String pin;
    private List<Account> accounts;

    public Customer () {

    }
    public Customer (String customerId, String firstName, String lastName, String email, String phoneNumber, String pin) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pin = pin;
        this.accounts = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPin() {
        return pin;
    }
    public List<Account> getAccounts() {
        return accounts;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void addAccount( Account account) {
        if (account != null) {
            accounts.add(account);
        }
    }

    public boolean removeAccount(Account account) {
        return accounts.remove(account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public boolean authenticate(String inputPin) {
        return this.pin.equals(inputPin);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email+ '\'' +
                ", accounts=" + accounts.size() +
                '}';
    }
}