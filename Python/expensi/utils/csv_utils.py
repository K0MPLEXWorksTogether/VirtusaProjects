import csv
import os


class CSVUtils:
    @staticmethod
    def ensure_file_exists(file_path: str, headers: list):
        if not os.path.exists(file_path):
            with open(file_path, mode="w", newline="") as file:
                writer = csv.writer(file)
                writer.writerow(headers)

    @staticmethod
    def read_all(file_path: str) -> list:
        if not os.path.exists(file_path):
            return []

        with open(file_path, mode="r", newline="") as file:
            reader = csv.DictReader(file)
            return list(reader)

    @staticmethod
    def append_row(file_path: str, row: dict, headers: list):
        file_exists = os.path.exists(file_path)

        with open(file_path, mode="a", newline="") as file:
            writer = csv.DictWriter(file, fieldnames=headers)
            if not file_exists:
                writer.writeheader()
            writer.writerow(row)

    @staticmethod
    def overwrite(file_path: str, rows: list, headers: list):
        with open(file_path, mode="w", newline="") as file:
            writer = csv.DictWriter(file, fieldnames=headers)
            writer.writeheader()
            writer.writerows(rows)
