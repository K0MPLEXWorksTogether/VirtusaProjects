package tech.abhirammangipudi.interfaces;

import tech.abhirammangipudi.errors.InvalidAmountException;


public interface Deposit {
    void deposit(double amount) throws InvalidAmountException;
}
