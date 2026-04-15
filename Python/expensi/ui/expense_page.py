import tkinter as tk
from tkinter import ttk

from repositories.expense_repository import ExpenseRepository
from repositories.category_repository import CategoryRepository
from models.expense import Expense
from models.category import Category
from datetime import datetime


class ExpensePage(tk.Frame):
    def __init__(
        self, master, expense_repo: ExpenseRepository, category_repo: CategoryRepository
    ):
        super().__init__(master)

        self.repo = expense_repo
        self.category_repo = category_repo

        nav_frame = tk.Frame(self)
        nav_frame.pack()
        tk.Button(
            nav_frame,
            text="Go to Categories",
            command=lambda: master.switch_frame("category"),
        ).pack()
        tk.Button(
            nav_frame,
            text="Go to Analytics",
            command=lambda: master.switch_frame("analytics"),
        ).pack()

        title = tk.Label(self, text="Expenses", font=("Arial", 18))
        title.pack(pady=10)
        self.tree = ttk.Treeview(
            self, columns=("date", "category", "amount", "description"), show="headings"
        )

        for col in ("date", "category", "amount", "description"):
            self.tree.heading(col, text=col.capitalize())

        self.tree.pack(fill="both", expand=True, padx=20, pady=10)
        btn_frame = tk.Frame(self)
        btn_frame.pack(pady=10)

        tk.Button(btn_frame, text="Add Expense", command=self.open_add_window).pack(
            side="left", padx=5
        )
        tk.Button(btn_frame, text="Delete All", command=self.delete_all).pack(
            side="left", padx=5
        )

        self.load_data()

    def load_data(self):
        for row in self.tree.get_children():
            self.tree.delete(row)

        for expense in self.repo.find_all():
            self.tree.insert(
                "",
                "end",
                values=(
                    expense.get_date().strftime("%Y-%m-%d"),
                    expense.get_category().get_name(),
                    expense.get_amount(),
                    expense.get_description(),
                ),
            )

    def delete_all(self):
        self.repo.delete_all()
        self.load_data()

    def open_add_window(self):
        win = tk.Toplevel(self)
        win.title("Add Expense")
        win.geometry("300x300")

        tk.Label(win, text="Date (YYYY-MM-DD)").pack()
        date_entry = tk.Entry(win)
        date_entry.pack()

        tk.Label(win, text="Category").pack()
        category_entry = tk.Entry(win)
        category_entry.pack()

        tk.Label(win, text="Amount").pack()
        amount_entry = tk.Entry(win)
        amount_entry.pack()

        tk.Label(win, text="Description").pack()
        desc_entry = tk.Entry(win)
        desc_entry.pack()

        def save():
            expense = Expense(
                date=datetime.strptime(date_entry.get(), "%Y-%m-%d"),
                category=Category(category_entry.get()),
                amount=float(amount_entry.get()),
                description=desc_entry.get(),
            )

            self.repo.save(expense)
            if not (self.category_repo.exists(category_entry.get())):
                self.category_repo.save(Category(category_entry.get()))
            win.destroy()
            self.load_data()

        tk.Button(win, text="Save", command=save).pack(pady=10)
