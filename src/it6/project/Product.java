/*
 * Sari-Sari Store Inventory System
 * Product Model Class
 */
package it6.project;

public class Product {
    private int    id;
    private String name;
    private int    categoryId;   // FK to categories.id
    private String category;     // resolved name via JOIN (read-only display)
    private double price;
    private int    quantity;

    // Full constructor (id + categoryId + resolved name)
    public Product(int id, String name, int categoryId, String categoryName,
                   double price, int quantity) {
        this.id         = id;
        this.name       = name;
        this.categoryId = categoryId;
        this.category   = categoryName;
        this.price      = price;
        this.quantity   = quantity;
    }

    // Constructor for new products (no id yet)
    public Product(String name, int categoryId, double price, int quantity) {
        this.id         = 0;
        this.name       = name;
        this.categoryId = categoryId;
        this.category   = "";
        this.price      = price;
        this.quantity   = quantity;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public int    getCategoryId() { return categoryId; }
    /** Resolved category name from the JOIN — used for display only. */
    public String getCategory()   { return category; }
    public double getPrice()      { return price; }
    public int    getQuantity()   { return quantity; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)                   { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setCategoryId(int categoryId)   { this.categoryId = categoryId; }
    public void setCategory(String category)    { this.category = category; }
    public void setPrice(double price)          { this.price = price; }
    public void setQuantity(int quantity)       { this.quantity = quantity; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', categoryId=" + categoryId
                + ", category='" + category + "', price=" + price
                + ", quantity=" + quantity + '}';
    }
}