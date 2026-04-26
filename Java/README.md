# Bank Simulation Using JDBC And Swing

This project simulates a user client and service, as well as the bank client and services. It stores the data persistently in a MySQL database, and displays the user UI for interaction.

## Demo Video
![Bank Demo](assets/vokoscreenNG-2026-04-26_12-40-55.mp4)

## Models
The models are the basic design blocks of this project. They are encapsulated classes with properties and methods. They are the objects that will hold the current state of the application.

- **User**: This is the bank user. They have many properties such as address and phone number, but importantly, every user has a username and password.
- **Account**: This is an abstract class. It represents the generalized properies of any bank account, be it current or savings.
- **CurrentAccount**: A current account is a specialization on account. It allows withdraw over a certain overdraft limit, and has no minimum balance.
- **SavingsAccount:** A savings account is a specialization on account. It has a minimum balance requirement and pays intrest every year. 
- **Transaction:** Transactions are the operations such as deposit and withdraw. These are useful for the double ledger accounting and bookkeeping for an account.

## Interfaces
The interfaces represent different types of behaviors available around the bank.

- **Authenticate**: Authenticate models `login` and `logout`, for restriction on method access.
- **CheckBalance**: It is a behavior allowed for every account. Any account can check their balance.
- **Deposit**: All accounts allow deposit, the `Account` class implements it.
- **Repository**: Any repository class must implement this interface. These methods are bare minimum. It is a generic interface.
- **Transfer**: Transfer allows transfer from one bank account to another. It is implemented by `TransferService`.
- **Withdraw**: While any account allows withdraw, it is implemented differently in current and savings account. A savings account does not allow withdraw over mininum balance, while current does not allow over overdraft limit.

## Repositories
These are the classes that connect the state (models) to a database to allow the state to persist. The use `ConnectionSingleton` from `utils` to connect to the MySQL database, and every class implements the generic interface `Repository` with the appropriate model.

## Services
These classes utilize repository and models to provide various services to the user. They allow a user or bank to actually interact and get or set the data they need.

## UI
These are the classes that are responsible for proving the UI pages. `BankingApp` is the root `JFrame`, and every other page is a `JPanel`. They interface with services to provide the user an interface to interfact with the application.

## SOLID
This project implements all the SOLID principles. Here's a demo on how.
- **Single Resposibility Principle**: No class interacts with another class without and interface in between. For example, the state (models) are seperate from persistance (repositories)
- **Open/Closed Principle**: Every class is broken into its smallest unit. Behavior and state are seperated. For example, say I create another account called `CheckingAccount` which has both overdraft and intrest, then I can freely extend `Account`, and implement `Withdraw` accordingly.
- **Liskov Substitution Principle**: Every subclass can be replaced with its predecesor. Infact, this is seen several times in the code itself. I have used general `Account` and specialized `SavingsAccount` interchangably.
- **Interface Segregation Principle**: I have perfectly broken all transactions into their smallest parts. I have used this principle while implementing `Withdraw`.
- **Dependency Inversion Principle**: As you can see, the applications UI and services build upon the models and repository. I have slowly increased functionality from models -> repository -> service -> ui. All dependencies only depend on low level dependencies.