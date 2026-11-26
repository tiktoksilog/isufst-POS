ISUFST POS -- Canteen Point of Sale System
A Java desktop application for handling canteen sales, product
information, inventory, and daily transaction records. The system
connects to a MySQL database and provides separate access for
administrators and cashiers.

Technologies: Java 23 · Java Swing · MySQL · JDBC · Maven
1. Project Overview
ISUFST POS is designed to make canteen transactions faster and keep
sales and product records organized in one application. It supports the
regular checkout process, from selecting products and reviewing an order
to accepting payment and displaying a receipt.

The system also helps administrators maintain product information and
review sales for a selected date.
Problem
Handling sales and inventory manually can make it difficult to keep
transaction records accurate, monitor available stock, and review daily
sales. ISUFST POS provides a centralized way to record purchases, update
inventory, and retrieve transaction details.
Intended Users
- Administrator -- processes sales, manages products, and views
  sales reports.
- Cashier -- logs in, searches and selects products, prepares
  customer orders, and completes payments.
Both roles use the point-of-sale screen. Administrator accounts have
additional access to product management.
2. Objectives
The project aims to:
- Record customer purchases in an organized format.
- Calculate order totals, cash received, and change.
- Update product stock after a successful transaction.
- Allow administrators to add, update, search, and manage products.
- Provide date-based sales reports for reviewing completed
  transactions.
3. Main Features
Point-of-Sale (POS)
- Browse products displayed with their prices and available stock.
- Search for products by name or filter them by category.
- Add products and quantities to the shopping cart.
- Review item quantities, subtotals, and the overall total.
- Edit quantities, remove items, or clear an unfinished cart.
- Enter the cash received and calculate the customer's change.
- Display a receipt containing the receipt number, date, cashier,
  purchased items, total, cash received, and change.
Product Management
Available to administrator accounts:
- Add products with a name, category, price, stock quantity, and
  active status.
- Select existing products and load their information for editing.
- Update product details and refresh the product list.
- Search for products.
- Delete products when permitted. If a product already has sales
  records, the system marks it inactive instead of removing its sales
  history.
Sales Reports
- Filter transactions by date using the yyyy-MM-dd format or the
  Today option.
- View receipt number, transaction date and time, cashier, total, cash
  received, and change.
- Select a transaction to review its purchased products, unit prices,
  quantities, and subtotals.
- View the transaction count and total sales for the selected date.
4. Sales Workflow
A typical transaction follows these steps:
1. Login -- The cashier enters their account details.
2. Select Products -- Products and quantities are added to the
   order.
3. Review Order -- The cashier checks the cart and total amount.
4. Payment -- Cash received is entered, and the system calculates
   the change.
5. Save Transaction -- The sale and its items are stored, and
   product stock is reduced.
6. Receipt -- The receipt is displayed and the cart is cleared.
5. Project Scope and Limitations
Scope
ISUFST POS focuses on the daily operations of a canteen point-of-sale
system. Its documented functions include sales processing, product and
stock management, receipt display, user login, and date-based sales
reporting.
Considerations
- The system requires a working MySQL database connection.
- Product and sales information depend on the accuracy of the data
  entered.
- The application is designed as a Java desktop program, not an
  external online ordering platform.
- The default database connection settings should be changed to match
  the local MySQL installation.
- The included sample accounts are for testing. Use secure credentials
  before any real deployment.
6. System Structure
The project separates the user interface, application operations, and
database access.
  Layer                               Responsibility
  View                            Java Swing windows: LoginFrame,
POSFrame, ProductFrame, and
SalesReportFrame.

  Controller                      Handles login, checkout, product
actions, and sales report requests.

  DAO                             Performs database operations using
SQL and JDBC.
Database                        MySQL stores user accounts,
products, categories, sales, and
sale items.
7. Database Design
The database uses five main tables to organize the system's information.
  Table                               Purpose
  users                             Stores login accounts, user names,
and account roles.

  categories                        Stores product category names.

  products                          Stores product information,
