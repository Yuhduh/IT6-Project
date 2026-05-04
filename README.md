# Sari-Sari Store Inventory System - Complete Implementation Guide

## 📋 Overview

This is a fully functional Java Swing-based inventory management system for a Sari-Sari store. The system includes a MySQL database backend with complete CRUD operations, a user-friendly GUI, and real-time stock monitoring.

## 🏗️ System Architecture

### Database Structure

- **Database Name**: `sari_sari_inventory`
- **Main Table**: `products`
  - `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
  - `name` (VARCHAR(100), NOT NULL)
  - `category` (VARCHAR(50), NOT NULL)
  - `price` (DECIMAL(10,2), NOT NULL)
  - `quantity` (INT, NOT NULL, DEFAULT 0)
  - `created_at` (TIMESTAMP)
  - `updated_at` (TIMESTAMP)

### Java Classes

#### 1. **MySQLConnection.java**

- Handles database connection using JDBC
- Automatically creates database and tables on first run
- Supports fallback passwords for flexibility
- Default user: root
- Supported passwords: "" (empty) or "carl"

#### 2. **Product.java**

- Data model class representing a product
- Contains fields: id, name, category, price, quantity
- Includes getters and setters for all fields

#### 3. **ProductDAO.java**

- Data Access Object for database operations
- **CRUD Operations**:
  - `insertProduct()` - Add new product
  - `updateProduct()` - Modify existing product
  - `deleteProduct()` - Remove product
  - `getAllProducts()` - Fetch all products
  - `getProductById()` - Get specific product
  - `searchProducts()` - Search by name/category
- **Statistics Methods**:
  - `getTotalProducts()` - Count all products
  - `getTotalItemsInStock()` - Sum all quantities
  - `getLowStockItems()` - Get items below threshold
  - `getLowStockCount()` - Count low stock items
- **Stock Operations**:
  - `updateProductQuantity()` - Update stock level
  - `addStock()` - Add quantity to product
  - `sellProduct()` - Deduct quantity with validation

#### 4. **Dashboard.java**

- Main landing page showing key statistics
- Displays:
  - Total products count
  - Total items in stock
  - Number of low stock items
  - List of items needing restock
- Navigation to other modules

#### 5. **ProductList.java**

- Complete product management interface
- Features:
  - JTable displaying all products
  - Search functionality (by name/category)
  - Add new product with validation
  - Edit existing products
  - Delete products with confirmation
  - Sell/Deduct stock with quantity validation
  - Refresh product list

#### 6. **StockIn.java**

- Dedicated module for adding stock
- Features:
  - Dropdown selection of products
  - Quantity input field
  - Add Stock button with validation
  - Automatic quantity update

#### 7. **StockMonitoring.java**

- Real-time inventory monitoring
- Two-section display:
  - **Low Stock Items** (< 10 units) - Red warning section
  - **Well-Stocked Items** (≥ 10 units) - Green success section
- Refresh button for real-time updates

#### 8. **IT6Project.java**

- Main entry point
- Launches Dashboard on startup

## 🚀 How to Use

### Prerequisites

- Java JDK 8 or higher
- MySQL Server installed and running
- MySQL JDBC Driver (mysql-connector-java)

### Setup Instructions

1. **Install MySQL JDBC Driver**
   - Download: `mysql-connector-java-x.x.x.jar`
   - Add to project classpath in NetBeans:
     - Right-click project → Properties
     - Libraries → Add JAR/Folder
     - Select the MySQL connector JAR file

2. **Configure Database Connection**
   - Edit `MySQLConnection.java` if needed
   - Default settings:
     - Server: localhost:3306
     - User: root
     - Password: "" (empty) or "carl"
   - Database will be created automatically

3. **Run the Application**
   - In NetBeans: Run Project (F6) or click Run button
   - Application launches with Dashboard

### User Features

#### Dashboard

- View all key metrics at a glance
- Quick access to all modules via navigation buttons

#### Product List

- **Add Product**: Click "+ Add Product" → Fill form → OK
- **Edit Product**: Select product → Click "✏ Edit" → Modify → OK
- **Delete Product**: Select product → Click "🗑 Delete" → Confirm
- **Sell Product**: Select product → Click "➖ Sell/Deduct" → Enter quantity → OK
- **Search**: Enter keyword → Click "Search" → View results
- **Refresh**: Click "Refresh" to reload all products

#### Stock In

- Select product from dropdown
- Enter quantity to add
- Click "+ Add Stock" to update inventory

#### Stock Monitoring

- View products with low stock (red section)
- View well-stocked products (green section)
- Click "Refresh" to update real-time data

## ✅ Validation Features

1. **Product Creation/Update**
   - No empty fields allowed
   - Quantity must be non-negative
   - Price must be valid decimal

2. **Stock Operations**
   - Quantity must be positive
   - Cannot sell more than available stock
   - Automatic warnings for negative quantities

3. **Search**
   - Case-insensitive search
   - Searches both name and category fields

## 📊 Sample Data

After first run, you can add sample products:

- Lucky Me Pancit Canton - Noodles - ₱15.00 - 8 units
- Ligo Sardines - Canned Goods - ₱20.00 - 5 units
- Skyflakes Biscuits - Biscuits - ₱7.00 - 25 units
- Alaska Milk - Beverages - ₱40.00 - 15 units

## 🔧 Technical Details

### Database Connection

- Uses JDBC for database connectivity
- Connection pooling not implemented (student project)
- Auto-reconnection handling for dropped connections

### UI Framework

- Java Swing (JFrame, JPanel, JTable, JButton, etc.)
- Custom layouts (BorderLayout, GridLayout, FlowLayout)
- Modal dialogs for data entry
- Color-coded status indicators

### Code Organization

- Package: `it6.project`
- Separation of concerns (Model, DAO, View)
- MVC-like architecture for maintainability

## 🐛 Troubleshooting

### Database Connection Failed

- Check if MySQL Server is running
- Verify credentials in MySQLConnection.java
- Ensure MySQL connector JAR is in classpath

### No Products Showing

- Click "Refresh" in Product List
- Check database connection
- Ensure products are added from Dashboard or ProductList

### Stock Not Updating

- Verify database connectivity
- Check for validation errors
- Ensure quantity values are valid integers

## 📝 Future Enhancements

Possible improvements for advanced implementations:

- User authentication and roles
- Product images/photos
- Transaction history logging
- Batch import/export (CSV)
- Email alerts for low stock
- Sales reports and analytics
- Multi-user access with permissions
- Backup and restore functionality

## 👨‍💻 Developer Information

- **Author**: Student - IT6 Project
- **Technology Stack**: Java, MySQL, Swing
- **Development Environment**: NetBeans IDE
- **Database**: MySQL 5.7+

## 📄 License

Educational Project - Feel free to modify and extend as needed.

---

**System Status**: ✅ Production Ready
**Last Updated**: 2026-04-25
**Version**: 1.0
