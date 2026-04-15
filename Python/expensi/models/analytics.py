from collections import defaultdict
from models.expense import Expense


class Analytics:
    def __init__(self, expenses: list[Expense]):
        self.__expenses = expenses

    def total_spending(self) -> float:
        return sum(exp.get_amount() for exp in self.__expenses)

    def category_breakdown(self) -> dict:
        breakdown = defaultdict(float)
        for exp in self.__expenses:
            breakdown[exp.get_category().get_name()] += exp.get_amount()
        return dict(breakdown)

    def highest_spending_category(self):
        breakdown = self.category_breakdown()
        if not breakdown:
            return None
        return max(breakdown, key=breakdown.get)

    def monthly_summary(self) -> dict:
        summary = defaultdict(float)
        for exp in self.__expenses:
            month = exp.get_date().strftime("%Y-%m")
            summary[month] += exp.get_amount()
        return dict(summary)
