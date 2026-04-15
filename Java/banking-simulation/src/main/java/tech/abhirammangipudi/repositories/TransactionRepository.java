package tech.abhirammangipudi.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.interfaces.Repository;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.utils.ConnectionSingleton;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.errors.ConnectionError;

public class TransactionRepository implements Repository<Transaction, UUID> {
    private Transaction mapper(ResultSet rs) throws SQLException {
        return new Transaction(
                UUID.fromString(rs.getString("transactionId")),
                Transaction.TransactionType.valueOf(rs.getString("transactionType")),
                rs.getDouble("amount"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getDouble("balanceAfter"),
                Transaction.TransactionAgent.valueOf(rs.getString("transactionAgent")),
                UUID.fromString(rs.getString("userId")),
                UUID.fromString(rs.getString("accountId")));
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException {
        throw new UnsupportedOperationException("This entity does not allow operation: Update");
    }

    @Override
    public List<Transaction> findAll(int page, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * limit;

        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapper(rs));
            }
            return transactions;
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during fetch: " + e.getMessage());
        }
    }

    @Override
    public Transaction findById(UUID id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Transactions WHERE transactionId = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper(rs);
            } else {
                throw new ResourceNotFoundException("Transaction not found: " + id, id.toString());
            }
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public void save(Transaction entity) {
        String sql = "INSERT INTO Transactions (transactionId, transactionType, amount, timestamp, balanceAfter, transactionAgent, userId, accountId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getTransactionId().toString());
            stmt.setString(2, entity.getType().name());
            stmt.setDouble(3, entity.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getTimestamp()));
            stmt.setDouble(5, entity.getBalanceAfter());
            stmt.setString(6, entity.getTransactionAgent().name());
            stmt.setString(7, entity.getUserId().toString());
            stmt.setString(8, entity.getAccountId().toString());

            stmt.executeUpdate();
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during transaction save: " + e.getMessage());
        }
    }

    @Override
    public void update(UUID id, Transaction entity) throws ResourceNotFoundException, UnsupportedOperationException {
        throw new UnsupportedOperationException("This entity does not allow operation: Update");
    }

    public List<Transaction> findByUserId(UUID userId, int page, int limit) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Transactions WHERE userId = ? DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int offset = (page - 1) * limit;
            stmt.setString(1, userId.toString());
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapper(rs));
            }

            return transactions;
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during transaction save: " + e.getMessage());
        }
    }
}
