package tech.abhirammangipudi.models;

import java.time.LocalDateTime;
import java.util.UUID;

import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.interfaces.Withdraw;
import tech.abhirammangipudi.models.Transaction.TransactionAgent;
import tech.abhirammangipudi.models.Transaction.TransactionType;

public class CurrentAccount extends Account implements Withdraw {
    private final double overdraftLimit;

    public CurrentAccount(UUID accountNumber, User accountHolder, double balance, LocalDateTime dateOpened,
            double minimumBalance, double overdraftLimit) {
        super(accountNumber, accountHolder, dateOpened, balance, minimumBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(User accountHolder, double balance, LocalDateTime dateOpened,
            double minimumBalance, double overdraftLimit) {
        super(accountHolder, balance, dateOpened, minimumBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(User accountHolder, double balance, double minimumBalance,
            double overdraftLimit) {
        super(accountHolder, balance, minimumBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getCurrentOverdraft() {
        return Math.max(0, -getBalance());
    }

    @Override
    public void withdraw(double amount)
            throws InsufficientFundsException, InvalidAmountException, MinimumBalanceException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive", amount);
        }

        if (getBalance() + overdraftLimit < amount) {
            throw new InsufficientFundsException("Overdraft limits exceeded", amount);
        }

        setBalance(getBalance() - amount);
        Transaction transaction = new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, amount,
                LocalDateTime.now(), getBalance(), TransactionAgent.USER, getAccountHolder().getUserId(),
                getAccountNumber());
        addTransaction(transaction);
    }
}
