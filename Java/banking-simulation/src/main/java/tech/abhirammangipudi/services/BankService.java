package tech.abhirammangipudi.services;

import java.util.List;
import java.util.ArrayList;

import tech.abhirammangipudi.repositories.CurrentAccountRepository;
import tech.abhirammangipudi.repositories.SavingsAccountRepository;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.SavingsAccount;

public class BankService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final String bankUsername;
    private final String bankPassword;
    private boolean isLoggedIn = false;

    public BankService(SavingsAccountRepository savingsAccountRepository,
            CurrentAccountRepository currentAccountRepository, String bankUsername, String bankPassword) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.currentAccountRepository = currentAccountRepository;
        this.bankUsername = bankUsername;
        this.bankPassword = bankPassword;
    }

    public BankService(SavingsAccountRepository savingsAccountRepository,
            CurrentAccountRepository currentAccountRepository) throws AuthenticationException {
        this.savingsAccountRepository = savingsAccountRepository;
        this.currentAccountRepository = currentAccountRepository;
        this.bankUsername = System.getenv("BANK_USERNAME");
        this.bankPassword = System.getenv("BANK_PASSWORD");

        if (this.bankUsername == null || this.bankPassword == null) {
            throw new AuthenticationException("Bank username and password are not set in the environment");
        }
    }

    public void login(String username, String password) throws AuthenticationException {
        if (!username.equals(this.bankUsername) || !password.equals(this.bankPassword)) {
            throw new AuthenticationException("The credentials provided are incorrect");
        }
        this.isLoggedIn = true;
    }

    public List<Account> getAllAccounts() throws AuthenticationException {
        if (this.isLoggedIn) {
            List<Account> allAccounts = new ArrayList<>();
            allAccounts.addAll(this.savingsAccountRepository.findAll(1, 100));
            allAccounts.addAll(this.currentAccountRepository.findAll(1, 100));
            return allAccounts;
        } else {
            throw new AuthenticationException("Unauthorized. Login to run this operation");
        }
    }

    public void processIntrest() throws InvalidAmountException, AuthenticationException, ResourceNotFoundException {
        if (isLoggedIn) {
            List<SavingsAccount> allSavings = this.savingsAccountRepository.findAll(1, 10000);
            for (SavingsAccount account : allSavings) {
                account.depositInterest();
                savingsAccountRepository.update(account.getAccountNumber(), account);
            }
        } else {
            throw new AuthenticationException("Unauthorized. Login to run this operation");
        }
    }

    public double getBankAssets() throws AuthenticationException {
        if (isLoggedIn) {
            double total = 0;
            total += savingsAccountRepository.findAll(1, 10000).stream().mapToDouble(Account::checkBalance).sum();
            total += currentAccountRepository.findAll(1, 10000).stream().mapToDouble(Account::checkBalance).sum();
            return total;
        } else {
            throw new AuthenticationException("Unauthorized. Login to run this operation");
        }
    }
}
