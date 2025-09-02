-- Enhanced Business Intelligence Database Schema and Data
-- Drop existing tables if they exist
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- Create customers table
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    customer_segment VARCHAR(50) CHECK (customer_segment IN ('Premium', 'Standard', 'Basic'))
);

-- Create enhanced products table
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    description TEXT,
    manufacturer VARCHAR(255)
);

-- Create enhanced sales table
CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES products(id),
    customer_id INTEGER REFERENCES customers(id),
    sale_date DATE NOT NULL,
    revenue DECIMAL(10, 2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    region VARCHAR(100),
    sales_person VARCHAR(255)
);

-- Insert sample customers
INSERT INTO customers (customer_name, email, phone, address, city, country, customer_segment) VALUES
('Tech Solutions Inc', 'contact@techsolutions.com', '+1-555-0101', '123 Tech Street', 'San Francisco', 'USA', 'Premium'),
('Global Enterprises', 'sales@globalent.com', '+1-555-0102', '456 Business Ave', 'New York', 'USA', 'Premium'),
('StartUp Labs', 'hello@startuplabs.com', '+1-555-0103', '789 Innovation Blvd', 'Austin', 'USA', 'Standard'),
('Creative Agency', 'info@creativeagency.com', '+1-555-0104', '321 Design Lane', 'Los Angeles', 'USA', 'Standard'),
('Home Office Solutions', 'support@homeoffice.com', '+1-555-0105', '654 Remote Work St', 'Denver', 'USA', 'Basic'),
('Educational Institute', 'admin@eduinstitute.com', '+1-555-0106', '987 Learning Way', 'Boston', 'USA', 'Standard'),
('Healthcare Corp', 'procurement@healthcorp.com', '+1-555-0107', '147 Medical Center Dr', 'Chicago', 'USA', 'Premium'),
('Retail Chain Ltd', 'buyers@retailchain.com', '+1-555-0108', '258 Commerce Plaza', 'Miami', 'USA', 'Standard'),
('Manufacturing Co', 'orders@mfgco.com', '+1-555-0109', '369 Industrial Park', 'Detroit', 'USA', 'Premium'),
('Small Business Hub', 'owner@smallbizhub.com', '+1-555-0110', '741 Main Street', 'Portland', 'USA', 'Basic');

-- Insert enhanced products
INSERT INTO products (product_name, category, price, description, manufacturer) VALUES
-- Electronics
('Laptop Pro 15"', 'Electronics', 1299.99, 'High-performance laptop with 16GB RAM and 512GB SSD', 'TechCorp'),
('Smartphone X', 'Electronics', 899.99, 'Latest smartphone with 5G connectivity and advanced camera', 'MobileInc'),
('Monitor 4K Ultra', 'Electronics', 449.99, '27-inch 4K monitor with HDR support', 'DisplayTech'),
('Wireless Headphones Premium', 'Electronics', 249.99, 'Noise-canceling wireless headphones with 30hr battery', 'AudioMax'),
('Tablet Pro', 'Electronics', 649.99, '11-inch tablet with stylus support and 256GB storage', 'TechCorp'),
('Smart Watch Series 5', 'Electronics', 399.99, 'Health tracking smartwatch with GPS and cellular', 'WearableTech'),
('Gaming Laptop', 'Electronics', 1899.99, 'High-end gaming laptop with RTX graphics', 'GameTech'),
('Wireless Mouse', 'Electronics', 79.99, 'Ergonomic wireless mouse with precision tracking', 'InputDevices'),

-- Appliances
('Coffee Maker Deluxe', 'Appliances', 199.99, 'Programmable coffee maker with built-in grinder', 'KitchenPro'),
('Air Purifier Pro', 'Appliances', 299.99, 'HEPA air purifier for large rooms up to 500 sq ft', 'CleanAir'),
('Smart Thermostat', 'Appliances', 249.99, 'WiFi-enabled programmable thermostat with app control', 'HomeSmart'),
('Robotic Vacuum', 'Appliances', 399.99, 'Self-navigating robotic vacuum with mapping technology', 'CleanBot'),
('Microwave Convection', 'Appliances', 179.99, 'Countertop convection microwave with smart sensor', 'KitchenPro'),

