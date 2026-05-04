# Database Schema Documentation

## Database Name

```sql
sari_sari_inventory
```

## Tables

### products Table

**Purpose**: Store all product information for the inventory system

**SQL Definition**:

```sql
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Column Definitions**:

| Column       | Type          | Constraints                 | Purpose                                |
| ------------ | ------------- | --------------------------- | -------------------------------------- |
| `id`         | INT           | PRIMARY KEY, AUTO_INCREMENT | Unique product identifier              |
| `name`       | VARCHAR(100)  | NOT NULL                    | Product name (e.g., "Lucky Me Pancit") |
| `category`   | VARCHAR(50)   | NOT NULL                    | Product category (e.g., "Noodles")     |
| `price`      | DECIMAL(10,2) | NOT NULL                    | Unit price in Philippine Peso (₱)      |
| `quantity`   | INT           | NOT NULL, DEFAULT 0         | Current stock quantity                 |
| `created_at` | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP   | Record creation date/time              |
| `updated_at` | TIMESTAMP     | AUTO UPDATE                 | Last modification date/time            |

## Useful Queries

### View All Products

```sql
SELECT * FROM products ORDER BY id ASC;
```

### Get Low Stock Items (below 10)

```sql
SELECT * FROM products WHERE quantity < 10 ORDER BY quantity ASC;
```

### Get Well-Stocked Items (10 or more)

```sql
SELECT * FROM products WHERE quantity >= 10 ORDER BY name;
```

### Calculate Total Stock Value

```sql
SELECT SUM(price * quantity) AS total_value FROM products;
```

### Get Product Count

```sql
SELECT COUNT(*) AS total_products FROM products;
```

### Get Total Items in Stock

```sql
SELECT SUM(quantity) AS total_stock FROM products;
```

### Search Products by Name

```sql
SELECT * FROM products WHERE name LIKE '%Pancit%';
```

### Get Top 5 Products by Quantity

```sql
SELECT * FROM products ORDER BY quantity DESC LIMIT 5;
```

### Get Products by Category

```sql
SELECT * FROM products WHERE category = 'Noodles';
```

### Update Product Price

```sql
UPDATE products SET price = 15.50 WHERE name = 'Lucky Me Pancit Canton';
```

### Increase Stock for a Product

```sql
UPDATE products SET quantity = quantity + 10 WHERE id = 1;
```

### Decrease Stock (Sell)

```sql
UPDATE products SET quantity = quantity - 5 WHERE id = 1;
```

### Delete Low Stock Products (Keep careful!)

```sql
DELETE FROM products WHERE quantity = 0;
```

### Get Product Statistics

```sql
SELECT
    COUNT(*) AS total_products,
    SUM(quantity) AS total_items_in_stock,
    AVG(price) AS average_price,
    MIN(price) AS min_price,
    MAX(price) AS max_price
FROM products;
```

## Data Types Explanation

### INT

- Used for: product id, quantity
- Range: -2,147,483,648 to 2,147,483,647
- Sufficient for inventory quantities

### VARCHAR(n)

- Used for: name (100), category (50)
- Variable length string up to n characters
- 100 for names to allow longer product names
- 50 for categories (sufficient for common values)

### DECIMAL(10,2)

- Used for: price
- 10 total digits, 2 after decimal point
- Range: -99,999,999.99 to 99,999,999.99
- Ideal for monetary values (no floating-point errors)

### TIMESTAMP

- Used for: created_at, updated_at
- Auto-updates on record modification
- Useful for tracking data changes

## Indexes (For Performance)

Current index (implicit via PRIMARY KEY):

- `id` - Used for fast lookups

Additional indexes that could be added for larger datasets:

```sql
-- Speed up name searches
CREATE INDEX idx_product_name ON products(name);

-- Speed up category filtering
CREATE INDEX idx_product_category ON products(category);

-- Speed up low stock queries
CREATE INDEX idx_product_quantity ON products(quantity);
```

## Constraints & Validation

**Database Level**:

- `NOT NULL` constraints ensure no missing critical data
- `PRIMARY KEY` ensures unique products
- `DECIMAL(10,2)` prevents invalid price formats

**Application Level** (ProductDAO.java):

- Quantity must be >= 0 (no negative stock)
- All fields must be non-empty when creating products
- Price must be valid decimal number
- Cannot sell more than available quantity

## Sample Data

```sql
INSERT INTO products (name, category, price, quantity) VALUES
('Lucky Me Pancit Canton', 'Noodles', 15.00, 8),
('Ligo Sardines', 'Canned Goods', 20.00, 5),
('Skyflakes Biscuits', 'Biscuits', 7.00, 25),
('Alaska Milk', 'Beverages', 40.00, 15),
('Piattos Cheese', 'Snacks', 12.00, 3),
('Green Peas', 'Vegetables', 25.00, 12);
```

## Backup & Restore

### Backup Database

```bash
mysqldump -u root -p sari_sari_inventory > backup.sql
# When prompted for password, enter "carl" (or your password)
```

### Restore Database

```bash
mysql -u root -p sari_sari_inventory < backup.sql
```

## Database Connection (JDBC)

**Connection String**:

```
jdbc:mysql://localhost:3306/sari_sari_inventory
```

**Credentials**:

- Username: root
- Password: "" (empty) or "carl"
- Driver: com.mysql.cj.jdbc.Driver

## Performance Considerations

Current state (optimal for small datasets):

- No complex joins needed
- Single table design
- Suitable for < 10,000 products

For scaling:

- Add audit_log table for transaction history
- Add categories table (normalized)
- Add suppliers table
- Implement data archiving for old records

---

**Last Updated**: 2026-04-25
**Version**: 1.0
