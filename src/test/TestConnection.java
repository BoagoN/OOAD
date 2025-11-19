package test;

import dao.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Comprehensive MySQL Connection Test");

        
        System.out.println("\n1. Testing MySQL SERVER connection...");
        DatabaseConnection.testServerConnection();

        
        System.out.println("\n2. Testing DATABASE connection...");
        DatabaseConnection.testConnection();

        System.out.println("\nTest Complete");
    }
}