category, price, stock, and active
status.

  sales                             Stores one record for each
completed transaction.
sale_items                        Stores the individual products
included in each sale.
Table Relationships
- Products are associated with categories.
- Sales are associated with the user who processed them.
- Sale items are associated with their corresponding sale and product.
Primary and foreign key relationships connect the records so that
transaction details can be retrieved together.
8. Transaction Handling
The checkout process includes checks to help maintain consistent
transaction records:
- A sale cannot proceed with an empty cart.
- The cash amount must be valid and at least equal to the order total.
- Product stock is checked again when the sale is saved.
- The sale, sale items, and stock deductions are handled in a database
  transaction.
- If a database operation fails, the transaction is rolled back to
  prevent partial changes from being committed.
- The login process compares passwords using SHA-256 hashes.
9. Technologies and Requirements
  Technology                          Purpose
  Java 23                         Main programming language specified
by the Maven project.

  Java Swing                      Builds the desktop interface and
application windows.

  MySQL 8.x                       Stores user, product, category, and
sales information.

  JDBC                            Connects the Java application to
MySQL.

  Maven                           Manages the project build and
dependencies.
MySQL Connector/J 9.1.0         JDBC driver used to connect to
MySQL.
Requirements
Before running the project, install:
- JDK 23
- MySQL Server 8.x
- MySQL Workbench (recommended for importing the database script)
- Apache NetBeans or another IDE that supports Maven projects
- Git (optional, for cloning the repository)
10. Installation and Execution
Step 1 -- Get the Source Code
Clone the repository:
git clone https://github.com/tiktoksilog/isufst-POS.git
You can also open the GitHub repository and choose Code → Download
ZIP, then extract the downloaded file.
Step 2 -- Create the Database
1. Start MySQL Server.
2. Open MySQL Workbench and connect to your local MySQL instance.
3. Open the project's database/isufst_pos_db.sql script.
4. Execute the script using the lightning bolt button.
5. Wait for the script to finish creating and populating the
   isufst_pos_db database.
Important: The SQL script drops and recreates the isufst_pos_db
database. Running it again will erase the existing data in that
database. Back up any data you need before executing the script.
Step 3 -- Configure the Database Connection
Open:
config/DBConnection.java
Check the database connection settings and update the MySQL username and
password to match your local setup.

The project's default settings are:

  Setting    Default
  Host       localhost
Port       3306
Database   isufst_pos_db
Username   root
Password   Blank
Step 4 -- Open the Maven Project
1. Open Apache NetBeans.
2. Select File → Open Project.
3. Choose the project folder containing pom.xml.
4. Allow Maven to load the required dependencies.
Step 5 -- Run the Application
Run the main class:
com.isufst.pos.Main
In NetBeans, you can also right-click the project and select Run.
Build Using Maven
From the project folder, run:
mvn clean package
11. Sample Login Accounts
The database script includes sample accounts for testing:

  Role            Username    Password
  Administrator   admin     admin123
Cashier         cashier   cashier123

These are sample credentials. Change them and use secure passwords
before deploying the application in an actual environment.
12. Project Information
- Project Name: ISUFST POS
- Project Type: Canteen Point of Sale System
- Application: Java desktop application
- Database: MySQL
- Build Tool: Maven
- Main Class: com.isufst.pos.Main
13. Screenshots
<img width="1917" height="1198" alt="Screenshot 2026-11-26 101801" src="https://github.com/user-attachments/assets/11612f79-b1d1-44d4-9862-da458c5b9aab" />
<img width="1917" height="1198" alt="Screenshot 2026-11-26 101741" src="https://github.com/user-attachments/assets/bd173a7c-2270-4970-a74a-dad25bd3f69a" />
<img width="1917" height="1198" alt="Screenshot 2026-11-26 101732" src="https://github.com/user-attachments/assets/08b8e148-36b4-4542-aaa9-2b199f42f41d" />


ISUFST POS
A simple way to manage canteen sales, products, inventory, and daily
reports.
