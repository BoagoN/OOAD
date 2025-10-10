public class InvestmentAccount extends Account implements Interest {
    public static final double MONTHLY_INTEREST_RATE = 0.05;
    public static final double MINIMUM_OPEN_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super (accountNumber, balance, branch, customer);

        if (balance < MINIMUM_OPEN_DEPOSIT) {
            throw new IllegalArgumentException("Investment Account requires minimum deposit of " + MINIMUM_OPEN_DEPOSIT);
        }
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposited: " + amount + "to Investment Account: " + accountNumber);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + "from Investment Account: " + accountNumber);
            return true;
        }
        System.out.println("Insufficient funds");
        return false;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
        System.out.println("Interest of " + interest + " added to Investment Account: " + accountNumber);
    }

    @Override 
    public double getMonthlyInterestRate() {
        return MONTHLY_INTEREST_RATE;
    }
    
    public static double getMinimumOpenDeposit() {
        return MINIMUM_OPEN_DEPOSIT;
    }
}