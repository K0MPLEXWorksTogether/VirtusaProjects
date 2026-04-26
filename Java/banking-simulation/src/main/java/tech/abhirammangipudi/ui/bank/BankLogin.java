package tech.abhirammangipudi.ui.bank;

import javax.swing.*;
import java.awt.*;

import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.services.BankService;
import tech.abhirammangipudi.ui.BankingApp;

public class BankLogin extends JPanel {
    public BankLogin(BankingApp app, BankService bankService) {
        setLayout(new GridLayout(5, 2));
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JButton login = new JButton("Login");
        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showPage("landing"));

        login.addActionListener(e -> {
            String bankUName = username.getText();
            String bankPwd = new String(password.getPassword());

            try {
                bankService.login(bankUName, bankPwd);
                JOptionPane.showMessageDialog(this, "Login successful!");
                app.showPage("bankDashboard");
            } catch (AuthenticationException ae) {
                JOptionPane.showConfirmDialog(this, ae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(new JLabel("Username:"));
        add(username);
        add(new JLabel("Password:"));
        add(password);
        add(login);
        add(back);
    }
}
