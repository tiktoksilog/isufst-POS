SCOPE AND LIMITATIONS
Scope
• Java desktop POS application.
• MySQL database: users, categories, products, sales and sale_items.
• Login with ADMIN and CASHIER roles.
• Product and category organization.
• Recording of sales, quantities, prices, payment and change.
• Sales report filtered by date.

Limitations
• Designed for the canteen workflow represented in the project.
• Requires a running MySQL server and a configured JDBC connection.
• Local database settings use localhost by default.
• The database script does not define separate customer, supplier, inventory-movement or payment tables.


TARGET USERS
Administrator
• Manages products and views sales information.
• Maintains the product list and stock values.

Cashier
• Logs in to the POS.
• Processes canteen purchases and payments.

Canteen personnel
• May use sales summaries to review recorded transactions, subject to assigned access.


ENTITY RELATIONSHIP DIAGRAM (ERD)
USERS
PK user_id
username (unique)
full_name, role, created_at

SALES
PK sale_id
FK user_id
sale_date, total, paid, change

SALE_ITEMS
PK sale_item_id
FK sale_id, FK product_id
quantity, unit_price, subtotal

CATEGORIES
PK category_id
name (unique)

CATEGORIES
PK category_id
name (unique) 

<img width="1473" height="830" alt="Screenshot 2026-11-26 103138" src="https://github.com/user-attachments/assets/bed0a462-191e-4f94-be6b-927355360c84" />
