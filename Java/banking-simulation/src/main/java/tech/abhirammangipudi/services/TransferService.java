package tech.abhirammangipudi.services;

import java.util.List;

import tech.abhirammangipudi.errors.AuthenticationException;
import tech.abhirammangipudi.errors.InsufficientFundsException;
import tech.abhirammangipudi.errors.InvalidAmountException;
import tech.abhirammangipudi.errors.MinimumBalanceException;
import tech.abhirammangipudi.interfaces.Transfer;
import tech.abhirammangipudi.interfaces.Withdraw;
import tech.abhirammangipudi.models.Account;
import tech.abhirammangipudi.models.Transaction;
import tech.abhirammangipudi.models.User;
import tech.abhirammangipudi.repositories.TransactionRepository;

public class TransferService implements Transfer {
    private final UserService userService;
    private final TransactionRepository transactionRepo;

    public TransferService(UserService userService, TransactionRepository transactionRepo) {
        this.userService = userService;
        this.transactionRepo = transactionRepo;
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
            InvalidAmountException, MinimumBalanceException, AuthenticationException {
        validateOwnership(source);
        validateOwnership(destination);

        if (source instanceof Withdraw) {
            Withdraw withdrawableSource = (Withdraw) source;
            withdrawableSource.withdraw(amount);
        } else {
            throw new UnsupportedOperationException("This account type does not allow withdrawal");
        }

        destination.deposit(amount);
        persistLatestTransaction(source);
        persistLatestTransaction(destination);
    }
}
