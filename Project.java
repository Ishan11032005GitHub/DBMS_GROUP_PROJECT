import java.sql.*;
import java.util.Scanner;

public class BusinessManager {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/RetailSync";
        String username = "root";
        String password = "1234";
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = getConnection()) {
            String[] tables = { "Customers", "Employees", "Suppliers", "Products", "Purchases", "AvailableStocks",
                    "Sales", "Returns" };

            while (true) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Print a table");
                System.out.println("2. Insert into a table");
                System.out.println("3. Delete from a table");
                System.out.println("4. Update a table");
                System.out.println("5. Run a custom query, There are 8 queries");
                System.out.println("6. Exit");

                int opChoice = scanner.nextInt();
                scanner.nextLine();

                switch (opChoice) {
                    case 1: // Print a table
                        int printChoice = getTableChoice(scanner, tables);
                        if (printChoice == -1)
                            break;
                        printTable(conn, tables[printChoice]);
                        break;

                    case 2: // Insert
                        insertIntoTable(conn, scanner);
                        break;

                    case 3: // Delete
                        deleteFromTable(conn, scanner);
                        break;

                    case 4: // Update
                        updateTable(conn, scanner);
                        break;

                        case 5: // Custom Queries
                        System.out.println("\nCustom Query Options:");
                        System.out.println("1. List all products by supplier name");
                        System.out.println("2. List all sales made to a customer");
                        System.out.println("3. List all pending purchases");
                        System.out.println("4. List returns by product name");
                        System.out.println("5. Show top 5 selling products");
                        System.out.println("6. Show stock levels of all products");
                        System.out.println("7. List female employees");
                        System.out.println("8. Back");
                    
                        int queryChoice = scanner.nextInt();
                        scanner.nextLine();
                    
                        switch (queryChoice) {
                            case 1:
                                System.out.print("Enter supplier name: ");
                                listProductsBySupplier(conn, scanner.nextLine());
                                break;
                            case 2:
                                System.out.print("Enter customer name: ");
                                listSalesByCustomer(conn, scanner.nextLine());
                                break;
                            case 3:
                                listPendingPurchases(conn);
                                break;
                            case 4:
                                System.out.print("Enter product name: ");
                                listReturnsByProduct(conn, scanner.nextLine());
                                break;
                            case 5:
                                showTopSellingProducts(conn);
                                break;
                            case 6:
                                showStockLevels(conn);
                                break;
                            case 7:
                                listFemaleEmployees(conn);
                                break;
                            case 8:
                                break; // Go back to main menu
                            default:
                                System.out.println("Invalid query option.");
                        }
                        break; 
                    case 6: // Exit
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid operation choice.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getTableChoice(Scanner scanner, String[] tables) {
        System.out.println("\nChoose a table:");
        for (int i = 0; i < tables.length; i++) {
            System.out.println((i + 1) + ". " + tables[i]);
        }
        System.out.println((tables.length + 1) + ". Cancel");

        int tableChoice = scanner.nextInt();
        scanner.nextLine();

        if (tableChoice == tables.length + 1)
            return -1;
        if (tableChoice < 1 || tableChoice > tables.length) {
            System.out.println("Invalid table choice.");
            return -1;
        }
        return tableChoice - 1;
    }

    private static void printTable(Connection conn, String tableName) {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void insertIntoTable(Connection conn, Scanner scanner) {
        System.out.println("Choose a table to insert into: Customers, Products, Sales");
        String table = scanner.nextLine().trim();

        try {
            switch (table) {
                case "Customers", "customers":
                    System.out.print("Enter name, email, phone, address, country:\n> ");
                    String name = scanner.nextLine();
                    String email = scanner.nextLine();
                    String phone = scanner.nextLine();
                    String address = scanner.nextLine();
                    String country = scanner.nextLine();

                    String insertCustomer = "INSERT INTO Customers (CustomerName, EmailID, PhoneNo, Address, Country) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertCustomer)) {
                        stmt.setString(1, name);
                        stmt.setString(2, email);
                        stmt.setString(3, phone);
                        stmt.setString(4, address);
                        stmt.setString(5, country);
                        stmt.executeUpdate();
                        System.out.println("Customer inserted.");
                    }
                    break;

                case "Products", "products":
                    System.out.print("Enter name, supplierID, description, unitPrice:\n> ");
                    String pname = scanner.nextLine();
                    int sid = Integer.parseInt(scanner.nextLine());
                    String desc = scanner.nextLine();
                    double price = Double.parseDouble(scanner.nextLine());

                    String insertProduct = "INSERT INTO Products (ProductName, SupplierID, Description, UnitPrice) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertProduct)) {
                        stmt.setString(1, pname);
                        stmt.setInt(2, sid);
                        stmt.setString(3, desc);
                        stmt.setDouble(4, price);
                        stmt.executeUpdate();
                        System.out.println("Product inserted.");
                    }
                    break;

                case "Sales", "sales":
                    System.out.print("Enter purchaseID, productID, customerID, quantity, unitCost:\n> ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    int prodid = Integer.parseInt(scanner.nextLine());
                    int custid = Integer.parseInt(scanner.nextLine());
                    int qty = Integer.parseInt(scanner.nextLine());
                    double cost = Double.parseDouble(scanner.nextLine());

                    String insertSale = "INSERT INTO Sales (PurchaseID, ProductID, CustomerID, Quantity, UnitCost) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertSale)) {
                        stmt.setInt(1, pid);
                        stmt.setInt(2, prodid);
                        stmt.setInt(3, custid);
                        stmt.setInt(4, qty);
                        stmt.setDouble(5, cost);
                        stmt.executeUpdate();
                        System.out.println("Sale inserted.");
                    }
                    break;

                default:
                    System.out.println("Insert not supported for that table yet.");
            }
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    private static void deleteFromTable(Connection conn, Scanner scanner) {
        System.out.println("Choose table to delete from: Customers, Products, Sales");
        String table = scanner.nextLine().trim();

        try {
            switch (table) {
                case "Customers":
                    System.out.print("Enter CustomerID to delete: ");
                    int cid = scanner.nextInt();
                    scanner.nextLine();
                    String deleteCustomer = "DELETE FROM Customers WHERE CustomerID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteCustomer)) {
                        stmt.setInt(1, cid);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Customer deleted." : "Customer not found.");
                    }
                    break;

                case "Products":
                    System.out.print("Enter ProductID to delete: ");
                    int pid = scanner.nextInt();
                    scanner.nextLine();
                    String deleteProduct = "DELETE FROM Products WHERE ProductID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteProduct)) {
                        stmt.setInt(1, pid);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Product deleted." : "Product not found.");
                    }
                    break;

                case "Sales":
                    System.out.print("Enter PurchaseItemID to delete: ");
                    int sid = scanner.nextInt();
                    scanner.nextLine();
                    String deleteSale = "DELETE FROM Sales WHERE PurchaseItemID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSale)) {
                        stmt.setInt(1, sid);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Sale deleted." : "Sale not found.");
                    }
                    break;

                default:
                    System.out.println("Delete not supported for that table.");
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    private static void updateTable(Connection conn, Scanner scanner) {
        System.out.println("Choose table to update: Customers, Products");
        String table = scanner.nextLine().trim();

        try {
            switch (table) {
                case "Customers":
                    System.out.print("Enter CustomerID to update: ");
                    int cid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new phone and address:\n> ");
                    String newPhone = scanner.nextLine();
                    String newAddr = scanner.nextLine();

                    String updateCustomer = "UPDATE Customers SET PhoneNo = ?, Address = ? WHERE CustomerID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateCustomer)) {
                        stmt.setString(1, newPhone);
                        stmt.setString(2, newAddr);
                        stmt.setInt(3, cid);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Customer updated." : "Customer not found.");
                    }
                    break;

                case "Products":
                    System.out.print("Enter ProductID to update: ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new price:\n> ");
                    double newPrice = Double.parseDouble(scanner.nextLine());

                    String updateProduct = "UPDATE Products SET UnitPrice = ? WHERE ProductID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateProduct)) {
                        stmt.setDouble(1, newPrice);
                        stmt.setInt(2, pid);
                        int rows = stmt.executeUpdate();
                        System.out.println(rows > 0 ? "Product updated." : "Product not found.");
                    }
                    break;

                default:
                    System.out.println("Update not supported for that table.");
            }
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    private static void listProductsBySupplier(Connection conn, String supplierName) {
        String query = "SELECT p.ProductID, p.ProductName, p.UnitPrice " +
                "FROM Products p JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                "WHERE s.SupplierName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, supplierName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No products found for supplier: " + supplierName);
                    return;
                }
                System.out.println("Products supplied by " + supplierName + ":");
                while (rs.next()) {
                    System.out.println(rs.getInt("ProductID") + "\t" + rs.getString("ProductName") + "\t"
                            + rs.getDouble("UnitPrice"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listSalesByCustomer(Connection conn, String customerName) {
        String query = "SELECT s.PurchaseItemID, s.ProductID, s.Quantity, s.UnitCost " +
                "FROM Sales s JOIN Customers c ON s.CustomerID = c.CustomerID " +
                "WHERE c.CustomerName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No sales found for customer: " + customerName);
                    return;
                }
                System.out.println("Sales for customer " + customerName + ":");
                while (rs.next()) {
                    System.out.println("ItemID: " + rs.getInt("PurchaseItemID") +
                            ", ProductID: " + rs.getInt("ProductID") +
                            ", Quantity: " + rs.getInt("Quantity") +
                            ", Unit Cost: " + rs.getDouble("UnitCost"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listPendingPurchases(Connection conn) {
        String query = "SELECT PurchaseID, ProductID, TotalAmount FROM Purchases WHERE Status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            System.out.println("Pending Purchases:");
            while (rs.next()) {
                System.out.println("PurchaseID: " + rs.getInt("PurchaseID") +
                        ", ProductID: " + rs.getInt("ProductID") +
                        ", Total: " + rs.getDouble("TotalAmount"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listReturnsByProduct(Connection conn, String productName) {
        String query = "SELECT r.ReturnID, r.ReturnDate, r.Reason, r.Status " +
                "FROM Returns r JOIN Products p ON r.ProductID = p.ProductID " +
                "WHERE p.ProductName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No returns found for product: " + productName);
                    return;
                }
                System.out.println("Returns for product " + productName + ":");
                while (rs.next()) {
                    System.out.println("ReturnID: " + rs.getInt("ReturnID") +
                            ", Date: " + rs.getTimestamp("ReturnDate") +
                            ", Reason: " + rs.getString("Reason") +
                            ", Status: " + rs.getString("Status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showTopSellingProducts(Connection conn) {
        String query = "SELECT p.ProductName, SUM(s.Quantity) AS TotalSold " +
                "FROM Sales s JOIN Products p ON s.ProductID = p.ProductID " +
                "GROUP BY p.ProductName " +
                "ORDER BY TotalSold DESC " +
                "LIMIT 5";

        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("Top 5 Selling Products:");
            System.out.printf("%-25s %-10s%n", "Product Name", "Total Sold");
            while (rs.next()) {
                System.out.printf("%-25s %-10d%n",
                        rs.getString("ProductName"),
                        rs.getInt("TotalSold"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showStockLevels(Connection conn) {
        String query = "SELECT * FROM AvailableStocks"; 
    
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
    
            System.out.println("Available Stocks:");
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(meta.getColumnName(i) + ": " + rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void listFemaleEmployees(Connection conn) {
        String query = "SELECT EmployeeID, EmployeeName, Gender FROM Employees WHERE Gender = 'Female'";

        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No female employees found.");
                return;
            }

            System.out.println("Female Employees:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("EmployeeID") +
                        ", Name: " + rs.getString("EmployeeName"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}

// javac -cp ".;mysql-connector-j-9.2.0.jar" BusinessManager.java
// java -cp ".;mysql-connector-j-9.2.0.jar" BusinessManager
