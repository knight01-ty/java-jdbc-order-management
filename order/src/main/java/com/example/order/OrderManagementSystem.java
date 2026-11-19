
import java.sql.*;
import java.util.Scanner;

public class OrderManagementSystem {

    static final String URL =
        "jdbc:postgresql://localhost:5432/ecommerce_db";
    static final String USER = "postgres";
    static final String PASSWORD = "PASS";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    static void connect() throws SQLException {
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database connected successfully!");
    }

    public static void main(String[] args) {
        try {
            connect();

            while (true) {
                System.out.println("\n===== ORDER MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert three orders");
                System.out.println("2. Search orders by status");
                System.out.println("3. Increase order quantity");
                System.out.println("4. Update order status");
                System.out.println("5. Display total order value");
                System.out.println("6. Delete a cancelled order");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        insertOrders();
                        break;
                    case 2:
                        searchOrders();
                        break;
                    case 3:
                        increaseQuantity();
                        break;
                    case 4:
                        updateStatus();
                        break;
                    case 5:
                        totalOrderValue();
                        break;
                    case 6:
                        deleteOrder();
                        break;
                    case 0:
                        con.close();
                        sc.close();
                        System.out.println("Program terminated.");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void insertOrders() throws SQLException {
        String sql = "INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            int[] ids = {101, 102, 103};
            String[] customers = {"Aman", "Riya", "Karan"};
            String[] products = {"Laptop", "Mouse", "Keyboard"};
            int[] quantities = {3, 5, 2};
            double[] prices = {55000, 800, 1500};
            String[] statuses = {"Pending", "Shipped", "Cancelled"};

            for (int i = 0; i < 3; i++) {
                ps.setInt(1, ids[i]);
                ps.setString(2, customers[i]);
                ps.setString(3, products[i]);
                ps.setInt(4, quantities[i]);
                ps.setDouble(5, prices[i]);
                ps.setString(6, statuses[i]);

                try {
                    ps.executeUpdate();
                    System.out.println("Order " + ids[i] + " inserted.");
                } catch (SQLException e) {
                    if ("23505".equals(e.getSQLState())) {
                        System.out.println("Duplicate order ID: " + ids[i]);
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    static void searchOrders() throws SQLException {
        System.out.print("Enter order status: ");
        String status = sc.nextLine();

        String sql = "SELECT * FROM orders " +
                     "WHERE status = ? AND quantity > 2 " +
                     "ORDER BY unit_price DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println(
                        "ID: " + rs.getInt("order_id") +
                        ", Customer: " + rs.getString("customer_name") +
                        ", Product: " + rs.getString("product_name") +
                        ", Quantity: " + rs.getInt("quantity") +
                        ", Price: " + rs.getDouble("unit_price") +
                        ", Status: " + rs.getString("status")
                    );
                }

                if (!found) {
                    System.out.println("No matching orders found.");
                }
            }
        }
    }

    static void increaseQuantity() throws SQLException {
        System.out.print("Enter order ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter additional quantity: ");
        int extra = Integer.parseInt(sc.nextLine());

        if (extra <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        String sql = "UPDATE orders " +
                     "SET quantity = quantity + ? " +
                     "WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, extra);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Quantity updated successfully!");
            } else {
                System.out.println("Order does not exist.");
            }
        }
    }

    static void updateStatus() throws SQLException {
        System.out.print("Enter order ID: ");
        int id = Integer.parseInt(sc.nextLine());

        String checkSql = "SELECT * FROM orders WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Order does not exist.");
                    return;
                }

                System.out.println("Current status: " +
                                   rs.getString("status"));
            }
        }

        System.out.print("Enter new status (Pending/Shipped/Delivered): ");
        String status = sc.nextLine();

        if (!status.equalsIgnoreCase("Pending") &&
            !status.equalsIgnoreCase("Shipped") &&
            !status.equalsIgnoreCase("Delivered")) {
            System.out.println("Invalid status!");
            return;
        }

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();
            System.out.println("Status updated successfully!");

            String displaySql = "SELECT * FROM orders WHERE order_id = ?";

            try (PreparedStatement display =
                     con.prepareStatement(displaySql)) {
                display.setInt(1, id);

                try (ResultSet rs = display.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(
                            "ID: " + rs.getInt("order_id") +
                            ", Customer: " + rs.getString("customer_name") +
                            ", Product: " + rs.getString("product_name") +
                            ", Quantity: " + rs.getInt("quantity") +
                            ", Price: " + rs.getDouble("unit_price") +
                            ", Status: " + rs.getString("status")
                        );
                    }
                }
            }
        }
    }

    static void totalOrderValue() throws SQLException {
        String sql = "SELECT SUM(quantity * unit_price) AS total FROM orders";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                System.out.printf("Total order value: %.2f%n",
                                  rs.getDouble("total"));
            }
        }
    }

    static void deleteOrder() throws SQLException {
        System.out.print("Enter order ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM orders " +
                     "WHERE order_id = ? AND status = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, "Cancelled");

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Cancelled order deleted successfully!");
            } else {
                System.out.println(
                    "Order cannot be deleted. It either does not exist " +
                    "or its status is not Cancelled."
                );
            }
        }
    }









}
