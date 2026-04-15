import tkinter as tk
from tkinter import ttk

from repositories.category_repository import CategoryRepository
from models.category import Category


class CategoryPage(tk.Frame):
    def __init__(self, master, category_repo: CategoryRepository):
        super().__init__(master)

        self.repo = category_repo

        title = tk.Label(self, text="Categories", font=("Arial", 18))
        title.pack(pady=10)
        self.tree = ttk.Treeview(self, columns=("name",), show="headings")
        self.tree.heading("name", text="Name")
        self.tree.pack(fill="both", expand=True, padx=20, pady=10)

        btn_frame = tk.Frame(self)
        btn_frame.pack(pady=10)
        tk.Button(btn_frame, text="Add Category", command=self.open_add_window).pack(
            side="left", padx=5
        )
        tk.Button(btn_frame, text="Delete All", command=self.delete_all).pack(
            side="left", padx=5
        )
        tk.Button(btn_frame, text="Delete By Name", command=self.delete_by_name).pack(
            side="left", padx=5
        )

        self.load_data()

    def load_data(self):
        for row in self.tree.get_children():
            self.tree.delete(row)

        for category in self.repo.find_all():
            self.tree.insert("", "end", values=(category.get_name(),))

    def delete_all(self):
        self.repo.delete_all()
        self.load_data()

    def delete_by_name(self):
        selected = self.tree.selection()

        if not selected:
            return

        name = self.tree.item(selected[0])["values"][0]
        self.repo.delete_by_name(name)
        self.load_data()

    def open_add_window(self):
        win = tk.Toplevel(self)
        win.title("Add Category")
        win.geometry("250x150")

        tk.Label(win, text="Category Name").pack()
        name_entry = tk.Entry(win)
        name_entry.pack()

        def save():
            category = Category(name_entry.get())
            self.repo.save(category)
            win.destroy()
            self.load_data()

        tk.Button(win, text="Save", command=save).pack(pady=10)
