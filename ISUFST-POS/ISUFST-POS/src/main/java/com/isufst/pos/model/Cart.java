package com.isufst.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The current order being rung up by the cashier.
 */
public class Cart {

    private final List<SaleItem> items = new ArrayList<>();

    /** Adds a product, merging with an existing line for the same product. */
    public void add(Product product, int quantity) {
        for (SaleItem item : items) {
            if (item.getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new SaleItem(product.getProductId(), product.getName(), quantity, product.getPrice()));
    }

    /** Quantity of a product already in the cart (0 if none). */
    public int getQuantityOf(int productId) {
        for (SaleItem item : items) {
            if (item.getProductId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public void setQuantity(int index, int quantity) {
        if (quantity <= 0) {
            items.remove(index);
        } else {
            items.get(index).setQuantity(quantity);
        }
    }

    public SaleItem get(int index) {
        return items.get(index);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}
