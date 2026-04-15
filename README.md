# Virtusa Projects

## Python (Expense Tracker)
Expensi, this expense tracker allows its users to track expenses, make and store different categories of their expenses, and finally allow the user to understand and analyze the expenses clearly.

### Key Features
- A clean and simple architecture. Every model maps to a repository, that stores or manages its data in a .csv file in the current directory.
- `matplotlib` is used for the charts and analytics.
- `tkinter` is used to render the UI, and allow the user to interact with the application.

This file can be run by first installing the dependencies, and then run by:
```bash
python3 -m venv .venv  # Creates a virtual environment
source .venv/bin/activate  # Activates the virtual environment
pip install -r requirements.txt # Installs all dependencies
python3 main.py # Runs the main.py script
```

## SQL (Hospital Management Database)
- This database design is centered on managing doctors, patients and their interactions as appointments and precscription of treatments.

### Key Features
- A ER diagram describing the entities and their relationships.
- A Relatational Database diagram describing the choice of the datatypes and nature of the relationships.
- A .sql file, that when run, creates the neccessary tables, inserts seed data, and creates stored procudures that run the needed analytics.
- These stored procedures can be connected to any application and called for analytics.
- These queries have been written using aggregate functions, joins, group by, order by and a good understanding of key design.

This project has been developed using MySQL as the database. It can be run my sourcing the .sql file:
```bash
mysql -u root -p hospital_db < database.sql
```

## Java (Banking System Simulation)
This java project, simulates a bank, where different customer interactions are modelled using the right object-oriented design, and every transaction is validated and consistent. 

### Key Features
- A good design of the `User`, `Transaction`, `Account` which is an abstract class, and `SavingsAccount` and `CurrentAccount`, which are derived classes of account. 
- Connection to `JDBC` via MySQL and PHPMyAdmin allow this application to store data persistantly and consistently.
- UI layer has been implemented using `AWT` for layout and `Swing` for the actual components of the application.
- This project successfully implements all SOLID principles, and includes custom checked and unchecked exceptions for consitency.

This project needs some dependecies such as MySQL Connector for connecting to the database. So it has been bootstrapped using the maven package manager for java. To run this application, first install the dependencies, and run:

```bash
mvn compile # Compiles the java project and installs dependencies
java -cp target/classes tech.abhirammangipudi.Main
```