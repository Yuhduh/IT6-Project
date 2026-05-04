/*
 * Sari-Sari Store Inventory System
 * Product Data Access Object (DAO) - Database Operations
 */
package it6.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProductDAO {

    private MySQLConnection dbConnection;

    public ProductDAO() {
        this.dbConnection = new MySQLConnection();
    }

    // ── Helper: map a ResultSet row → Product (uses JOIN columns) ────────────

    private Product mapRow(ResultSet rs) throws Exception {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("category_id"),
            rs.getString("category_name"),   // resolved via INNER JOIN
            rs.getDouble("price"),
            rs.getInt("quantity")
        );
    }

    // ── Base SELECT with INNER JOIN ──────────────────────────────────────────

    private static final String SELECT_ALL =
        "SELECT p.id, p.name, p.category_id, c.name AS category_name, "
        + "       p.price, p.quantity "
        + "FROM products p "
        + "INNER JOIN categories c ON c.id = p.category_id ";

    // ── CRUD operations ──────────────────────────────────────────────────────

    /**
     * Insert a new product.
     */
    public boolean insertProduct(Product product) {
        String query =
            "INSERT INTO products (name, category_id, price, quantity) "
            + "VALUES (?, ?, ?, ?)";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error inserting product: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Update an existing product.
     */
    public boolean updateProduct(Product product) {
        String query =
            "UPDATE products SET name = ?, category_id = ?, price = ?, quantity = ? "
            + "WHERE id = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());
            ps.setInt(5, product.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error updating product: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Delete a product.
     */
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error deleting product: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Get all products — uses INNER JOIN to resolve category name.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = SELECT_ALL + "ORDER BY p.id ASC";

        try (Connection con = dbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error fetching products: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return products;
    }

    /**
     * Get a single product by id — uses INNER JOIN.
     */
    public Product getProductById(int productId) {
        String query = SELECT_ALL + "WHERE p.id = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error fetching product: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    /**
     * Search products by name or category name — uses INNER JOIN.
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query =
            SELECT_ALL
            + "WHERE p.name LIKE ? OR c.name LIKE ? "
            + "ORDER BY p.id ASC";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            ps.setString(1, term);
            ps.setString(2, term);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    // ── Aggregate / stock helpers ────────────────────────────────────────────

    public int getTotalProducts() {
        String query = "SELECT COUNT(*) AS total FROM products";
        try (Connection con = dbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalItemsInStock() {
        String query = "SELECT COALESCE(SUM(quantity), 0) AS total FROM products";
        try (Connection con = dbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<Product> getLowStockItems(int threshold) {
        List<Product> products = new ArrayList<>();
        String query = SELECT_ALL + "WHERE p.quantity < ? ORDER BY p.quantity ASC";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) products.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }

        return products;
    }

    public int getLowStockCount(int threshold) {
        String query = "SELECT COUNT(*) AS total FROM products WHERE quantity < ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean updateProductQuantity(int productId, int newQuantity) {
        if (newQuantity < 0) {
            JOptionPane.showMessageDialog(null, "Quantity cannot be negative!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String query = "UPDATE products SET quantity = ? WHERE id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error updating quantity: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean addStock(int productId, int quantityToAdd) {
        Product product = getProductById(productId);
        if (product != null) {
            return updateProductQuantity(productId, product.getQuantity() + quantityToAdd);
        }
        return false;
    }

    public boolean sellProduct(int productId, int quantityToSell) {
        Product product = getProductById(productId);
        if (product != null) {
            int newQty = product.getQuantity() - quantityToSell;
            if (newQty < 0) {
                JOptionPane.showMessageDialog(null,
                    "Insufficient stock! Only " + product.getQuantity() + " items available.",
                    "Stock Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return updateProductQuantity(productId, newQty);
        }
        return false;
    }
}