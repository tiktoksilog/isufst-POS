package com.isufst.pos.view;

import com.isufst.pos.controller.POSController;
import com.isufst.pos.model.Category;
import com.isufst.pos.model.Product;
import com.isufst.pos.model.SaleItem;
import com.isufst.pos.model.User;
import com.isufst.pos.util.Formatter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Main cashier screen (View). All logic is delegated to {@link POSController}.
 *
 */
public class POSFrame extends javax.swing.JFrame {

    private static final Color TILE_COLOR = new Color(232, 245, 233);
    private static final Color OK_COLOR = new Color(0, 102, 51);
    private static final Color ERROR_COLOR = new Color(198, 40, 40);

    private final DefaultTableModel cartModel;
    private POSController controller;   // set at the end of the constructor
    private boolean loadingCategories;

    public POSFrame(User user) {
        initComponents();

        cartModel = new DefaultTableModel(new Object[]{"Item", "Price", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart.setModel(cartModel);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(190);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(40);
        scrollProducts.getVerticalScrollBar().setUnitIncrement(16);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);

        controller = new POSController(this, user);
    }

    // ---------- methods used by the controller ----------

    public void setCashierName(String name) {
        lblCashier.setText("Cashier: " + name);
    }

    public void setAdminAccess(boolean admin) {
        btnProducts.setVisible(admin);
    }

    public void setCategories(List<Category> categories) {
        loadingCategories = true;
        cmbCategory.removeAllItems();
        cmbCategory.addItem(new Category(0, "All Categories"));
        for (Category c : categories) {
            cmbCategory.addItem(c);
        }
        loadingCategories = false;
    }

    public Category getSelectedCategory() {
        return (Category) cmbCategory.getSelectedItem();
    }

    public String getSearchText() {
        return txtSearch.getText().trim();
    }

    /** Builds one button per product; clicking a button calls onClick. */
    public void displayProducts(List<Product> products, Consumer<Product> onClick) {
        productGridPanel.removeAll();
        if (products.isEmpty()) {
            productGridPanel.add(new JLabel("No products found."));
        }
        for (Product p : products) {
            String html = "<html><center><b>" + p.getName() + "</b><br>"
                    + Formatter.peso(p.getPrice()) + "<br>"
                    + "<font size='2'>Stock: " + p.getStock() + "</font></center></html>";
            JButton tile = new JButton(html);
            tile.setPreferredSize(new Dimension(150, 90));
            tile.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tile.setBackground(TILE_COLOR);
            tile.setFocusPainted(false);
            tile.setEnabled(p.getStock() > 0);
            tile.setToolTipText(p.getCategoryName() + " - " + p.getName());
            tile.addActionListener(e -> onClick.accept(p));
            productGridPanel.add(tile);
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    public void showCart(List<SaleItem> items, BigDecimal total) {
        cartModel.setRowCount(0);
        for (SaleItem item : items) {
            cartModel.addRow(new Object[]{
                item.getProductName(),
                Formatter.money(item.getUnitPrice()),
                item.getQuantity(),
                Formatter.money(item.getSubtotal())
            });
        }
        lblTotal.setText(Formatter.peso(total));
    }

    public int getSelectedCartIndex() {
        return tblCart.getSelectedRow();
    }

    public void selectCartIndex(int index) {
        if (index >= 0 && index < tblCart.getRowCount()) {
            tblCart.setRowSelectionInterval(index, index);
        }
    }

    public String getCashText() {
        return txtCash.getText().trim();
    }

    public void clearCash() {
        txtCash.setText("");
    }

    public void setChange(String text, boolean ok) {
        lblChange.setText(text);
        lblChange.setForeground(ok ? OK_COLOR : ERROR_COLOR);
    }

    public Integer askQuantity(String productName, int current) {
        String input = JOptionPane.showInputDialog(this, "New quantity for " + productName + ":", current);
        if (input == null) {
            return null;
        }
        try {
            return Integer.valueOf(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void showReceipt(String receipt) {
        JTextArea area = new JTextArea(receipt);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Receipt", JOptionPane.PLAIN_MESSAGE);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "ISUFST POS", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "ISUFST POS", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "ISUFST POS",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        headerRightPanel = new javax.swing.JPanel();
        lblCashier = new javax.swing.JLabel();
        btnProducts = new javax.swing.JButton();
        btnSales = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        productsPanel = new javax.swing.JPanel();
        filterPanel = new javax.swing.JPanel();
        lblCategory = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        scrollProducts = new javax.swing.JScrollPane();
        productWrapPanel = new javax.swing.JPanel();
        productGridPanel = new javax.swing.JPanel();
        cartPanel = new javax.swing.JPanel();
        scrollCart = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        checkoutPanel = new javax.swing.JPanel();
        lblTotalText = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblCashText = new javax.swing.JLabel();
        txtCash = new javax.swing.JTextField();
        lblChangeText = new javax.swing.JLabel();
        lblChange = new javax.swing.JLabel();
        btnEditQty = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnCheckout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ISUFST POS");
        setMinimumSize(new java.awt.Dimension(1100, 650));
        getContentPane().setLayout(new java.awt.BorderLayout(8, 8));

        headerPanel.setBackground(new java.awt.Color(0, 102, 51));
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("ISUFST POS");
        headerPanel.add(lblTitle, java.awt.BorderLayout.WEST);

        headerRightPanel.setOpaque(false);
        headerRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        lblCashier.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCashier.setForeground(new java.awt.Color(255, 255, 255));
        lblCashier.setText("Cashier:");
        headerRightPanel.add(lblCashier);

        btnProducts.setText("Manage Products");
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });
        headerRightPanel.add(btnProducts);

        btnSales.setText("Sales Report");
        btnSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalesActionPerformed(evt);
            }
        });
        headerRightPanel.add(btnSales);

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        headerRightPanel.add(btnLogout);

        headerPanel.add(headerRightPanel, java.awt.BorderLayout.EAST);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        productsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Menu"));
        productsPanel.setLayout(new java.awt.BorderLayout());

        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        lblCategory.setText("Category:");
        filterPanel.add(lblCategory);

        cmbCategory.setPreferredSize(new java.awt.Dimension(180, 28));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        filterPanel.add(cmbCategory);

        lblSearch.setText("Search:");
        filterPanel.add(lblSearch);

        txtSearch.setColumns(18);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        filterPanel.add(txtSearch);

        productsPanel.add(filterPanel, java.awt.BorderLayout.NORTH);

        productWrapPanel.setLayout(new java.awt.BorderLayout());

        productGridPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        productGridPanel.setLayout(new java.awt.GridLayout(0, 4, 8, 8));
        productWrapPanel.add(productGridPanel, java.awt.BorderLayout.NORTH);

        scrollProducts.setViewportView(productWrapPanel);

        productsPanel.add(scrollProducts, java.awt.BorderLayout.CENTER);

        getContentPane().add(productsPanel, java.awt.BorderLayout.CENTER);

        cartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Order"));
        cartPanel.setPreferredSize(new java.awt.Dimension(440, 500));
        cartPanel.setLayout(new java.awt.BorderLayout());

        tblCart.setRowHeight(26);
        scrollCart.setViewportView(tblCart);

        cartPanel.add(scrollCart, java.awt.BorderLayout.CENTER);

        checkoutPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        checkoutPanel.setLayout(new java.awt.GridLayout(0, 2, 8, 8));

        lblTotalText.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotalText.setText("TOTAL:");
        checkoutPanel.add(lblTotalText);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(0, 102, 51));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("₱0.00");
        checkoutPanel.add(lblTotal);

        lblCashText.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblCashText.setText("Cash Tendered:");
        checkoutPanel.add(lblCashText);

        txtCash.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtCash.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCashActionPerformed(evt);
            }
        });
        txtCash.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCashKeyReleased(evt);
            }
        });
        checkoutPanel.add(txtCash);

        lblChangeText.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblChangeText.setText("Change:");
        checkoutPanel.add(lblChangeText);

        lblChange.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblChange.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChange.setText("₱0.00");
        checkoutPanel.add(lblChange);

        btnEditQty.setText("Edit Quantity");
        btnEditQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditQtyActionPerformed(evt);
            }
        });
        checkoutPanel.add(btnEditQty);

        btnRemove.setText("Remove Item");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        checkoutPanel.add(btnRemove);

        btnClear.setText("Clear Order");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        checkoutPanel.add(btnClear);

        btnCheckout.setBackground(new java.awt.Color(0, 102, 51));
        btnCheckout.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckout.setText("CHECKOUT");
        btnCheckout.setPreferredSize(new java.awt.Dimension(150, 45));
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });
        checkoutPanel.add(btnCheckout);

        cartPanel.add(checkoutPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(cartPanel, java.awt.BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        controller.openProductManagement();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesActionPerformed
        controller.openSalesReport();
    }//GEN-LAST:event_btnSalesActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        controller.logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        if (!loadingCategories && controller != null) {
            controller.filterProducts();
        }
    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        controller.filterProducts();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCashActionPerformed
        controller.checkout();
    }//GEN-LAST:event_txtCashActionPerformed

    private void txtCashKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCashKeyReleased
        controller.updateChange();
    }//GEN-LAST:event_txtCashKeyReleased

    private void btnEditQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditQtyActionPerformed
        controller.editQuantity();
    }//GEN-LAST:event_btnEditQtyActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        controller.removeItem();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        controller.clearOrder();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        controller.checkout();
    }//GEN-LAST:event_btnCheckoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEditQty;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSales;
    private javax.swing.JPanel cartPanel;
    private javax.swing.JPanel checkoutPanel;
    private javax.swing.JComboBox<com.isufst.pos.model.Category> cmbCategory;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel headerRightPanel;
    private javax.swing.JLabel lblCashText;
    private javax.swing.JLabel lblCashier;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblChangeText;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalText;
    private javax.swing.JPanel productGridPanel;
    private javax.swing.JPanel productWrapPanel;
    private javax.swing.JPanel productsPanel;
    private javax.swing.JScrollPane scrollCart;
    private javax.swing.JScrollPane scrollProducts;
    private javax.swing.JTable tblCart;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
