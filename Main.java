public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        BankStaff teller1 = new BankStaff("TELLER001", "Lopang", "Sewagodimo", "1234");
        
        // Register customers
        Customer customer1 = teller1.registerCustomer("CUST001", "Catherine", "Serame", 
                                                    "CatSerame@gmail.com","7639788", "5678");
        Customer customer2 = teller1.registerCustomer("CUST002", "Jackson", "7830123", "Sekgwatlhe", 
                                                    "SekgwatlkeJ@gmail.com", "9012");
        
        // Add customers to bank
        bank.registerCustomer(customer1);
        bank.registerCustomer(customer2);
        
        System.out.println("Customers registered successfully!");

        // Create accounts using BankStaff
        Account savingsAccount = teller1.createAccount(customer1, "Savings", 1000.00, 
                                                     "WaterShed", "", "");
        Account investmentAccount = teller1.createAccount(customer1, "Investment", 600.00, 
                                                        "WaterShed", "", "");
        Account chequeAccount = teller1.createAccount(customer2, "Cheque", 2000.00, 
                                                    "Riverwalk", "Company Y", "123 Business Av");
        
        // Add accounts to bank
        bank.addAccount(savingsAccount);
        bank.addAccount(investmentAccount);
        bank.addAccount(chequeAccount);

        System.out.println("Accounts created successfully!");

        
        // deposits
        System.out.println("Making deposits...");
        savingsAccount.deposit(500.00);
        investmentAccount.deposit(300.00);
        chequeAccount.deposit(1000.00);

        //withdrawals
        chequeAccount.withdraw(200.00);
        savingsAccount.withdraw(100.00); // This should fail for Savings account
       

        System.out.println("Savings Account: " + savingsAccount.getBalance());
        System.out.println("Investment Account: " + investmentAccount.getBalance());
        System.out.println("Cheque Account: " + chequeAccount.getBalance());

        
        bank.payInterest();

        
        System.out.println("Savings Account: " + savingsAccount.getBalance());
        System.out.println("Investment Account: " + investmentAccount.getBalance());
        System.out.println("Cheque Account: " + chequeAccount.getBalance());


        //Test authentication
        Customer authenticated = bank.authenticateCustomer("CUST001", "5678");
        if (authenticated != null) {
            System.out.println("Customer authenticated: " + authenticated.getFirstName() + " " + authenticated.getLastName());
        } else {
            System.out.println("Authentication failed!");
        }

        System.out.println("\n=== Final Bank Status ===");
        System.out.println("Bank staff: " + teller1);
        System.out.println("Bank status: " + bank);
    }
}