package test;

import dao.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Comprehensive MySQL Connection Test ===");

        // Test 1: Check if MySQL server is reachable
        System.out.println("\n1. Testing MySQL SERVER connection...");
        DatabaseConnection.testServerConnection();

        // Test 2: Check if database exists and is accessible
        System.out.println("\n2. Testing DATABASE connection...");
        DatabaseConnection.testConnection();

        System.out.println("\n=== Test Complete ===");
    }
}