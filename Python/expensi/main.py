from repositories.expense_repository import ExpenseRepository
from ui.expense_page import ExpensePage

if __name__ == "__main__":
    expense_repo = ExpenseRepository()
    expense_page = ExpensePage(expense_repo)
    expense_page.run()