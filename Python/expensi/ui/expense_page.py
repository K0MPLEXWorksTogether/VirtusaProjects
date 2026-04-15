import tkinter as tk
from tkinter import ttk
from datetime import datetime

from repositories.expense_repository import ExpenseRepository
from models.expense import Expense
from models.category import Category

class ExpensePage:
    def __init__(self, expense_repo: ExpenseRepository):
        self.root = tk.Tk()
        self.root.title("Expenses")
        self.root.geometry("900x600")
        self.expense_repo = expense_repo

        self.columns = ("Date", "Category", "Amount", "Description")
        main_frame = tk.Frame(self.root)
        main_frame.pack(fill="both", expand=True, padx=10, pady=10)
        table_frame = tk.Frame(main_frame)
        table_frame.pack(fill="both", expand=True)

        self.tree = ttk.Treeview(table_frame, columns=self.columns, show="headings")

        for col in self.columns:
            self.tree.heading(col, text=col)
            self.tree.column(col, anchor="center", width=150)
        self.load_rows()
        scrollbar = ttk.Scrollbar(table_frame, orient="vertical", command=self.tree.yview)
        self.tree.configure(yscroll=scrollbar.set)

        self.tree.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
        button_frame = tk.Frame(main_frame)
        button_frame.pack(fill="x", pady=10)

        add_button = tk.Button(button_frame, text="Add Expense", width=15, command=self.add_expense)
        delete_button = tk.Button(button_frame, text="Delete All", width=15, command=self.expense_repo.delete_all)

        add_button.pack(side="left", padx=10)
        delete_button.pack(side="left", padx=10)

    def load_rows(self):
        data = self.expense_repo.find_all()
        for row in data:
            self.tree.insert("", tk.END, values=row)

    def add_expense(self):
        dialog = tk.Toplevel(self.root)
        dialog.title("Add Expense")
        dialog.geometry("300x300")

        tk.Label(dialog, text="Date (YYYY-MM-DD)").pack()
        date_entry = tk.Entry(dialog)
        date_entry.pack()

        tk.Label(dialog, text="Category").pack()
        category_entry = tk.Entry(dialog)
        category_entry.pack()

        tk.Label(dialog, text="Amount").pack()
        amount_entry = tk.Entry(dialog)
        amount_entry.pack()

        tk.Label(dialog, text="Description").pack()
        desc_entry = tk.Entry(dialog)
        desc_entry.pack()
        tk.Button(
            dialog,
            text="Save",
            command=lambda: self.__submit(
                dialog,
                date_entry,
                category_entry,
                amount_entry,
                desc_entry
            )
        ).pack(pady=10)

    def __submit(self, dialog, date_entry, category_entry, amount_entry, desc_entry) -> None:
        try:
            expense = Expense(
                date=datetime.strptime(date_entry.get(), "%Y-%m-%d"),
                category=Category(category_entry.get()),
                amount=float(amount_entry.get()),
                description=desc_entry.get()
            )
            self.expense_repo.save(expense)
            self.load_rows()

            dialog.destroy()

        except Exception as e:
            print("Error:", e)

    def run(self):
        self.root.mainloop()
