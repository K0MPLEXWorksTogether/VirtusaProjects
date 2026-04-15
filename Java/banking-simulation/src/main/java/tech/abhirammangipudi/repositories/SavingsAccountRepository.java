package tech.abhirammangipudi.repositories;

import tech.abhirammangipudi.interfaces.Repository;
import tech.abhirammangipudi.models.SavingsAccount;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.utils.ConnectionSingleton;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.errors.ConnectionError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SavingsAccountRepository implements Repository<SavingsAccount, UUID> {

    private final UserRepository userRepo;

    public SavingsAccountRepository(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private SavingsAccount mapper(ResultSet rs) throws SQLException, ResourceNotFoundException {
        UUID userId = UUID.fromString(rs.getString("accountHolder"));
        User holder = userRepo.findById(userId);

        return new SavingsAccount(
                UUID.fromString(rs.getString(
                        "accountNumber")),
                holder,
                rs.getDouble("balance"),
                rs.getTimestamp("dateOpened").toLocalDateTime(),
                rs.getDouble("minimumBalance"),
                rs.getDouble("interestRate"));
    }

    @Override
    public void save(SavingsAccount account) {
        String sql = "INSERT INTO Accounts (accountNumber, accountHolder, dateOpened, balance, minimumBalance, interestRate, overdraftLimit) VALUES (?, ?, ?, ?, ?, ?, NULL)";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber().toString());
            stmt.setString(2, account.getAccountHolder().getUserId().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(account.getDateOpened()));
            stmt.setDouble(4, account.checkBalance());
            stmt.setDouble(5, account.getMinimumBalance());
            stmt.setDouble(6, account.getinterestRate());

            stmt.executeUpdate();
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during account save: " + e.getMessage());
        }
    }

    @Override
    public SavingsAccount findById(UUID id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Accounts WHERE accountNumber = ? AND interestRate IS NOT NULL";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper(rs);
            } else {
                throw new ResourceNotFoundException("Savings account not found: " + id, id.toString());
            }
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during account lookup: " + e.getMessage());
        }
    }

    @Override
    public List<SavingsAccount> findAll(int page, int limit) {
        List<SavingsAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts WHERE interestRate IS NOT NULL LIMIT ? OFFSET ?";
        int offset = (page - 1) * limit;

        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    accounts.add(mapper(rs));
                } catch (ResourceNotFoundException e) {
                    System.err.println("Skipping account: " + e.getMessage());
                }
            }
            return accounts;
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during fetch: " + e.getMessage());
        }
    }

    @Override
    public void update(UUID id, SavingsAccount entity) throws ResourceNotFoundException {
        String sql = "UPDATE Accounts SET balance = ?, minimumBalance = ? WHERE accountNumber = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, entity.checkBalance());
            stmt.setDouble(2, entity.getMinimumBalance());
            stmt.setString(3, id.toString());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0)
                throw new ResourceNotFoundException("Account not found for update.", id.toString());
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during account update: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException {
        String sql = "DELETE FROM Accounts WHERE accountNumber = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0)
                throw new ResourceNotFoundException("Account not found for deletion.", id.toString());
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during account deletion: " + e.getMessage());
        }
    }
}