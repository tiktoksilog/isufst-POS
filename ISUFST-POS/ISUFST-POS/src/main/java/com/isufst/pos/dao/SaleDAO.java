package com.isufst.pos.dao;

import com.isufst.pos.config.DBConnection;
import com.isufst.pos.model.Sale;
import com.isufst.pos.model.SaleItem;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public int save(Sale sale) throws SQLException {
        String insertSale = "INSERT INTO sales (user_id, sale_date, total_amount, amount_paid, change_amount) "
                + "VALUES (?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        String deductStock = "UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSale = conn.prepareStatement(insertSale, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement psItem = conn.prepareStatement(insertItem);
                    PreparedStatement psStock = conn.prepareStatement(deductStock)) {

                psSale.setInt(1, sale.getUserId());
                psSale.setTimestamp(2, Timestamp.valueOf(sale.getSaleDate()));
                psSale.setBigDecimal(3, sale.getTotalAmount());
                psSale.setBigDecimal(4, sale.getAmountPaid());
                psSale.setBigDecimal(5, sale.getChangeAmount());
                psSale.executeUpdate();

                int saleId;
                try (ResultSet keys = psSale.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to get the new sale ID.");
                    }
                    saleId = keys.getInt(1);
                }

                for (SaleItem item : sale.getItems()) {
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.setInt(3, item.getQuantity());
                    if (psStock.executeUpdate() == 0) {
                        throw new SQLException("Not enough stock for " + item.getProductName() + ".");
                    }

                    psItem.setInt(1, saleId);
                    psItem.setInt(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setBigDecimal(4, item.getUnitPrice());
                    psItem.setBigDecimal(5, item.getSubtotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();

                conn.commit();
                sale.setSaleId(saleId);
                return saleId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Sale> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT s.sale_id, s.user_id, u.full_name, s.sale_date, s.total_amount, "
                + "s.amount_paid, s.change_amount "
                + "FROM sales s JOIN users u ON s.user_id = u.user_id "
                + "WHERE DATE(s.sale_date) = ? ORDER BY s.sale_date DESC";
        List<Sale> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale sale = new Sale();
                    sale.setSaleId(rs.getInt("sale_id"));
                    sale.setUserId(rs.getInt("user_id"));
                    sale.setCashierName(rs.getString("full_name"));
                    sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                    sale.setTotalAmount(rs.getBigDecimal("total_amount"));
                    sale.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    sale.setChangeAmount(rs.getBigDecimal("change_amount"));
                    list.add(sale);
                }
            }
        }
        return list;
    }

    public List<SaleItem> findItems(int saleId) throws SQLException {
        String sql = "SELECT si.sale_item_id, si.sale_id, si.product_id, p.name, si.quantity, si.unit_price "
                + "FROM sale_items si JOIN products p ON si.product_id = p.product_id "
                + "WHERE si.sale_id = ? ORDER BY si.sale_item_id";
        List<SaleItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"));
                    item.setSaleItemId(rs.getInt("sale_item_id"));
                    item.setSaleId(rs.getInt("sale_id"));
                    list.add(item);
                }
            }
        }
        return list;
    }
}
