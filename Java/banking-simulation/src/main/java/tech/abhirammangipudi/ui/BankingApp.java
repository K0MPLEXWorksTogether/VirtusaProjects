package tech.abhirammangipudi.ui;

import javax.swing.*;
import java.awt.*;

import tech.abhirammangipudi.repositories.TransactionRepository;
import tech.abhirammangipudi.services.*;
import tech.abhirammangipudi.ui.bank.*;
import tech.abhirammangipudi.ui.common.*;
import tech.abhirammangipudi.ui.user.*;

public class BankingApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel container;

    public BankingApp(UserService userService, SavingsAccountService savingsService, CurrentAccountService currentAccountService, TransferService transferService, BankService bankService, TransactionRepository transactionRepository) {
        frame = new JFrame("Demo Bank");
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        TransactionsPage transactionsPage = new TransactionsPage(this, transactionRepository);
        LandingPage landingPage = new LandingPage(this);
        BankLogin bankLogin = new BankLogin(this, bankService);
        BankDashboard bankDashboard = new BankDashboard(this, bankService, transactionsPage);
        LoginPage loginPage = new LoginPage(this, userService);
        SignupPage signupPage = new SignupPage(this, userService);
        DashboardPage dashboardPage = new DashboardPage(this, savingsService, currentAccountService, transferService,
                transactionsPage);
                
        container.add(bankLogin, "bankLogin");
        container.add(bankDashboard, "bankDashboard");
        container.add(transactionsPage, "transactionsPage");
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
