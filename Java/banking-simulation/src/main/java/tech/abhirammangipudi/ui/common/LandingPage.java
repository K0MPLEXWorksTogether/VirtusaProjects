package tech.abhirammangipudi.ui.common;

import javax.swing.*;
import java.awt.*;

import tech.abhirammangipudi.ui.BankingApp;

public class LandingPage extends JPanel {
    public LandingPage(BankingApp app) {
        setLayout(new GridLayout(3, 1));

        JButton userLogin = new JButton("Login as User");
        JButton bankLogin = new JButton("Login as Bank");

        userLogin.addActionListener(e -> app.showPage("login"));
        bankLogin.addActionListener(e -> app.showPage("bankLogin"));

        add(new JLabel("Welcome to Bank System", SwingConstants.CENTER));
        add(userLogin);
        add(bankLogin);
    }
}
