# Quick Start Guide - Sari-Sari Store Inventory System

## ⚡ Get Running in 5 Minutes

### Step 1: Prepare MySQL (30 seconds)

```bash
# Ensure MySQL Server is running
# On Windows: Start MySQL Server (Services or MySQL Workbench)
# On Mac: brew services start mysql
# On Linux: sudo systemctl start mysql
```

### Step 2: Add MySQL JDBC Driver (1 minute)

1. Download: `mysql-connector-java-8.0.23.jar` (or latest)
2. In NetBeans:
   - Right-click project → Properties
   - Go to Libraries tab
   - Click "Add JAR/Folder"
   - Select the downloaded JAR
   - Click OK

### Step 3: Run the Project (30 seconds)

```
Press F6 or click Run Project
```

### Step 4: Use the Application (3 minutes)

- **Dashboard opens first** - See overview
- **Add your first product**: Go to Product List → + Add Product
- **Add stock**: Go to Stock In → Select product → Enter quantity
- **Monitor inventory**: Go to Stock Monitoring → See status

## 🎯 Common Tasks

### Add a Product

1. Product List → + Add Product
2. Fill in: Name, Category, Price, Quantity
3. Click OK

### Update Stock

1. Stock In tab
2. Select product from dropdown
3. Enter quantity to add
4. Click + Add Stock

### Check Low Stock Items

1. Stock Monitoring tab
2. See red section for items below 10 units
3. Click Refresh for latest data

### Delete a Product

1. Product List tab
2. Click on product row to select
3. Click 🗑 Delete
4. Confirm deletion

## 📊 Sample Products (To Test)

Try adding these test products:

- **Lucky Me Pancit Canton** | Noodles | ₱15.00 | 8 qty
- **Ligo Sardines** | Canned Goods | ₱20.00 | 5 qty
- **Skyflakes** | Biscuits | ₱7.00 | 25 qty
- **Alaska Milk** | Beverages | ₱40.00 | 15 qty

After adding, go to Stock Monitoring to see:

- Low Stock section: First 2 items (red)
- Well-Stocked section: Last 2 items (green)

## ⚙️ MySQL Configuration

**File**: MySQLConnection.java

Default settings:

```java
Server: localhost:3306
Database: sari_sari_inventory (auto-created)
User: root
Password: "" (empty) or "carl"
```

To change password:

1. Open MySQLConnection.java
2. Edit PASSWORD_CANDIDATES array
3. Save and restart

## ✅ Verify Installation

All working if:

- ✓ Dashboard loads without errors
- ✓ Can add a product without database errors
- ✓ Product appears in Product List table
- ✓ Stock Monitoring shows the product

## 🔍 Debug Tips

**No products showing?**

- Click Refresh button
- Check console for error messages

**Database connection failed?**

- Ensure MySQL is running: `mysql -u root` in terminal
- Check MySQLConnection.java credentials
- Verify JDBC driver is added to project

**Table won't populate?**

- Stop and restart the application
- Database/tables auto-create on startup

## 📱 Interface Overview

```
┌─────────────────────────────────────────┐
│  Sari-Sari Store Inventory System       │
├─────────────────────────────────────────┤
│ [Dashboard] [Product List] [Stock In] [Stock Monitoring] │
├─────────────────────────────────────────┤
│                                         │
│         Main Content Area               │
│                                         │
└─────────────────────────────────────────┘
```

## 🚨 Important Notes

1. **First Run**: Application automatically creates database and tables
2. **Product Quantity**: Cannot be negative
3. **Low Stock Threshold**: Fixed at 10 units
4. **Search**: Case-insensitive, searches name and category
5. **Data Persists**: All changes saved to database

## 📞 Support

Check README.md for detailed documentation
Or review code comments in each Java file

---

**Happy Inventory Managing! 📦**
