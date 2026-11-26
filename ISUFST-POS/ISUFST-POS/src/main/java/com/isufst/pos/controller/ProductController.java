package com.isufst.pos.controller;

import com.isufst.pos.dao.CategoryDAO;
import com.isufst.pos.dao.ProductDAO;
import com.isufst.pos.model.Category;
import com.isufst.pos.model.Product;
import com.isufst.pos.view.ProductFrame;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles adding, editing and deleting products.
 */
public class ProductController {

    private final ProductFrame view;
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private List<Product> products = new ArrayList<>();

    public ProductController(ProductFrame view) {
        this.view = view;
        try {
            view.setCategories(categoryDAO.findAll());
        } catch (SQLException e) {
            view.showError("Failed to load categories:\n" + e.getMessage());
        }
        loadProducts();
    }

    public void loadProducts() {
        try {
            products = productDAO.search(view.getSearchText());
            view.showProducts(products);
        } catch (SQLException e) {
            view.showError("Failed to load products:\n" + e.getMessage());
        }
    }

    public void selectProduct() {
        int row = view.getSelectedRow();
        if (row >= 0 && row < products.size()) {
            view.fillForm(products.get(row));
        }
    }

    public void addProduct() {
        Product p = readForm();
        if (p == null) {
            return;
        }
        try {
            productDAO.insert(p);
            view.showMessage("Product added.");
            view.clearForm();
            loadProducts();
        } catch (SQLException e) {
            view.showError("Failed to add product:\n" + e.getMessage());
        }
    }

    public void updateProduct() {
        if (view.getIdText().isEmpty()) {
            view.showError("Select a product from the table first.");
            return;
        }
        Product p = readForm();
        if (p == null) {
            return;
        }
        p.setProductId(Integer.parseInt(view.getIdText()));
        try {
            productDAO.update(p);
            view.showMessage("Product updated.");
            view.clearForm();
            loadProducts();
        } catch (SQLException e) {
            view.showError("Failed to update product:\n" + e.getMessage());
        }
    }

    public void deleteProduct() {
        if (view.getIdText().isEmpty()) {
            view.showError("Select a product from the table first.");
            return;
        }
        if (!view.confirm("Delete \"" + view.getProductNameText() + "\"?")) {
            return;
        }
        try {
            boolean deleted = productDAO.delete(Integer.parseInt(view.getIdText()));
            view.showMessage(deleted
                    ? "Product deleted."
                    : "This product already has sales records, so it was set to Inactive instead.");
            view.clearForm();
            loadProducts();
        } catch (SQLException e) {
            view.showError("Failed to delete product:\n" + e.getMessage());
        }
    }

    /** Validates the form and builds a Product, or returns null after showing an error. */
    private Product readForm() {
        String name = view.getProductNameText();
        Category category = view.getSelectedCategory();

        if (name.isEmpty()) {
            view.showError("Product name is required.");
            return null;
        }
        if (category == null) {
            view.showError("Please choose a category.");
            return null;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(view.getPriceText()).setScale(2, RoundingMode.HALF_UP);
            if (price.signum() < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            view.showError("Price must be a valid amount (e.g. 75 or 75.50).");
            return null;
        }

        int stock;
        try {
            stock = Integer.parseInt(view.getStockText());
            if (stock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            view.showError("Stock must be a whole number of 0 or more.");
            return null;
        }

        Product p = new Product();
        p.setName(name);
        p.setCategoryId(category.getCategoryId());
        p.setPrice(price);
        p.setStock(stock);
        p.setActive(view.isActiveChecked());
        return p;
    }
}
