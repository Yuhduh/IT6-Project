# Testing & Troubleshooting Guide

## 🧪 Testing Checklist

### Initial Setup Test

- [ ] Application launches without errors
- [ ] Dashboard displays with four stat panels visible
- [ ] Navigation buttons are clickable
- [ ] No database error messages in console

### Product Management Test

- [ ] Can add a product with all fields populated
- [ ] Product appears in Product List table immediately
- [ ] Can search for added product
- [ ] Can edit product details
- [ ] Can delete product with confirmation dialog
- [ ] Product removed from list after deletion

### Stock Operations Test

- [ ] Can select product from dropdown in Stock In
- [ ] Can add stock to product
- [ ] Quantity updates in Product List after adding stock
- [ ] Can deduct stock from Product List using "Sell/Deduct"
- [ ] Cannot sell more than available quantity (error shown)

### Monitoring Features Test

- [ ] Dashboard shows correct total products count
- [ ] Dashboard shows correct total stock count
- [ ] Low stock items (< 10) appear in Stock Monitoring red section
- [ ] Well-stocked items (>= 10) appear in Stock Monitoring green section
- [ ] Stock Monitoring refreshes with latest data

### Validation Tests

- [ ] Empty fields validation works (all fields required)
- [ ] Negative quantity prevention works
- [ ] Non-numeric inputs rejected for price/quantity
- [ ] Search filters products correctly
- [ ] Maximum quantity/price values accepted

### Navigation Tests

- [ ] Can navigate between all modules
- [ ] Window properly closes when switching screens
- [ ] All buttons lead to correct screens
- [ ] Dashboard button returns to dashboard from any screen

## 🐛 Common Issues & Solutions

### Issue 1: "Unable to connect to MySQL"

**Symptoms**: Error message on startup, no database operations work

**Solutions**:

1. Check if MySQL is running:

   ```bash
   mysql -u root
   ```

   If error, start MySQL service

2. Verify credentials in MySQLConnection.java:

   ```java
   private static final String USER = "root";
   private static final String[] PASSWORD_CANDIDATES = {"", "carl"};
   ```

   - Try with empty password first
   - If that fails, try with "carl"

3. Ensure JDBC driver is installed:
   - Right-click project → Properties
   - Libraries tab
   - Confirm mysql-connector-java.jar is listed

### Issue 2: "No tables found" / Products not showing

**Symptoms**: Product List table is empty, no data visible

**Solutions**:

1. Click "Refresh" button in Product List
2. Check database was created:
   ```bash
   mysql -u root -e "SHOW DATABASES;" | grep sari_sari
   ```
3. Force database recreation:
   - Delete sari_sari_inventory database in MySQL
   - Restart application (will recreate)

4. Add sample data manually via Product List

### Issue 3: Stock quantity not updating

**Symptoms**: Add stock or sell doesn't change quantity values

**Solutions**:

1. Verify database connection is active
2. Click "Refresh" to reload data from database
3. Check for console error messages
4. Confirm quantity value is numeric and > 0

### Issue 4: Application crashes when adding product

**Symptoms**: Error dialog appears or app closes

**Solutions**:

1. Check console for exception details
2. Ensure all fields are filled (no empty values)
3. Check price is valid decimal (e.g., 15.50 not 15,50)
4. Verify database connection

### Issue 5: Search not finding products

**Symptoms**: Enter search term, no results appear

**Solutions**:

1. Search is case-insensitive (should work)
2. Try partial name match
3. Verify product exists by clicking Refresh
4. Search includes both name AND category fields

### Issue 6: "Column not found" error

**Symptoms**: SQL error mentioning missing columns

**Solutions**:

1. Database was created with old schema
2. Backup data, then delete database:
   ```bash
   mysql -u root -e "DROP DATABASE sari_sari_inventory;"
   ```
3. Restart application to recreate

## 🔍 Debug Mode Testing

### Enable Console Logging

Add to MySQLConnection.java after successful operations:

```java
System.out.println("Database operation completed successfully");
```

### Test Database Query Directly

```bash
# Connect to MySQL
mysql -u root

# Select database
USE sari_sari_inventory;

# View all products
SELECT * FROM products;

# Check table structure
DESCRIBE products;
```

### Monitor Database Changes in Real-time

```bash
# Terminal 1: Start monitoring log
mysql -u root -e "SET SESSION sql_mode='';
SELECT * FROM products;"

# Terminal 2: Run application and make changes
# Terminal 1: Re-run SELECT query to see changes
```

## 📋 Functional Test Scenarios

### Scenario 1: Complete Product Lifecycle

1. ✓ Dashboard opens successfully
2. ✓ Go to Product List
3. ✓ Click "+ Add Product"
4. ✓ Add "Test Product" | "Category" | 10.00 | 5
5. ✓ Product appears in table
6. ✓ Go to Stock In
7. ✓ Select "Test Product"
8. ✓ Add 10 quantity
9. ✓ Check Product List → quantity is now 15
10. ✓ Go to Stock Monitoring
11. ✓ Product appears in "Well-Stocked" section
12. ✓ Return to Product List
13. ✓ Sell 10 units
14. ✓ Quantity becomes 5
15. ✓ Check Stock Monitoring → Product now in "Low Stock"
16. ✓ Delete product
17. ✓ Product list is empty
18. ✓ Dashboard stats updated

**Expected Result**: All steps complete without errors ✅

### Scenario 2: Data Validation

1. ✓ Try to add product with empty name → Error
2. ✓ Try to add product with negative price → Error
3. ✓ Try to add product with text in quantity → Error
4. ✓ Try to sell more than available → Error
5. ✓ Try to add 0 stock → Error

**Expected Result**: All validations work ✅

### Scenario 3: Search Functionality

1. ✓ Add products: "Milk", "Cheese", "Bread"
2. ✓ Search "Milk" → Shows only Milk
3. ✓ Search "lk" → Shows Milk (case-insensitive partial match)
4. ✓ Search "Dairy" → Shows only Cheese and Milk if category is "Dairy"
5. ✓ Click Refresh → Shows all products again

**Expected Result**: Search works accurately ✅

## ✅ Pre-Deployment Checklist

Before considering the system production-ready:

- [ ] All CRUD operations work without errors
- [ ] Database persists data after application restart
- [ ] Navigation between all modules works
- [ ] Validation prevents invalid data entry
- [ ] Stock monitoring accurate (< 10 is low)
- [ ] Dashboard statistics match database totals
- [ ] Search functionality works
- [ ] Can handle 100+ products without lag
- [ ] No memory leaks after long use
- [ ] Error messages are clear and helpful
- [ ] No console warnings or exceptions

## 📊 Performance Testing

### Load Test (Optional)

1. Add 100 products programmatically
2. Measure Product List load time
3. Measure search performance
4. Measure Stock Monitoring rendering time

### Expected Performance:

- Product List load: < 1 second
- Search results: < 500ms
- Stock Monitoring: < 1 second

## 🔐 Data Integrity Check

Verify database integrity:

```sql
-- Check for orphaned records
SELECT * FROM products WHERE id IS NULL;

-- Check for duplicate entries
SELECT name, COUNT(*) FROM products GROUP BY name HAVING COUNT(*) > 1;

-- Verify quantity values
SELECT * FROM products WHERE quantity < 0;
```

All queries should return empty results for healthy database.

---

**Last Updated**: 2026-04-25
**Difficulty**: Beginner to Intermediate
