public class SavingsAccount extends Account implements Interest {
    private static final double MONTHLY_INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposited: " + amount + "to Savings Account: " + accountNumber);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        System.out.println("Withdrawals not allowed from Savings Account");
        return false;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
        System.out.println("Interest of " + interest + "has been added to Savings Account: " + accountNumber);
    }

    @Override
    public double getMonthlyInterestRate() {
        return MONTHLY_INTEREST_RATE;
    }
}