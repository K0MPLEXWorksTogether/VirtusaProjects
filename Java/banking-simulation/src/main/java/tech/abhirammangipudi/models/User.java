package tech.abhirammangipudi.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID userId;
    private String firstName;
    private String lastName;
    private final LocalDateTime dateOfBirth;
    private final String email;
    private String address;
    private String phoneNumber;
    private final String username;
    private String passwordHash;
    private final LocalDateTime createdAt;
    private List<Account> accounts;

    public User(String firstName, String lastName, LocalDateTime dateOfBirth, String email, String address,
            String phoneNumber, String username, String passwordHash, List<Account> accounts) {
        this.userId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.accounts = accounts;
    }

    public User(UUID userId, String firstName, String lastName, LocalDateTime dateOfBirth, String email, String address,
            String phoneNumber, String username, String passwordHash, LocalDateTime createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", dateOfBirth="
                + dateOfBirth + ", email=" + email + ", address=" + address + ", phoneNumber=" + phoneNumber
                + ", username=" + username + ", passwordHash=" + passwordHash + ", createdAt=" + createdAt
                + ", accounts=" + accounts + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (dateOfBirth == null) {
            if (other.dateOfBirth != null)
                return false;
        } else if (!dateOfBirth.equals(other.dateOfBirth))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (passwordHash == null) {
            if (other.passwordHash != null)
                return false;
        } else if (!passwordHash.equals(other.passwordHash))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (accounts == null) {
            if (other.accounts != null)
                return false;
        } else if (!accounts.equals(other.accounts))
            return false;
        return true;
    }
}
