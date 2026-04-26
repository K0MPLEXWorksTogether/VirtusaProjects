package tech.abhirammangipudi.ui.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.repositories.TransactionRepository;
import tech.abhirammangipudi.ui.BankingApp;

public class TransactionsPage extends JPanel {
    private JTable table;
    private List<Transaction> transactions;
    private TransactionRepository transactionRepository;
    private UUID accountId;

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
        loadTransactions();
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void loadTransactions() {
        try {
            transactions = new ArrayList<>(transactionRepository.findByAccountId(accountId));
            refreshTable();
        } catch (ResourceNotFoundException rnfe) {
            showError(rnfe);
        }
    }

    private void refreshTable() {
        Object[][] data = new Object[transactions.size()][8];
        for (int i = 0; i < transactions.size(); i++) {
            data[i][0] = transactions.get(i).getTransactionId();
            data[i][1] = transactions.get(i).getType();
            data[i][2] = transactions.get(i).getAmount();
            data[i][3] = transactions.get(i).getTimestamp();
            data[i][4] = transactions.get(i).getBalanceAfter();
            data[i][5] = transactions.get(i).getTransactionAgent();
            data[i][6] = transactions.get(i).getUserId();
            data[i][7] = transactions.get(i).getAccountId();
        }

        String[] columnNames = { "Transaction Id", "Transaction Type", "Amount", "TimeStamp", "Balance After",
                "Transaction Agent", "User Id", "Account Id" };
        table.setModel(new DefaultTableModel(data, columnNames));
    }

    public TransactionsPage(BankingApp app, TransactionRepository transactionRepository) {
        this.table = new JTable();
        this.transactionRepository = transactionRepository;
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            Container parent = back.getParent();
            if (parent.getParent().getName() == "bankDashboard") {
                app.showPage("bankDashboard");
            } else {
                app.showPage("dashboard");
            }
        });
        add(back);
    }
}
