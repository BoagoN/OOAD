public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String branch, Customer customer, String employerName, String employerAddress) {
        super(accountNumber, balance, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposited: " + amount + "to Cheque Account: " + accountNumber);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + "from Cheque Account: " + accountNumber);
            return true;
        }
        System.out.println("Insufficient funds");
        return false;
    }

    @Override
    public void calculateInterest() {
        System.out.println("No interest applied to Cheque Account");
    }

    public String getEmployerName() {
        return employerName;
    }
    public String getEmployerAddress() {
        return employerAddress;
    }
}