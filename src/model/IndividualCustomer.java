package model;

public class IndividualCustomer extends Customer {
    private String firstName;
    private String lastName;
    private String cellphoneNumber;

    public IndividualCustomer(String customerId, String email, String pin, String firstName, String lastName,String cellphoneNumber) {
        super(customerId, email, pin);
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer(Individual): " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + cellphoneNumber);
    }
}