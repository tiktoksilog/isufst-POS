package com.isufst.pos.controller;

import com.isufst.pos.dao.CategoryDAO;
import com.isufst.pos.dao.ProductDAO;
import com.isufst.pos.dao.SaleDAO;
import com.isufst.pos.model.Cart;
import com.isufst.pos.model.Category;
import com.isufst.pos.model.Product;
import com.isufst.pos.model.Sale;
import com.isufst.pos.model.SaleItem;
import com.isufst.pos.model.User;
import com.isufst.pos.util.Formatter;
import com.isufst.pos.view.LoginFrame;
import com.isufst.pos.view.POSFrame;
import com.isufst.pos.view.ProductFrame;
import com.isufst.pos.view.SalesReportFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Handles the main cashier screen: browsing products, the cart and checkout.
 */
public class POSController {

    private static final int RECEIPT_WIDTH = 40;

    private final POSFrame view;
    private final User user;
    private final Cart cart = new Cart();

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    public POSController(POSFrame view, User user) {
        this.view = view;
        this.user = user;

        view.setCashierName(user.getFullName() + " (" + user.getRole() + ")");
        view.setAdminAccess(user.isAdmin());
        loadCategories();
        filterProducts();
        refreshCart();
    }

    // ---------------- products ----------------

    private void loadCategories() {
        try {
            view.setCategories(categoryDAO.findAll());
        } catch (SQLException e) {
            view.showError("Failed to load categories:\n" + e.getMessage());
        }
    }

    public void filterProducts() {
        Category category = view.getSelectedCategory();
        int categoryId = category == null ? 0 : category.getCategoryId();
        try {
            view.displayProducts(productDAO.findAvailable(categoryId, view.getSearchText()), this::addToCart);
        } catch (SQLException e) {
            view.showError("Failed to load products:\n" + e.getMessage());
        }
    }

    // ---------------- cart ----------------

    public void addToCart(Product product) {
        int alreadyInCart = cart.getQuantityOf(product.getProductId());
        if (alreadyInCart + 1 > product.getStock()) {
            view.showError("Only " + product.getStock() + " " + product.getName() + " left in stock.");
            return;
        }
        cart.add(product, 1);
        refreshCart();
    }

    public void editQuantity() {
        int index = view.getSelectedCartIndex();
        if (index < 0) {
            view.showError("Select an item in the order first.");
            return;
        }
        SaleItem item = cart.get(index);
        Integer qty = view.askQuantity(item.getProductName(), item.getQuantity());
        if (qty == null) {
            return; // cancelled
        }
        if (qty < 0) {
            view.showError("Please enter a valid whole number.");
            return;
        }
        try {
            Product product = productDAO.findById(item.getProductId());
            if (product != null && qty > product.getStock()) {
                view.showError("Only " + product.getStock() + " " + product.getName() + " left in stock.");
                return;
            }
        } catch (SQLException e) {
            view.showError("Failed to check stock:\n" + e.getMessage());
            return;
        }
        cart.setQuantity(index, qty);
        refreshCart();
        view.selectCartIndex(index);
    }

    public void removeItem() {
        int index = view.getSelectedCartIndex();
        if (index < 0) {
            view.showError("Select an item in the order first.");
            return;
        }
        cart.remove(index);
        refreshCart();
    }

    public void clearOrder() {
        if (cart.isEmpty()) {
            return;
        }
        if (view.confirm("Clear the current order?")) {
            cart.clear();
            view.clearCash();
            refreshCart();
        }
    }

    private void refreshCart() {
        view.showCart(cart.getItems(), cart.getTotal());
        updateChange();
    }

    // ---------------- payment ----------------

    /** Live change computation while typing the cash amount. */
    public void updateChange() {
        String text = view.getCashText();
        if (text.isEmpty()) {
            view.setChange(Formatter.peso(BigDecimal.ZERO), true);
            return;
        }
        BigDecimal cash = parseAmount(text);
        if (cash == null) {
            view.setChange("Invalid amount", false);
        } else if (cash.compareTo(cart.getTotal()) < 0) {
            view.setChange("Insufficient", false);
        } else {
            view.setChange(Formatter.peso(cash.subtract(cart.getTotal())), true);
        }
    }

