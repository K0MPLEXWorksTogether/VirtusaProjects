class Category:
    def __init__(self, name: str):
        self.__name = name

    def get_name(self) -> str:
        return self.__name

    def set_name(self, name: str):
        if not name or not name.strip():
            raise ValueError("Category name cannot be empty")
        self.__name = name.strip()

    def to_dict(self):
        return {"name": self.__name}

    @staticmethod
    def from_dict(data: dict):
        return Category(name=data["name"])

    def __str__(self):
        return f"Category(name={self.__name})"
