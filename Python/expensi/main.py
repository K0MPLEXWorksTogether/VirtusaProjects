import tkinter as tk

from ui.expense_page import ExpensePage
from ui.category_page import CategoryPage
from ui.analytics_page import AnalyticsPage

from repositories.expense_repository import ExpenseRepository
from repositories.category_repository import CategoryRepository
from repositories.analytics_repository import AnalyticsRepository


class App(tk.Tk):
    def __init__(self):
        super().__init__()

        self.title("Expense Tracker")
        self.geometry("800x500")

        self.category_repo = CategoryRepository()
        self.expense_repo = ExpenseRepository()
        self.analytics_repo = AnalyticsRepository(self.expense_repo)

        self._frame = None
        self.switch_frame("expense")

    def switch_frame(self, page_name: str):
        if self._frame is not None:
            self._frame.destroy()

        if page_name == "expense":
            self._frame = ExpensePage(self, self.expense_repo, self.category_repo)
        elif page_name == "category":
            self._frame = CategoryPage(self, self.category_repo)
        elif page_name == "analytics":
            self._frame = AnalyticsPage(self, self.analytics_repo)

        self._frame.pack(fill="both", expand=True)


if __name__ == "__main__":
    app = App()
    app.mainloop()