    public void checkout() {
        if (cart.isEmpty()) {
            view.showError("The order is empty. Click a product to add it.");
            return;
        }
        BigDecimal total = cart.getTotal();
        BigDecimal cash = parseAmount(view.getCashText());
        if (cash == null) {
            view.showError("Please enter the cash amount tendered.");
            return;
        }
        if (cash.compareTo(total) < 0) {
            view.showError("Insufficient cash. Total is " + Formatter.peso(total) + ".");
            return;
        }

        Sale sale = new Sale();
        sale.setUserId(user.getUserId());
        sale.setCashierName(user.getFullName());
        sale.setSaleDate(LocalDateTime.now());
        sale.setTotalAmount(total);
        sale.setAmountPaid(cash);
        sale.setChangeAmount(cash.subtract(total));
        sale.setItems(new ArrayList<>(cart.getItems()));

        try {
            saleDAO.save(sale);
        } catch (SQLException e) {
            view.showError("Transaction failed. Nothing was saved.\n\n" + e.getMessage());
            filterProducts(); // refresh stock numbers
            return;
        }

        view.showReceipt(buildReceipt(sale));
        cart.clear();
        view.clearCash();
        refreshCart();
        filterProducts();
    }

    private static BigDecimal parseAmount(String text) {
        try {
            BigDecimal value = new BigDecimal(text.replace(",", "").trim());
            return value.signum() < 0 ? null : value.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String buildReceipt(Sale sale) {
        String line = "-".repeat(RECEIPT_WIDTH) + "\n";
        StringBuilder sb = new StringBuilder();
        sb.append(center("ISUFST CANTEEN")).append("\n");
        sb.append(center("ISUFST POS")).append("\n");
        sb.append(center("OFFICIAL RECEIPT")).append("\n");
        sb.append(line);
        sb.append("Receipt No: ").append(String.format("%06d", sale.getSaleId())).append("\n");
        sb.append("Date     : ").append(Formatter.dateTime(sale.getSaleDate())).append("\n");
        sb.append("Cashier  : ").append(sale.getCashierName()).append("\n");
        sb.append(line);
        sb.append(String.format("%-22s%5s%13s%n", "Item", "Qty", "Amount"));
        for (SaleItem item : sale.getItems()) {
            String name = item.getProductName();
            if (name.length() > 21) {
                name = name.substring(0, 20) + ".";
            }
            sb.append(String.format("%-22s%5d%13s%n", name, item.getQuantity(), Formatter.money(item.getSubtotal())));
            sb.append(String.format("   @ %s%n", Formatter.money(item.getUnitPrice())));
        }
        sb.append(line);
        sb.append(String.format("%-20s%20s%n", "TOTAL", Formatter.peso(sale.getTotalAmount())));
        sb.append(String.format("%-20s%20s%n", "CASH", Formatter.peso(sale.getAmountPaid())));
        sb.append(String.format("%-20s%20s%n", "CHANGE", Formatter.peso(sale.getChangeAmount())));
        sb.append(line);
        sb.append(center("Salamat Po!")).append("\n");
        return sb.toString();
    }

    private static String center(String text) {
        int pad = Math.max(0, (RECEIPT_WIDTH - text.length()) / 2);
        return " ".repeat(pad) + text;
    }

    // ---------------- navigation ----------------

    public void openProductManagement() {
        if (!user.isAdmin()) {
            view.showError("Only administrators can manage products.");
            return;
        }
        ProductFrame frame = new ProductFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                filterProducts(); // show updated prices/stock
            }
        });
        frame.setVisible(true);
    }

    public void openSalesReport() {
        new SalesReportFrame().setVisible(true);
    }

    public void logout() {
        if (!cart.isEmpty() && !view.confirm("There is an unfinished order. Log out anyway?")) {
            return;
        }
        if (cart.isEmpty() && !view.confirm("Do you want to log out?")) {
            return;
        }
        view.dispose();
        new LoginFrame().setVisible(true);
    }
}
