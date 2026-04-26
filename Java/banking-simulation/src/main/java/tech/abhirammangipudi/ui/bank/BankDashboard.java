package tech.abhirammangipudi.ui.bank;

import java.util.List;
import java.util.ArrayList;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.services.BankService;
import tech.abhirammangipudi.ui.BankingApp;
import tech.abhirammangipudi.ui.common.TransactionsPage;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;

public class BankDashboard extends JPanel {
    private JTable table;
    private List<Account> accounts = new ArrayList<>();
    private final BankService bankService;

    private Account getSelectedAccount() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an account first");
            return null;
        }
        return accounts.get(row);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void loadAccounts() {
        try {
            accounts = new ArrayList<>(bankService.getAllAccounts());
            refreshTable();
        } catch (AuthenticationException ae) {
            showError(ae);
        }
    }
    
    private void refreshTable() {
        Object[][] data = new Object[accounts.size()][3];
        for (int i = 0; i < accounts.size(); i++) {
            data[i][0] = accounts.get(i).getAccountNumber();
            data[i][1] = accounts.get(i).checkBalance();
            
            if (accounts.get(i).getMinimumBalance() == 0) {
                data[i][2] = "CURRENT";
            } else {
                data[i][2] = "SAVINGS";
            }
        }

        String[] columnNames = { "Account Number", "Balance", "Account Type" };
        table.setModel(new DefaultTableModel(data, columnNames));
    }

    public BankDashboard(BankingApp app, BankService bankService, TransactionsPage transactionsPage) {
        setName("bankDashboard");
        this.bankService = bankService;
        setLayout(new BorderLayout());
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton intrest = new JButton("Pay Intrest");
        JButton assets = new JButton("Get Bank Assets");
        JButton transactions = new JButton("Get Transactions");
        JButton back = new JButton("Back");
        actions.add(intrest);
        actions.add(assets);
        actions.add(transactions);

        back.addActionListener(e -> app.showPage("bankLogin"));
        intrest.addActionListener(e -> {
            try {
                this.bankService.processIntrest();
                loadAccounts();
                refreshTable();
            } catch (AuthenticationException | InvalidAmountException | ResourceNotFoundException err) {
                showError(err);
            }
        });

        assets.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, String.format("Bank Assets: %f", this.bankService.getBankAssets()));
                loadAccounts();
                refreshTable();
            } catch (AuthenticationException ae) {
                showError(ae);
            }
        });

        transactions.addActionListener(e -> {
            transactionsPage.setAccountId(getSelectedAccount().getAccountNumber());
            app.showPage("transactionsPage");
        });

        add(actions, BorderLayout.SOUTH);
        loadAccounts();
        refreshTable();
    }
}
