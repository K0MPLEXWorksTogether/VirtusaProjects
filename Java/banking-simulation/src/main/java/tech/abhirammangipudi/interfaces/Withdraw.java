package tech.abhirammangipudi.interfaces;

import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;

public interface Withdraw {
    void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException, MinimumBalanceException;
}
