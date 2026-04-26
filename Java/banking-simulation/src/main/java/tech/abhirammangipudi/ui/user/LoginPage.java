package tech.abhirammangipudi.ui.user;

import javax.swing.*;
import java.awt.*;

import tech.abhirammangipudi.ui.BankingApp;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.services.UserService;;

public class LoginPage extends JPanel {
    public LoginPage(BankingApp app, UserService userService) {
        setLayout(new GridLayout(5, 2));
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JButton login = new JButton("Login");
        JButton signup = new JButton("Sign Up");
        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showPage("landing"));

        login.addActionListener(e -> {
            String uname = username.getText();
            String pwd = new String(password.getPassword());

            try {
                userService.login(uname, pwd);
                JOptionPane.showMessageDialog(this, "Login successful!");
                app.showPage("dashboard");
            } catch (AuthenticationException ae) {
                JOptionPane.showConfirmDialog(this, ae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signup.addActionListener(e -> app.showPage("signup"));
        add(new JLabel("Username:"));
        add(username);
        add(new JLabel("Password:"));
        add(password);
        add(login);
        add(signup);
        add(back);
    }
}
