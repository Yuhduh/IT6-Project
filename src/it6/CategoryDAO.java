/*
 * Sari-Sari Store Inventory System
 * Category Data Access Object (DAO)
 */
package it6.project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CategoryDAO {

    private MySQLConnection dbConnection;

    public CategoryDAO() {
        this.dbConnection = new MySQLConnection();
    }

    /**
     * Returns all categories ordered by name.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name FROM categories ORDER BY name ASC";

        try (Connection con = dbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error fetching categories: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return categories;
    }
}