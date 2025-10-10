public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;


    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;

    }

    public String getAccountNumber()
    { return accountNumber; }
    public double getBalance()
    { return balance; }
    public String getBranch()
    {return branch; }
    public Customer getCustomer()
    {return customer; }

public void setBalance (double balance)
{ this.balance = balance; }

    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);
    public abstract void calculateInterest();

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", branch='" + branch + '\''
                +
                '}';
    }
}