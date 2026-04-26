package tech.abhirammangipudi.services;

import java.util.List;

import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.errors.ResourceNotFoundException;
import tech.abhirammangipudi.interfaces.Transfer;
import tech.abhirammangipudi.interfaces.Withdraw;
import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.CurrentAccount;
import tech.abhirammangipudi.models.SavingsAccount;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.repositories.CurrentAccountRepository;
import tech.abhirammangipudi.repositories.SavingsAccountRepository;
import tech.abhirammangipudi.repositories.TransactionRepository;

public class TransferService implements Transfer {
    private final UserService userService;
    private final TransactionRepository transactionRepo;
    private final CurrentAccountRepository currentAccountRepository;
    private final SavingsAccountRepository savingsAccountRepository;


    public TransferService(UserService userService, TransactionRepository transactionRepo, CurrentAccountRepository currentAccountRepository, SavingsAccountRepository savingsAccountRepository) {
        this.userService = userService;
        this.transactionRepo = transactionRepo;
        this.currentAccountRepository = currentAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    private void validateOwnership(Account account) throws AuthenticationException {
        User currentUser = userService.getCurrentUser();
        if (!account.getAccountHolder().getUserId().equals(currentUser.getUserId())) {
            throw new AuthenticationException("Access Denied: You do not own this account.");
        }
    }

    private void persistLatestTransaction(Account account) {
        List<Transaction> txs = account.getTransactions();
        if (!txs.isEmpty()) {
            transactionRepo.save(txs.get(txs.size() - 1));
        }
    }

    @Override
    public void transfer(Account source, Account destination, double amount) throws InsufficientFundsException,
            InvalidAmountException, MinimumBalanceException, AuthenticationException, ResourceNotFoundException{
        validateOwnership(source);
        validateOwnership(destination);

        if (source instanceof Withdraw) {
            Withdraw withdrawableSource = (Withdraw) source;
            withdrawableSource.withdraw(amount);
        } else {
            throw new UnsupportedOperationException("This account type does not allow withdrawal");
        }

        destination.deposit(amount);
        if (source instanceof CurrentAccount) {
            currentAccountRepository.update(source.getAccountNumber(), (CurrentAccount) source);
        } else {
            savingsAccountRepository.update(source.getAccountNumber(), (SavingsAccount) source);
        }

        if (destination instanceof CurrentAccount) {
            currentAccountRepository.update(destination.getAccountNumber(), (CurrentAccount) destination);
        } else {
            savingsAccountRepository.update(destination.getAccountNumber(), (SavingsAccount) destination);
        }
        persistLatestTransaction(source);
        persistLatestTransaction(destination);
    }
}
