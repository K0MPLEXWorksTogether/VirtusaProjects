from datetime import datetime
from models.category import Category


class Expense:
    def __init__(
        self, date: datetime, category: Category, amount: float, description: str
    ):
        self.__date = date
        self.__category = category
        self.__amount = amount
        self.__description = description

    def get_date(self) -> datetime:
        return self.__date

    def get_category(self) -> Category:
        return self.__category

    def get_amount(self) -> float:
        return self.__amount

    def get_description(self) -> str:
        return self.__description

    def set_date(self, date: datetime):
        if not isinstance(date, datetime):
            raise ValueError("Date must be a datetime object")
        self.__date = date

    def set_category(self, category: Category):
        if not isinstance(category, Category):
            raise ValueError("Invalid category")
        self.__category = category

    def set_amount(self, amount: float):
        if amount <= 0:
            raise ValueError("Amount must be positive")
        self.__amount = amount

    def set_description(self, description: str):
        self.__description = description.strip()

    def to_dict(self):
        return {
            "date": self.__date.strftime("%Y-%m-%d"),
            "category": self.__category.get_name(),
            "amount": self.__amount,
            "description": self.__description,
        }

    @staticmethod
    def from_dict(data: dict):
        return Expense(
            date=datetime.strptime(data["date"], "%Y-%m-%d"),
            category=Category(data["category"]),
            amount=data["amount"],
            description=data["description"],
        )

    def __str__(self):
        return f"{self.__date.date()} | {self.__category.get_name()} | ${self.__amount} | {self.__description}"
