package tech.abhirammangipudi.interfaces;

import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.models.Account;

public interface Transfer {
    void transfer(Account source, Account destination, double amount)
            throws InsufficientFundsException, InvalidAmountException, MinimumBalanceException, AuthenticationException, ResourceNotFoundException;
}
