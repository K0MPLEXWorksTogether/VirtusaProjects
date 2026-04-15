import tkinter as tk
from tkinter import ttk
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from repositories.analytics_repository import AnalyticsRepository


class AnalyticsPage(tk.Frame):
    def __init__(self, master, analytics_repo: AnalyticsRepository):
        super().__init__(master)
        self.analytics_repo = analytics_repo

        title = tk.Label(self, text="Analytics", font=("Arial", 18))
        title.pack(pady=10)

        summary_frame = tk.Frame(self)
        summary_frame.pack(pady=10)

        total = self.analytics_repo.get_total_spending()
        highest = self.analytics_repo.get_highest_spending_category()

        total_label = tk.Label(
            summary_frame, text=f"Total Spending: ₹{total}", font=("Arial", 14, "bold")
        )
        total_label.grid(row=0, column=0, padx=20)

        category_label = tk.Label(
            summary_frame, text=f"Top Category: {highest}", font=("Arial", 14)
        )
        category_label.grid(row=0, column=1, padx=20)

        charts_frame = tk.Frame(self)
        charts_frame.pack(fill="both", expand=True, pady=10)

        category_fig = self.analytics_repo.get_category_breakdown_chart()

        if category_fig:
            category_canvas = FigureCanvasTkAgg(category_fig, master=charts_frame)
            category_canvas.draw()
            category_canvas.get_tk_widget().grid(row=0, column=0, padx=10, pady=10)
        else:
            tk.Label(charts_frame, text="No category data").grid(row=0, column=0)

        monthly_fig = self.analytics_repo.get_monthly_spending_chart()

        if monthly_fig:
            monthly_canvas = FigureCanvasTkAgg(monthly_fig, master=charts_frame)
            monthly_canvas.draw()
            monthly_canvas.get_tk_widget().grid(row=0, column=1, padx=10, pady=10)
        else:
            tk.Label(charts_frame, text="No monthly data").grid(row=0, column=1)

        charts_frame.grid_columnconfigure(0, weight=1)
        charts_frame.grid_columnconfigure(1, weight=1)
