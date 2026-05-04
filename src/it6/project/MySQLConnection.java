/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it6.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class MySQLConnection {

    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME  = "sari_sari_inventory";
    private static final String DB_URL   = ROOT_URL + DB_NAME;
    private static final String USER     = "root";
    private static final String[] PASSWORD_CANDIDATES = {"", "carl"};

    public Connection getConnection() {
        try {
            Class.forName(DRIVER);

            Connection rootConnection = connectWithFallback(ROOT_URL);
            if (rootConnection == null) return null;

            createDatabaseAndTables(rootConnection);
            rootConnection.close();

            return connectWithFallback(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection connectWithFallback(String url) {
        for (String password : PASSWORD_CANDIDATES) {
            try {
                return DriverManager.getConnection(url, USER, password);
            } catch (Exception ignored) {
                // Try next password candidate.
            }
        }
        JOptionPane.showMessageDialog(null,
                "Unable to connect to MySQL. Please verify root credentials.",
                "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    private void createDatabaseAndTables(Connection con) {
        if (con == null) return;

        try {
            Statement st = con.createStatement();

            // 1. Create / select the database
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            st.executeUpdate("USE " + DB_NAME);

            // 2. Categories table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS categories ("
                + "  id   INT AUTO_INCREMENT PRIMARY KEY, "
                + "  name VARCHAR(100) NOT NULL UNIQUE"
                + ")"
            );

            // 3. Seed default sari-sari store categories (idempotent)
            String[] defaultCategories = {
                "Beverages", "Bread & Pastries", "Canned Goods",
                "Condiments & Sauces", "Dairy & Eggs", "Detergent & Cleaning",
                "Instant Noodles", "Personal Care", "Rice & Grains",
                "Snacks & Candies", "Tobacco", "Others"
            };
            for (String cat : defaultCategories) {
                st.executeUpdate(
                    "INSERT IGNORE INTO categories (name) VALUES ('" + cat + "')"
                );
            }

            // 4. Products table with category_id FK
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS products ("
                + "  id          INT AUTO_INCREMENT PRIMARY KEY, "
                + "  name        VARCHAR(100) NOT NULL, "
                + "  category_id INT NOT NULL, "
                + "  price       DECIMAL(10,2) NOT NULL, "
                + "  quantity    INT NOT NULL DEFAULT 0, "
                + "  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + "              ON UPDATE CURRENT_TIMESTAMP, "
                + "  CONSTRAINT fk_product_category "
                + "    FOREIGN KEY (category_id) REFERENCES categories(id)"
                + ")"
            );

            // 5. One-time migration for existing installs that have the old
            //    VARCHAR 'category' column
            migrateOldCategoryColumn(con);

            System.out.println("Database and tables ready!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Migrates the legacy VARCHAR 'category' column to the new category_id FK.
     * Safe to call on every startup — exits immediately if already migrated.
     */
    private void migrateOldCategoryColumn(Connection con) {
        try {
            // Check whether the old column still exists
            boolean hasOldColumn;
            try (java.sql.ResultSet rs =
                         con.getMetaData().getColumns(DB_NAME, null, "products", "category")) {
                hasOldColumn = rs.next();
            }
            if (!hasOldColumn) return; // already migrated or fresh install

            System.out.println("Migrating legacy 'category' column...");
            Statement st = con.createStatement();

            // Add category_id column if missing (partial migration guard)
            try {
                st.executeUpdate(
                    "ALTER TABLE products ADD COLUMN category_id INT NOT NULL DEFAULT 0"
                );
            } catch (Exception ignored) { }

            // Map old text values → category ids by name (case-insensitive)
            st.executeUpdate(
                "UPDATE products p "
                + "JOIN categories c ON LOWER(c.name) = LOWER(p.category) "
                + "SET p.category_id = c.id"
            );

            // Rows with no match → 'Others'
            st.executeUpdate(
                "UPDATE products p "
                + "JOIN categories c ON c.name = 'Others' "
                + "SET p.category_id = c.id "
                + "WHERE p.category_id = 0"
            );

            // Add FK constraint now that all rows are valid
            try {
                st.executeUpdate(
                    "ALTER TABLE products "
                    + "ADD CONSTRAINT fk_product_category "
                    + "  FOREIGN KEY (category_id) REFERENCES categories(id)"
                );
            } catch (Exception ignored) { }

            // Drop the old column
            st.executeUpdate("ALTER TABLE products DROP COLUMN category");

            System.out.println("Migration complete.");

        } catch (Exception e) {
            System.err.println("Migration warning: " + e.getMessage());
        }
    }

    // ── Utility helpers ──────────────────────────────────────────────────────

    public void executeSQLQuery(String query, String message) {
        try (Connection con = getConnection();
             Statement st = con != null ? con.createStatement() : null) {
            if (st == null) return;
            int rows = st.executeUpdate(query);
            System.out.println((rows > 0 ? "Success: " : "Failed: ") + message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int executePreparedUpdate(String query, Object... parameters) {
        try (Connection con = getConnection();
             PreparedStatement ps = con != null ? con.prepareStatement(query) : null) {
            if (ps == null) return 0;
            for (int i = 0; i < parameters.length; i++) ps.setObject(i + 1, parameters[i]);
            return ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}