-- Furniture
('Ergonomic Office Chair', 'Furniture', 349.99, 'Adjustable office chair with lumbar support and breathable mesh', 'OfficePro'),
('Standing Desk Adjustable', 'Furniture', 499.99, 'Electric height-adjustable standing desk 48"x30"', 'DeskMaster'),
('Conference Table', 'Furniture', 899.99, '8-person conference table with cable management', 'OfficePro'),
('Bookshelf Unit', 'Furniture', 199.99, '5-shelf wooden bookshelf unit with adjustable shelves', 'FurniturePlus'),
('Executive Desk', 'Furniture', 749.99, 'Large executive desk with drawers and hutch', 'OfficePro'),

-- Accessories
('USB-C Hub', 'Accessories', 89.99, '7-in-1 USB-C hub with HDMI, USB 3.0, and PD charging', 'ConnectTech'),
('Wireless Charger', 'Accessories', 49.99, '15W fast wireless charging pad with LED indicator', 'PowerTech'),
('Laptop Stand', 'Accessories', 59.99, 'Adjustable aluminum laptop stand for better ergonomics', 'ErgoAccessories'),
('Cable Management Kit', 'Accessories', 29.99, 'Complete cable organization solution for desk setup', 'OrganizeTech'),
('Bluetooth Speaker', 'Accessories', 129.99, 'Portable Bluetooth speaker with 360-degree sound', 'AudioMax'),
('Webcam HD', 'Accessories', 89.99, 'Full HD 1080p webcam with auto-focus and noise reduction', 'VideoTech');

-- Insert comprehensive sales data for better BI analytics
INSERT INTO sales (product_id, customer_id, sale_date, revenue, quantity, region, sales_person) VALUES
-- Q1 2025 Sales (January - March)
(1, 1, '2025-01-05', 1299.99, 1, 'West', 'John Smith'),
(2, 2, '2025-01-10', 899.99, 1, 'East', 'Sarah Johnson'),
(3, 3, '2025-01-15', 449.99, 1, 'South', 'Mike Wilson'),
(4, 4, '2025-01-20', 249.99, 2, 'West', 'Emily Davis'),
(5, 5, '2025-01-25', 649.99, 1, 'Central', 'David Brown'),
(9, 6, '2025-02-01', 199.99, 1, 'East', 'Lisa Garcia'),
(10, 7, '2025-02-05', 299.99, 1, 'Central', 'Tom Anderson'),
(11, 8, '2025-02-10', 249.99, 1, 'South', 'Jennifer Lee'),
(14, 9, '2025-02-15', 349.99, 2, 'West', 'John Smith'),
(15, 10, '2025-02-20', 499.99, 1, 'East', 'Sarah Johnson'),
(1, 2, '2025-03-01', 1299.99, 1, 'East', 'Mike Wilson'),
(6, 3, '2025-03-05', 399.99, 1, 'South', 'Emily Davis'),
(7, 4, '2025-03-10', 1899.99, 1, 'West', 'David Brown'),
(18, 5, '2025-03-15', 899.99, 1, 'Central', 'Lisa Garcia'),

