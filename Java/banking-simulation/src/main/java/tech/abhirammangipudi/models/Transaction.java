package tech.abhirammangipudi.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
    }

    public enum TransactionAgent {
        BANK,
        USER
    }

    private final UUID transactionId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double balanceAfter;
    private final TransactionAgent transactionAgent;
    private final UUID userId;
    private final UUID accountId;

    public Transaction(UUID transactionId, TransactionType type, double amount, LocalDateTime timestamp,
            double balanceAfter, TransactionAgent transactionAgent, UUID userId, UUID accountId) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
        this.transactionAgent = transactionAgent;
        this.userId = userId;
        this.accountId = accountId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public TransactionAgent getTransactionAgent() {
        return transactionAgent;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
