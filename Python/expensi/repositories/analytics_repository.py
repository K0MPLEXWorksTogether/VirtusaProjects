import matplotlib.pyplot as plt
from matplotlib.figure import Figure

from repositories.expense_repository import ExpenseRepository
from models.analytics import Analytics


class AnalyticsRepository:
    def __init__(self, expense_repository: ExpenseRepository):
        self.__expense_repository = expense_repository

    def __get_analytics(self) -> Analytics:
        expenses = self.__expense_repository.find_all()
        return Analytics(expenses)

    def get_category_breakdown_chart(self) -> Figure:
        analytics = self.__get_analytics()
        breakdown = analytics.category_breakdown()
        if not breakdown:
            return None

        fig, ax = plt.subplots(figsize=(4, 3))

        labels = list(breakdown.keys())
        sizes = list(breakdown.values())

        ax.pie(sizes, labels=labels, autopct="%1.1f%%")
        ax.set_title("Category-wise Spending")
        return fig

    def get_monthly_spending_chart(self) -> Figure:
        analytics = self.__get_analytics()
        monthly = analytics.monthly_summary()

        if not monthly:
            return None

        fig, ax = plt.subplots(figsize=(4, 3))
        labels = list(monthly.keys())
        sizes = list(monthly.values())

        ax.pie(sizes, labels=labels, autopct="%1.1f%%")
        ax.set_title("Monthly Spending Distribution")

        return fig

    def get_total_spending(self) -> float:
        return self.__get_analytics().total_spending()

    def get_highest_spending_category(self):
        return self.__get_analytics().highest_spending_category()
