package com.isufst.pos;

import com.isufst.pos.config.DBConnection;
import com.isufst.pos.view.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * ISUFST POS - Canteen Point of Sale System.
 *
 * Packages (MVC + DAO):
 *   model      - plain data classes (Product, Category, Sale, SaleItem, User, Cart)
 *   view       - JFrame Forms (LoginFrame, POSFrame, ProductFrame, SalesReportFrame)
 *   controller - logic that connects the views to the DAOs
 *   dao        - DAO classes (all SQL / database access)
 *   config     - DBConnection (JDBC settings)
 *
 */
public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException e) {
            // fall back to the default look and feel
        }

        java.awt.EventQueue.invokeLater(() -> {
            if (!DBConnection.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Cannot connect to the MySQL database 'isufst_pos_db'.\n\n"
                        + "1. Make sure MySQL Server is running.\n"
                        + "2. Run database/isufst_pos_db.sql in MySQL Workbench.\n"
                        + "3. Check the username/password in config/DBConnection.java.",
                        "ISUFST POS - Database Error", JOptionPane.WARNING_MESSAGE);
            }
            new LoginFrame().setVisible(true);
        });
    }
}
