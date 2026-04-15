package tech.abhirammangipudi.services;

import tech.abhirammangipudi.interfaces.Authenticate;
import tech.abhirammangipudi.repositories.UserRepository;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.errors.ResourceAlreadyExistsException;
import tech.abhirammangipudi.utils.PasswordUtils;

public class UserService implements Authenticate<User> {
    private final UserRepository userRepository;
    private User currentUser;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(User user) throws ResourceAlreadyExistsException {
        userRepository.save(user);
    }

    @Override
    public User login(String username, String password) throws AuthenticationException {
        try {
            User user = userRepository.findByUsername(username);
            if (user.getPasswordHash().equals(password)) {
                this.currentUser = user;
                return user;
            } else {
                throw new AuthenticationException("Invalid credentials");
            }
        } catch (ResourceNotFoundException rnf) {
            throw new AuthenticationException(String.format("No user with username: %s was found", username));
        }
    }

    public User getCurrentUser() throws AuthenticationException {
        if (currentUser == null) {
            throw new AuthenticationException("No user is currently logged in");
        }
        return currentUser;
    }

    @Override
    public void logout(User entity) {
        if (this.currentUser != null && this.currentUser.equals(entity)) {
            this.currentUser = null;
        }
    }

    public void updateProfile(User user) throws AuthenticationException, ResourceNotFoundException {
        if (currentUser == null) {
            throw new AuthenticationException("No user is currently logged in");
        }

        userRepository.update(user.getUserId(), user);
    }
}
