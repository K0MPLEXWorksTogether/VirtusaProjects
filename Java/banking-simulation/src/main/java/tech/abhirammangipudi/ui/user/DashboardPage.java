package tech.abhirammangipudi.ui.user;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.table.DefaultTableModel;

import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.CurrentAccount;
import tech.abhirammangipudi.models.SavingsAccount;
import tech.abhirammangipudi.services.CurrentAccountService;
import tech.abhirammangipudi.services.SavingsAccountService;
import tech.abhirammangipudi.services.TransferService;
import tech.abhirammangipudi.services.UserService;
import tech.abhirammangipudi.ui.BankingApp;
import tech.abhirammangipudi.ui.common.TransactionsPage;

public class DashboardPage extends JPanel {
    private JTable table;
    private List<Account> accounts = new ArrayList<>();

    private final SavingsAccountService savingsService;
    private final CurrentAccountService currentService;
    private final TransferService transferService;
    private final UserService userService;
    private String currentType = "SAVINGS";

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshAfterAction() {
        if (currentType.equals("SAVINGS"))
            loadSavings();
        else
            loadCurrent();
    }

    private Account getSelectedAccount() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an account first");
            return null;
        }
        return accounts.get(row);
    }

    private void loadSavings() {
        try {
            currentType = "SAVINGS";
            accounts = new ArrayList<>(savingsService.getSavingsAccounts(1, 10));
            refreshTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadCurrent() {
        try {
            currentType = "CURRENT";
            accounts = new ArrayList<>(currentService.getCurrentAccounts(1, 10));
            refreshTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void refreshTable() {
        Object[][] data = new Object[accounts.size()][2];

        for (int i = 0; i < accounts.size(); i++) {
            data[i][0] = accounts.get(i).getAccountNumber();
            data[i][1] = accounts.get(i).checkBalance();
        }

        String[] columnNames = { "Account Number", "Balanace" };
        table.setModel(new DefaultTableModel(data, columnNames));
    }

    private void handleCreateSavingsAccount() {
        String initialAmountString = JOptionPane.showInputDialog("Enter Initial Deposit: ");
        Double initialAmount = Double.parseDouble(initialAmountString);
        if (initialAmount < 300.0) {
            JOptionPane.showMessageDialog(this, "The minimum deposit needs are $300");
            return;
        }

        try {
            SavingsAccount newSavingsAccount = new SavingsAccount(userService.getCurrentUser(), initialAmount, 300.0,
                    5.0);
            savingsService.save(newSavingsAccount);
        } catch (AuthenticationException ae) {
            showError(ae);
        }

        refreshAfterAction();
    }

    private void handleCreateCurrentAccount() {
        String initialAmountString = JOptionPane.showInputDialog("Enter Initial Deposit: ");
        Double initialAmount = Double.parseDouble(initialAmountString);
        if (initialAmount < 0) {
            JOptionPane.showMessageDialog(this, "The amount entered is less than 0");
            return;
        }
        
        try {
            CurrentAccount newSavingsAccount = new CurrentAccount(userService.getCurrentUser(), initialAmount, 300.0,
                    300.0);
            currentService.save(newSavingsAccount);
        } catch (AuthenticationException ae) {
            showError(ae);
        }

        refreshAfterAction();
    }

    private void handleDeposit() {
        Account acc = getSelectedAccount();
        if (acc == null)
            return;

        String amtStr = JOptionPane.showInputDialog("Enter amount:");
        if (amtStr == null)
            return;

        try {
            double amt = Double.parseDouble(amtStr);

            if (currentType.equals("SAVINGS")) {
                savingsService.deposit(acc.getAccountNumber(), amt);
            } else {
                currentService.deposit(acc.getAccountNumber(), amt);
            }

            refreshAfterAction();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void handleWithdraw() {
        Account acc = getSelectedAccount();
        if (acc == null)
            return;

        String amtStr = JOptionPane.showInputDialog("Enter amount:");
        if (amtStr == null)
            return;

        try {
            double amt = Double.parseDouble(amtStr);

            if (currentType.equals("SAVINGS")) {
                savingsService.withdraw(acc.getAccountNumber(), amt);
            } else {
                currentService.withdraw(acc.getAccountNumber(), amt);
            }

            refreshAfterAction();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void handleCheckBalance() {
        Account acc = getSelectedAccount();
        if (acc == null)
            return;

        try {
            double balance;

            if (currentType.equals("SAVINGS")) {
                balance = savingsService.checkBalance(acc.getAccountNumber());
            } else {
                balance = currentService.checkBalance(acc.getAccountNumber());
            }

            JOptionPane.showMessageDialog(this, "Balance: " + balance);

        } catch (Exception e) {
            showError(e);
        }
    }

    private void handleTransfer() {
        Account source = getSelectedAccount();
        if (source == null)
            return;

        String destIdStr = JOptionPane.showInputDialog("Enter Destination Account ID:");
        String amtStr = JOptionPane.showInputDialog("Enter Amount:");

        if (destIdStr == null || amtStr == null)
            return;

        try {
            UUID destId = UUID.fromString(destIdStr);
            double amt = Double.parseDouble(amtStr);

            Account destination;
            if (savingsService.findById(destId) instanceof SavingsAccount) {
                destination = savingsService.findById(destId);
            } else {
                destination = currentService.findById(destId);
            }

            transferService.transfer(source, destination, amt);

            JOptionPane.showMessageDialog(this, "Transfer successful!");
            refreshAfterAction();

        } catch (Exception e) {
            showError(e);
        }
    }

    public DashboardPage(BankingApp app, SavingsAccountService savingsService, CurrentAccountService currentService,
            TransferService transferService, TransactionsPage transactionsPage, UserService userService) {
        setName("userDashboard");
        this.savingsService = savingsService;
        this.currentService = currentService;
        this.transferService = transferService;
        this.userService = userService;
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton currentBtn = new JButton("Current Accounts");
        JButton savingsBtn = new JButton("Savings Accounts");

        top.add(currentBtn);
        top.add(savingsBtn);

        savingsBtn.addActionListener(e -> loadSavings());
        currentBtn.addActionListener(e -> loadCurrent());

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");
        JButton transfer = new JButton("Transfer");
        JButton checkBalance = new JButton("Check Balance");
        JButton showTransactions = new JButton("Show Transactions");
        JButton back = new JButton("Back");

        actions.add(deposit);
        actions.add(withdraw);
        actions.add(transfer);
        actions.add(checkBalance);
        actions.add(showTransactions);
        actions.add(back);

        deposit.addActionListener(e -> handleDeposit());
        withdraw.addActionListener(e -> handleWithdraw());
        transfer.addActionListener(e -> handleTransfer());
        checkBalance.addActionListener(e -> handleCheckBalance());
        showTransactions.addActionListener(e -> {
            transactionsPage.setAccountId(getSelectedAccount().getAccountNumber());
            app.showPage("transactionsPage");
        });
        back.addActionListener(e -> app.showPage("login"));

        JPanel left = new JPanel();
        JButton createSavingsButton = new JButton("Create Savings Account");
        JButton createCurrentButton = new JButton("Create Current Account");
        left.add(createCurrentButton);
        left.add(createSavingsButton);
        left.setPreferredSize(new Dimension(200, 0));
        createCurrentButton.addActionListener(e -> handleCreateCurrentAccount());
        createSavingsButton.addActionListener(e -> handleCreateSavingsAccount());

        add(left, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        add(actions, BorderLayout.SOUTH);

        loadSavings();
    }
}
