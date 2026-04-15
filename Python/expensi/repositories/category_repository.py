from models.category import Category
from utils.csv_utils import CSVUtils


class CategoryRepository:
    def __init__(self, file_path: str = "assets/data/categories.csv"):
        self.__file_path = file_path
        self.__headers = ["name"]

        CSVUtils.ensure_file_exists(self.__file_path, self.__headers)

    def save(self, category: Category):
        if self.exists(category.get_name()):
            return

        CSVUtils.append_row(self.__file_path, category.to_dict(), self.__headers)

    def find_all(self) -> list:
        rows = CSVUtils.read_all(self.__file_path)
        return [Category.from_dict(row) for row in rows]

    def find_by_name(self, name: str):
        rows = CSVUtils.read_all(self.__file_path)

        for row in rows:
            if row["name"].lower() == name.lower():
                return Category.from_dict(row)

        return None

    def exists(self, name: str) -> bool:
        return self.find_by_name(name) is not None

    def delete_all(self):
        CSVUtils.overwrite(self.__file_path, [], self.__headers)

    def delete_by_name(self, name: str) -> bool:
        rows = CSVUtils.read_all(self.__file_path)

        updated_rows = [row for row in rows if row["name"].lower() != name.lower()]
        if len(rows) == len(updated_rows):
            return False

        CSVUtils.overwrite(self.__file_path, updated_rows, self.__headers)
        return True
