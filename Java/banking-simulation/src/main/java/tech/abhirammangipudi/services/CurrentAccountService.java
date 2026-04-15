package tech.abhirammangipudi.services;

import java.util.List;
import java.util.UUID;

import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.CurrentAccount;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.repositories.CurrentAccountRepository;
import tech.abhirammangipudi.repositories.TransactionRepository;
import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.BankException;
import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;

public class CurrentAccountService {
    private final CurrentAccountRepository currentAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    private void validateOwnership(Account account) throws AuthenticationException {
        User currentUser = userService.getCurrentUser();
        if (!account.getAccountHolder().getUserId().equals(currentUser.getUserId())) {
            throw new AuthenticationException("Access Denied: You do not own this account.");
        }
    }

    public CurrentAccountService(CurrentAccountRepository currentAccountRepository,
            TransactionRepository transactionRepository, UserService userService) {
        this.currentAccountRepository = currentAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<CurrentAccount> getCurrentAccounts(int page, int limit) throws AuthenticationException {
        User currentUser = userService.getCurrentUser();
        return currentAccountRepository.findAll(page, limit).stream()
                .filter(acc -> acc.getAccountHolder().equals(currentUser)).toList();
    }

    public List<Transaction> getAccountTransactions(UUID accountNumber, int page, int limit)
            throws AuthenticationException, ResourceNotFoundException {
        CurrentAccount currentAccount = currentAccountRepository.findById(accountNumber);
        validateOwnership(currentAccount);

        return transactionRepository.findByUserId(currentAccount.getAccountHolder().getUserId(), page, limit).stream()
                .filter(transaction -> transaction.getAccountId().equals(accountNumber)).toList();
    }

    public boolean checkOverdraft(UUID accountNumber) throws AuthenticationException, ResourceNotFoundException {
        CurrentAccount currentAccount = currentAccountRepository.findById(accountNumber);
        validateOwnership(currentAccount);

        return currentAccount.checkBalance() < 0;
    }

    public void deposit(UUID accountNumber, double amount)
            throws AuthenticationException, ResourceNotFoundException, InvalidAmountException {
        CurrentAccount account = currentAccountRepository.findById(accountNumber);
        validateOwnership(account);

        account.deposit(amount);
        Transaction latestTransaction = account.getTransactions().get(account.getTransactions().size() - 1);
        transactionRepository.save(latestTransaction);
        currentAccountRepository.update(accountNumber, account);
    }

    public void withdraw(UUID accountNumber, double amount) {
        try {
            CurrentAccount account = currentAccountRepository.findById(accountNumber);
            validateOwnership(account);

            account.withdraw(amount);
            Transaction latestTransaction = account.getTransactions().get(account.getTransactions().size() - 1);
            transactionRepository.save(latestTransaction);
            currentAccountRepository.update(accountNumber, account);
        } catch (ResourceNotFoundException | AuthenticationException | InsufficientFundsException
                | MinimumBalanceException
                | InvalidAmountException err) {
            throw new BankException(err.getMessage());
        }
    }

    public double checkBalance(UUID accountNumber) throws ResourceNotFoundException, AuthenticationException {
        CurrentAccount account = currentAccountRepository.findById(accountNumber);
        validateOwnership(account);

        return account.checkBalance();
    }
}
