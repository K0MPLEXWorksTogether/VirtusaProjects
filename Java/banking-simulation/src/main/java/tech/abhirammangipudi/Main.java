package tech.abhirammangipudi;

import javax.swing.*;


import tech.abhirammangipudi.repositories.*;
import tech.abhirammangipudi.services.*;
import tech.abhirammangipudi.ui.BankingApp;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserRepository userRepo = new UserRepository();
            SavingsAccountRepository savingsRepo = new SavingsAccountRepository(userRepo);
            CurrentAccountRepository currentAccountRepository = new CurrentAccountRepository(userRepo);
            TransactionRepository transactionRepository = new TransactionRepository();

            UserService userService = new UserService(userRepo);
            SavingsAccountService savingsService = new SavingsAccountService(savingsRepo, transactionRepository,
                    userService);
            CurrentAccountService currentService = new CurrentAccountService(currentAccountRepository,
                    transactionRepository, userService);
            TransferService transferService = new TransferService(userService, transactionRepository);

            BankService bankService = new BankService(savingsRepo, currentAccountRepository, "SampleBank",
                    "SamplePassword");
            new BankingApp(userService, savingsService, currentService, transferService, bankService, transactionRepository);
        });
    }
}