package com.mariusss.main;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    static ArrayList<String> usernames = new ArrayList<>();
    static ArrayList<String> passwords = new ArrayList<>();
    static String currentUser = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> productNames = new ArrayList<>();
        ArrayList<Double> productPrices = new ArrayList<>();
        boolean exit = false;

        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.println("\n1. Sign Up");
            System.out.println("2. Log In");
            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                signup(scanner);
            } else if (option == 2) {
                login(scanner);
                if (!currentUser.equals("")) loggedIn = true;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        while (!exit) {
            System.out.println("\n== SELECT MODE ==");
            System.out.println("1. Cashier Mode");
            System.out.println("2. Admin Mode");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int mode = scanner.nextInt();
            scanner.nextLine();

            if (mode == 1) {
                cashierMode(scanner, productNames, productPrices);
            } else if (mode == 2) {
                adminMode(scanner, productNames, productPrices);
            } else if (mode == 3) {
                exit = true;
                System.out.println("Exiting system. Thank you!");
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    public static void signup(Scanner scanner) {
        System.out.println("\n========= USER SIGNUP =========");
        String username;
        String password;

        while (true) {
            System.out.print("Enter username (5-15 characters, letters/numbers only): ");
            username = scanner.nextLine();
            if (Pattern.matches("^[a-zA-Z0-9]{5,15}$", username)) {
                break;
            } else {
                System.out.println("Invalid Username Format. Please Try Again.");
            }
        }

        while (true) {
            System.out.print("Enter password (8-20 characters, at least 1 uppercase and 1 number): ");
            password = scanner.nextLine();
            if (Pattern.matches("^(?=.*[A-Z])(?=.*\\d).{8,20}$", password)) {
                break;
            } else {
                System.out.println("Invalid Password Format. Please Try Again.");
            }
        }

        usernames.add(username);
        passwords.add(password);
        System.out.println("Signup successful!");
    }

    public static void login(Scanner scanner) {
        System.out.println("\n========= USER LOGIN =========");
        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            boolean found = false;
            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(username) && passwords.get(i).equals(password)) {
                    found = true;
                    currentUser = username;
                    break;
                }
            }

            if (found) {
                System.out.println("Login successful!");
                break;
            } else {
                System.out.println("Invalid credentials. Please try again.\n");
            }
        }
    }

    public static void cashierMode(Scanner scanner, ArrayList<String> productNames, ArrayList<Double> productPrices) {
        if (productNames.isEmpty()) {
            System.out.println("\nNo products available. Please ask admin to add products.");
            return;
        }

        ArrayList<String> cart = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();

        double total = 0;
        boolean addMore = true;

        while (addMore) {
            System.out.println("\n=== AVAILABLE PRODUCTS ===");
            for (int i = 0; i < productNames.size(); i++) {
                System.out.println((i + 1) + ". " + productNames.get(i) + " | Price: " + productPrices.get(i));
            }

            int index = -1;
            try {
                System.out.print("\nEnter product number to purchase: ");
                index = scanner.nextInt() - 1;
                scanner.nextLine();
                if (index < 0 || index >= productNames.size()) throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid product number.");
                scanner.nextLine();
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            cart.add(productNames.get(index));
            quantities.add(quantity);
            prices.add(productPrices.get(index));

            total += quantity * productPrices.get(index);

            System.out.println("\nCurrent Total Price: " + total);
            System.out.print("Add more items? (yes/no): ");
            addMore = scanner.nextLine().equalsIgnoreCase("yes");
        }

        processPayment(scanner, total);
        logTransaction(cart, quantities, prices, total);
    }

    public static void processPayment(Scanner scanner, double total) {
        double payment;
        do {
            System.out.print("\nEnter payment amount: ");
            payment = scanner.nextDouble();
            if (payment < total) {
                System.out.println("Insufficient amount! Please enter a valid payment.");
            }
        } while (payment < total);

        System.out.println("Change: " + (payment - total));
    }

    public static void adminMode(Scanner scanner, ArrayList<String> productNames, ArrayList<Double> productPrices) {
        boolean adminExit = false;

        while (!adminExit) {
            System.out.println("\n=== ADMIN OPTIONS ===");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Remove Product");
            System.out.println("4. Exit Admin Mode");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                viewProducts(productNames, productPrices);
            } else if (choice == 2) {
                addProduct(scanner, productNames, productPrices);
            } else if (choice == 3) {
                removeProduct(scanner, productNames, productPrices);
            } else if (choice == 4) {
                adminExit = true;
                System.out.println("Exiting Admin Mode...");
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void viewProducts(ArrayList<String> productNames, ArrayList<Double> productPrices) {
        if (productNames.isEmpty()) {
            System.out.println("\nNo products available.");
        } else {
            System.out.println("\n=== PRODUCT LIST ===");
            for (int i = 0; i < productNames.size(); i++) {
                System.out.println((i + 1) + ". " + productNames.get(i) + " | Price: " + productPrices.get(i));
            }
        }
    }

    public static void addProduct(Scanner scanner, ArrayList<String> productNames, ArrayList<Double> productPrices) {
        System.out.print("\nEnter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        productNames.add(name);
        productPrices.add(price);
        System.out.println("Product added successfully.");
    }

    public static void removeProduct(Scanner scanner, ArrayList<String> productNames, ArrayList<Double> productPrices) {
        if (productNames.isEmpty()) {
            System.out.println("\nNo products available to remove.");
            return;
        }

        System.out.print("Enter product number to remove: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index >= 0 && index < productNames.size()) {
            productNames.remove(index);
            productPrices.remove(index);
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("Invalid product number.");
        }
    }

    public static void logTransaction(ArrayList<String> cart, ArrayList<Integer> quantities, ArrayList<Double> prices, double total) {
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            writer.write("\n========== TRANSACTION ==========" + "\n");
            writer.write("Date & Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Cashier: " + currentUser + "\n");
            for (int i = 0; i < cart.size(); i++) {
                writer.write(cart.get(i) + " | Quantity: " + quantities.get(i) + " | Price: " + prices.get(i) + "\n");
            }
            writer.write("Total: " + total + "\n");
            writer.write("===============================\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
        }
    }
}
