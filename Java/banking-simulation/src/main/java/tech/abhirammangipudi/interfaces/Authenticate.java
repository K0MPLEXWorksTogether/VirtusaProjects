package tech.abhirammangipudi.interfaces;

import tech.abhirammangipudi.errors.AuthenticationException;

public interface Authenticate<T> {
    T login(String username, String password) throws AuthenticationException;

    void logout(T entity);
}