-- Q2 2025 Sales (April - June)
(2, 1, '2025-04-01', 899.99, 2, 'West', 'John Smith'),
(3, 6, '2025-04-05', 449.99, 1, 'East', 'Sarah Johnson'),
(4, 7, '2025-04-10', 249.99, 1, 'Central', 'Tom Anderson'),
(8, 8, '2025-04-15', 79.99, 3, 'South', 'Jennifer Lee'),
(9, 9, '2025-04-20', 199.99, 2, 'West', 'John Smith'),
(12, 10, '2025-04-25', 399.99, 1, 'East', 'Mike Wilson'),
(13, 1, '2025-05-01', 179.99, 1, 'West', 'Emily Davis'),
(14, 2, '2025-05-05', 349.99, 3, 'East', 'Sarah Johnson'),
(15, 3, '2025-05-10', 499.99, 2, 'South', 'David Brown'),
(19, 4, '2025-05-15', 199.99, 1, 'West', 'Lisa Garcia'),
(21, 5, '2025-05-20', 89.99, 2, 'Central', 'Tom Anderson'),
(22, 6, '2025-05-25', 49.99, 4, 'East', 'Jennifer Lee'),
(1, 7, '2025-06-01', 1299.99, 1, 'Central', 'John Smith'),
(5, 8, '2025-06-05', 649.99, 2, 'South', 'Mike Wilson'),
(6, 9, '2025-06-10', 399.99, 1, 'West', 'Emily Davis'),
(7, 10, '2025-06-15', 1899.99, 1, 'East', 'Sarah Johnson'),

-- Q3 2025 Sales (July - September) - Recent data
(3, 1, '2025-07-01', 449.99, 2, 'West', 'David Brown'),
(4, 2, '2025-07-05', 249.99, 3, 'East', 'Lisa Garcia'),
(10, 3, '2025-07-10', 299.99, 1, 'South', 'Tom Anderson'),
(11, 4, '2025-07-15', 249.99, 2, 'West', 'Jennifer Lee'),
(14, 5, '2025-07-20', 349.99, 1, 'Central', 'John Smith'),
(20, 6, '2025-07-25', 129.99, 2, 'East', 'Mike Wilson'),
(2, 7, '2025-08-01', 899.99, 1, 'Central', 'Emily Davis'),
(8, 8, '2025-08-05', 79.99, 5, 'South', 'Sarah Johnson'),
(13, 9, '2025-08-10', 179.99, 1, 'West', 'David Brown'),
(15, 10, '2025-08-15', 499.99, 2, 'East', 'Lisa Garcia'),
(23, 1, '2025-08-20', 59.99, 3, 'West', 'Tom Anderson'),
(24, 2, '2025-08-25', 89.99, 1, 'East', 'Jennifer Lee');

-- Create indexes for better query performance
CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_sales_product ON sales(product_id);
CREATE INDEX idx_sales_customer ON sales(customer_id);
CREATE INDEX idx_sales_region ON sales(region);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_customers_segment ON customers(customer_segment);

-- Create some useful views for common BI queries
CREATE OR REPLACE VIEW monthly_revenue AS
SELECT 
    DATE_TRUNC('month', sale_date) as month,
    SUM(revenue) as total_revenue,
    COUNT(*) as total_orders,
    AVG(revenue) as avg_order_value
FROM sales 
GROUP BY DATE_TRUNC('month', sale_date)
ORDER BY month;

CREATE OR REPLACE VIEW product_performance AS
SELECT 
    p.product_name,
    p.category,
    p.price,
    COUNT(s.id) as total_orders,
    SUM(s.quantity) as total_quantity_sold,
    SUM(s.revenue) as total_revenue,
    AVG(s.revenue) as avg_revenue_per_sale
FROM products p
LEFT JOIN sales s ON p.id = s.product_id
GROUP BY p.id, p.product_name, p.category, p.price
ORDER BY total_revenue DESC NULLS LAST;

CREATE OR REPLACE VIEW customer_analytics AS
SELECT 
    c.customer_name,
    c.customer_segment,
    c.city,
    c.country,
    COUNT(s.id) as total_orders,
    SUM(s.revenue) as total_spent,
    AVG(s.revenue) as avg_order_value,
    MAX(s.sale_date) as last_purchase_date
FROM customers c
LEFT JOIN sales s ON c.id = s.customer_id
GROUP BY c.id, c.customer_name, c.customer_segment, c.city, c.country
ORDER BY total_spent DESC NULLS LAST;