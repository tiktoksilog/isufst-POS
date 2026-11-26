package com.isufst.pos.controller;

import com.isufst.pos.dao.UserDAO;
import com.isufst.pos.model.User;
import com.isufst.pos.view.LoginFrame;
import com.isufst.pos.view.POSFrame;
import java.sql.SQLException;

/**
 * Handles the login screen.
 */
public class LoginController {

    private final LoginFrame view;
    private final UserDAO userDAO = new UserDAO();

    public LoginController(LoginFrame view) {
        this.view = view;
    }

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Please enter your username and password.");
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                view.showError("Invalid username or password.");
                view.clearPassword();
                return;
            }
            view.dispose();
            new POSFrame(user).setVisible(true);
        } catch (SQLException e) {
            view.showError("Cannot connect to the database.\n\n" + e.getMessage()
                    + "\n\nCheck that MySQL is running and the settings in DBConnection.java are correct.");
        }
    }
}
