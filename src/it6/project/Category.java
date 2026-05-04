/*
 * Sari-Sari Store Inventory System
 * Category Model Class
 */
package it6.project;

public class Category {
    private int id;
    private String name;

    public Category(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public int getId()     { return id; }
    public String getName(){ return name; }

    /** Shown in the JComboBox drop-down. */
    @Override
    public String toString() { return name; }
}