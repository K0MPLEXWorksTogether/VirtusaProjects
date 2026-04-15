package tech.abhirammangipudi.services;

import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.SavingsAccount;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.repositories.SavingsAccountRepository;
import tech.abhirammangipudi.repositories.TransactionRepository;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.BankException;
import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;

public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    private void validateOwnership(Account account) throws AuthenticationException {
        User currentUser = userService.getCurrentUser();
        if (!account.getAccountHolder().getUserId().equals(currentUser.getUserId())) {
            throw new AuthenticationException("Access Denied: You do not own this account.");
        }
    }

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository,
            TransactionRepository transactionRepository, UserService userService) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<SavingsAccount> getSavingsAccounts(int page, int limit) throws AuthenticationException {
        User currentUser = userService.getCurrentUser();
        return savingsAccountRepository.findAll(page, limit).stream()
                .filter(acc -> acc.getAccountHolder().equals(currentUser)).toList();
    }

    public List<Transaction> getAccountTransactions(UUID accountNumber, int page, int limit)
            throws ResourceNotFoundException, AuthenticationException {
        SavingsAccount account = savingsAccountRepository.findById(accountNumber);
        validateOwnership(account);

        return transactionRepository.findByUserId(account.getAccountHolder().getUserId(), page, limit).stream()
                .filter(transaction -> transaction.getAccountId().equals(accountNumber)).toList();
    }

    public void deposit(UUID accountNumber, double amount)
            throws ResourceNotFoundException, AuthenticationException, InvalidAmountException {
        SavingsAccount account = savingsAccountRepository.findById(accountNumber);
        validateOwnership(account);

        account.deposit(amount);
        Transaction latestTransaction = account.getTransactions().get(account.getTransactions().size() - 1);
        transactionRepository.save(latestTransaction);
        savingsAccountRepository.update(accountNumber, account);
    }

    public void withdraw(UUID accountNumber, double amount) {
        try {
            SavingsAccount account = savingsAccountRepository.findById(accountNumber);
            validateOwnership(account);

            account.withdraw(amount);
            Transaction latestTransaction = account.getTransactions().get(account.getTransactions().size() - 1);
            transactionRepository.save(latestTransaction);
            savingsAccountRepository.update(accountNumber, account);
        } catch (ResourceNotFoundException | AuthenticationException | InsufficientFundsException
                | MinimumBalanceException
                | InvalidAmountException err) {
            throw new BankException(err.getMessage());
        }
    }

    public double checkBalance(UUID accountNumber) throws ResourceNotFoundException, AuthenticationException {
        SavingsAccount account = savingsAccountRepository.findById(accountNumber);
        validateOwnership(account);

        return account.checkBalance();
    }

    public double checkIntrestRate(UUID accountNumber) throws ResourceNotFoundException, AuthenticationException {
        SavingsAccount account = savingsAccountRepository.findById(accountNumber);
        validateOwnership(account);

        return account.getinterestRate();
    }

    public double checkMinimumBalance(UUID accountNumber) throws ResourceNotFoundException, AuthenticationException {
        SavingsAccount account = savingsAccountRepository.findById(accountNumber);
        validateOwnership(account);

        return account.getMinimumBalance();
    }
}
