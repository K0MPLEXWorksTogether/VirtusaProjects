package tech.abhirammangipudi.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.interfaces.Deposit;
import tech.abhirammangipudi.models.Transaction.TransactionAgent;
import tech.abhirammangipudi.models.Transaction.TransactionType;
import tech.abhirammangipudi.interfaces.CheckBalance;

public abstract class Account implements Deposit, CheckBalance {
    private final UUID accountNumber;
    private final User accountHolder;
    private final LocalDateTime dateOpened;

    private double balance;
    private double minimumBalance;
    private List<Transaction> transactions;

    public Account(UUID accountNumber, User accountHolder, LocalDateTime dateOpened, double balance,
            double minimumBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.dateOpened = dateOpened;
        this.minimumBalance = minimumBalance;
        this.transactions = new ArrayList<>();
    }

    public Account(User accountHolder, double balance, LocalDateTime dateOpened,
            double minimumBalance) {
        this.accountNumber = UUID.randomUUID();
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.dateOpened = dateOpened;
        this.minimumBalance = minimumBalance;
        this.transactions = new ArrayList<>();
    }

    public Account(User accountHolder, double balance, double minimumBalance) {
        this.accountNumber = UUID.randomUUID();
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.minimumBalance = minimumBalance;
        this.dateOpened = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public UUID getAccountNumber() {
        return accountNumber;
    }

    public User getAccountHolder() {
        return accountHolder;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public boolean canWithdraw(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive", amount);
        }

        return (balance - amount) < 0;
    }

    protected double getBalance() {
        return this.balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    protected void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive", amount);
        }

        this.balance += amount;
        Transaction transaction = new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, amount,
                LocalDateTime.now(), this.balance, TransactionAgent.USER, this.accountHolder.getUserId(),
                this.accountNumber);
        this.transactions.add(transaction);
    }

    @Override
    public double checkBalance() {
        return balance;
    }
}
