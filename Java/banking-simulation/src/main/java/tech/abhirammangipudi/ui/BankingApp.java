package tech.abhirammangipudi.ui;

import javax.swing.*;

import tech.abhirammangipudi.services.CurrentAccountService;
import tech.abhirammangipudi.services.SavingsAccountService;
import tech.abhirammangipudi.services.TransferService;
import tech.abhirammangipudi.services.UserService;
import tech.abhirammangipudi.ui.common.LandingPage;
import tech.abhirammangipudi.ui.user.DashboardPage;
import tech.abhirammangipudi.ui.user.LoginPage;
import tech.abhirammangipudi.ui.user.SignupPage;

import java.awt.*;

public class BankingApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel container;

    public BankingApp(UserService userService, SavingsAccountService savingsService, CurrentAccountService currentAccountService, TransferService transferService) {
        frame = new JFrame("Demo Bank");
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        LandingPage landingPage = new LandingPage(this);
        LoginPage loginPage = new LoginPage(this, userService);
        SignupPage signupPage = new SignupPage(this, userService);
        DashboardPage dashboardPage = new DashboardPage(this, savingsService, currentAccountService, transferService);
        container.add(landingPage, "landing");
        container.add(loginPage, "login");
        container.add(signupPage, "signup");
        container.add(dashboardPage, "dashboard");

        frame.add(container);
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        showPage("landing");
    }

    public void showPage(String name) {
        cardLayout.show(container, name);
    }
}
