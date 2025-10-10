public class BankStaff {
    private String staffId;
    private String firstName;
    private String lastName;
    private String pin;
    
    public BankStaff(String staffId, String firstName, String lastName, String pin) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        setPin(pin);
    }
    

    public String getStaffId() { return staffId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return lastName; }
    public String getPin() { return pin; }
    
    public void setPin(String pin) {
        if (pin != null && pin.matches("\\d{4}")) {
            this.pin = pin;
        } else {
            throw new IllegalArgumentException("PIN must be 4 digits");
        }
    }
    

    public Customer registerCustomer(String customerId, String firstName, String lastName, 
                                   String email, String phoneNumber, String pin) {
        return new Customer(customerId, firstName, lastName, email, phoneNumber, pin);
    }
    
    public Account createAccount(Customer customer, String accountType, 
                               double initialDeposit, String branch,
                               String employerName, String employerAddress) {
        String accountNumber = generateAccountNumber();
        
        switch (accountType.toUpperCase()) {
            case "SAVINGS":
                return new SavingsAccount(accountNumber, initialDeposit, branch, customer);
            case "INVESTMENT":
                return new InvestmentAccount(accountNumber, initialDeposit, branch, customer);
            case "CHEQUE":
                return new ChequeAccount(accountNumber, initialDeposit, branch, customer, 
                                       employerName, employerAddress);
            default:
                throw new IllegalArgumentException("Invalid account type: " + accountType);
        }
    }
    
    public boolean authenticate(String inputPin) {
        return this.pin.equals(inputPin);
    }
    
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return staffId + ": " + firstName + " " + lastName;
    }
}