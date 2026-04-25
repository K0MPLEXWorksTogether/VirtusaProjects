package tech.abhirammangipudi.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.interfaces.Repository;
import tech.abhirammangipudi.models.CurrentAccount;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.utils.ConnectionSingleton;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.errors.ConnectionError;

public class CurrentAccountRepository implements Repository<CurrentAccount, UUID> {
    private final UserRepository userRepo;

    public CurrentAccountRepository(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private CurrentAccount mapper(ResultSet rs) throws SQLException, ResourceNotFoundException {
        UUID userId = UUID.fromString(rs.getString("accountHolder"));
        User holder = userRepo.findById(userId);

        return new CurrentAccount(
                UUID.fromString(rs.getString(
                        "accountNumber")),
                holder,
                rs.getDouble("balance"),
                rs.getTimestamp("dateOpened").toLocalDateTime(),
                rs.getDouble("minimumBalance"),
                rs.getDouble("overdraftLimit"));
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
            throw new RuntimeException("Database error during deletion: " + e.getMessage());
        }
    }

    @Override
    public List<CurrentAccount> findAll(int page, int limit) {
        List<CurrentAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts WHERE overdraftLimit IS NOT NULL LIMIT ? OFFSET ?";
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
                    System.err.println("Orphaned account found (No User): " + e.getMessage());
                }
            }
            return accounts;
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during list fetch: " + e.getMessage());
        }
    }

    @Override
    public CurrentAccount findById(UUID id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Accounts WHERE accountNumber = ? AND overdraftLimit IS NOT NULL";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper(rs);
            } else {
                throw new ResourceNotFoundException("Current account not found: " + id, id.toString());
            }
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during account lookup: " + e.getMessage());
        }
    }

    @Override
    public void save(CurrentAccount entity) {
        String sql = "INSERT INTO Accounts (accountNumber, accountHolder, dateOpened, balance, minimumBalance, overdraftLimit, interestRate) VALUES (?, ?, ?, ?, ?, ?, NULL)";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getAccountNumber().toString());
            stmt.setString(2, entity.getAccountHolder().getUserId().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getDateOpened()));
            stmt.setDouble(4, entity.checkBalance());
            stmt.setDouble(5, entity.getMinimumBalance());
            stmt.setDouble(6, entity.getOverdraftLimit());

            stmt.executeUpdate();
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during current account save: " + e.getMessage());
        }
    }

    @Override
    public void update(UUID id, CurrentAccount entity) throws ResourceNotFoundException {
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
}
