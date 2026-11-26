"""Imperative solutions to Project Euler problems 2 and 19."""


def sum_even_fibonacci(limit: int) -> int:
    a, b = 1, 2
    total = 0
    while a <= limit:
        if a % 2 == 0:
            total += a
        a, b = b, a + b
    return total


def is_leap_year(year: int) -> bool:
    return year % 400 == 0 or (year % 4 == 0 and year % 100 != 0)


def days_in_month(year: int, month: int) -> int:
    if month == 2:
        return 29 if is_leap_year(year) else 28
    if month in (4, 6, 9, 11):
        return 30
    return 31


def count_sundays(start_year: int = 1901, end_year: int = 2000) -> int:
    if start_year < 1900 or end_year < start_year:
        raise ValueError("Expected 1900 <= start_year <= end_year")

    weekday = 0  # 1 January 1900 was a Monday; Sunday is 6.
    total = 0
    for year in range(1900, end_year + 1):
        for month in range(1, 13):
            if year >= start_year and weekday == 6:
                total += 1
            weekday = (weekday + days_in_month(year, month)) % 7
    return total


if __name__ == "__main__":
    print("Problem 2:")
    print(sum_even_fibonacci(4_000_000))
    print("Problem 19:")
    print(count_sundays())
