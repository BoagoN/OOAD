package model;

public class CorporateCustomer extends Customer {
    private String companyName;
    private String registrationNumber;
    private String telephoneNumber;
    private String address;

    public CorporateCustomer(String customerId, String email, String pin, String companyName, String registrationNumber, String telephoneNumber, String address) {
        super(customerId, email, pin);
        this.customerId = customerId;
        this.email = email;
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.telephoneNumber = telephoneNumber;
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer(Corporate): " + companyName);
        System.out.println("Registration Number: " + registrationNumber);
        System.out.println("Physical Address: " + address);
        System.out.println("Telephone Number: " + telephoneNumber);
        System.out.println("Email: " + email);
    }
}