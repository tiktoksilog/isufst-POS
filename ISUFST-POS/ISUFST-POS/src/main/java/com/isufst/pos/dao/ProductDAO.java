package com.isufst.pos.dao;

import com.isufst.pos.config.DBConnection;
import com.isufst.pos.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String BASE_SELECT
            = "SELECT p.product_id, p.category_id, c.name AS category_name, p.name, "
            + "p.price, p.stock, p.is_active "
            + "FROM products p JOIN categories c ON p.category_id = c.category_id ";

    public List<Product> search(String keyword) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.name LIKE ? OR c.name LIKE ? ORDER BY c.category_id, p.name";
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            return mapList(ps);
        }
    }

    public List<Product> findAvailable(int categoryId, String keyword) throws SQLException {
        String sql = BASE_SELECT
                + "WHERE p.is_active = 1 AND (? = 0 OR p.category_id = ?) AND p.name LIKE ? "
                + "ORDER BY c.category_id, p.name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, categoryId);
            ps.setString(3, "%" + keyword + "%");
            return mapList(ps);
        }
    }

    public Product findById(int productId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            List<Product> list = mapList(ps);
            return list.isEmpty() ? null : list.get(0);
        }
    }

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO products (category_id, name, price, stock, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setBoolean(5, product.isActive());
            ps.executeUpdate();
        }
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET category_id = ?, name = ?, price = ?, stock = ?, is_active = ? "
                + "WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setBoolean(5, product.isActive());
            ps.setInt(6, product.getProductId());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes the product. If it already appears in past sales it cannot be deleted,
     * so it is marked inactive instead.
     *
     * @return true if deleted, false if it was deactivated instead
     */
    public boolean delete(int productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
                ps.setInt(1, productId);
                ps.executeUpdate();
                return true;
            } catch (SQLIntegrityConstraintViolationException e) {
                // Product is referenced by past sales: keep the record, just hide it.
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET is_active = 0 WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
                return false;
            }
        }
    }

    private List<Product> mapList(PreparedStatement ps) throws SQLException {
        List<Product> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock"),
                        rs.getBoolean("is_active")));
            }
        }
        return list;
    }
}
