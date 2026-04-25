package tech.abhirammangipudi.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.interfaces.Repository;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.utils.ConnectionSingleton;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.errors.ConnectionError;

public class UserRepository implements Repository<User, UUID> {
    private User mapper(ResultSet rs) throws SQLException {
        return new User(
                UUID.fromString(rs.getString(
                        "userId")),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getTimestamp("dateOfBirth").toLocalDateTime(),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("phoneNumber"),
                rs.getString("username"),
                rs.getString("passwordHash"),
                rs.getTimestamp("createdAt").toLocalDateTime());
    }

    @Override
    public void deleteById(UUID id) throws ResourceNotFoundException {
        String sql = "DELETE FROM Users WHERE userId = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            int rowsEffected = stmt.executeUpdate();
            if (rowsEffected == 0)
                throw new ResourceNotFoundException("User not found with id: " + id.toString(), id.toString());
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll(int page, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users LIMIT ? OFFSET ?";
        int offset = (page - 1) * limit;
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapper(rs));
            }
            return users;
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }

    @Override
    public User findById(UUID id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper(rs);
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + id.toString(), id.toString());
            }
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }

    public User findByUsername(String username) throws ResourceNotFoundException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapper(rs);
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + username, username);
            }
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO Users (userId, firstName, lastName, dateOfBirth, email, address, phoneNumber, username, passwordHash, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId().toString());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getDateOfBirth()));
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getPhoneNumber());
            stmt.setString(8, user.getUsername());
            stmt.setString(9, user.getPasswordHash());
            stmt.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));

            stmt.executeUpdate();
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }

    @Override
    public void update(UUID id, User entity) throws ResourceNotFoundException {
        String sql = "UPDATE Users SET firstName = ?, lastName = ?, address = ?, phoneNumber = ?, passwordHash = ? WHERE userId = ?";

        try (Connection conn = ConnectionSingleton.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setString(3, entity.getAddress());
            stmt.setString(4, entity.getPhoneNumber());
            stmt.setString(5, entity.getPasswordHash());
            stmt.setString(6, entity.getUserId().toString());

            int rowsEffected = stmt.executeUpdate();
            if (rowsEffected == 0)
                throw new ResourceNotFoundException("User not found with id: " + id.toString(), id.toString());
        } catch (SQLException | ConnectionError e) {
            throw new RuntimeException("Database error during save: " + e.getMessage());
        }
    }
}
