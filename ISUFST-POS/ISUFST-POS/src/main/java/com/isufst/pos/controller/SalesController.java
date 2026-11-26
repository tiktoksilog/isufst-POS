package com.isufst.pos.controller;

import com.isufst.pos.dao.SaleDAO;
import com.isufst.pos.model.Sale;
import com.isufst.pos.view.SalesReportFrame;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the daily sales report.
 */
public class SalesController {

    private final SalesReportFrame view;
    private final SaleDAO saleDAO = new SaleDAO();
    private List<Sale> sales = new ArrayList<>();

    public SalesController(SalesReportFrame view) {
        this.view = view;
        loadToday();
    }

    public void loadToday() {
        view.setDateText(LocalDate.now().toString());
        loadSales();
    }

    public void loadSales() {
        LocalDate date;
        try {
            date = LocalDate.parse(view.getDateText());
        } catch (DateTimeParseException e) {
            view.showError("Please enter the date as yyyy-MM-dd (e.g. 2026-11-16).");
            return;
        }
        try {
            sales = saleDAO.findByDate(date);
            view.showSales(sales);
            view.showItems(new ArrayList<>());

            BigDecimal total = BigDecimal.ZERO;
            for (Sale s : sales) {
                total = total.add(s.getTotalAmount());
            }
            view.setSummary(sales.size(), total);
        } catch (SQLException e) {
            view.showError("Failed to load sales:\n" + e.getMessage());
        }
    }

    public void loadItems() {
        int index = view.getSelectedSaleIndex();
        if (index < 0 || index >= sales.size()) {
            return;
        }
        try {
            view.showItems(saleDAO.findItems(sales.get(index).getSaleId()));
        } catch (SQLException e) {
            view.showError("Failed to load sale items:\n" + e.getMessage());
        }
    }
}
