from models.expense import Expense
from models.category import Category
from utils.csv_utils import CSVUtils
from datetime import datetime
from repositories.category_repository import CategoryRepository


class ExpenseRepository:
    def __init__(self, file_path: str = "assets/data/expenses.csv"):
        self.__file_path = file_path
        self.__headers = ["date", "category", "amount", "description"]
        CSVUtils.ensure_file_exists(self.__file_path, self.__headers)

    def save(self, expense: Expense):
        CSVUtils.append_row(self.__file_path, expense.to_dict(), self.__headers)

    def find_all(self) -> list:
        rows = CSVUtils.read_all(self.__file_path)
        return [self.__map_to_expense(row) for row in rows]

    def delete_all(self):
        CSVUtils.overwrite(self.__file_path, [], self.__headers)

    def __map_to_expense(self, row: dict) -> Expense:
        category_repo = CategoryRepository()
        category = category_repo.find_by_name(row["category"])
        if category is None:
            category = Category(row["category"])

        return Expense(
            date=datetime.strptime(row["date"], "%Y-%m-%d"),
            category=category,
            amount=float(row["amount"]),
            description=row["description"],
        )
