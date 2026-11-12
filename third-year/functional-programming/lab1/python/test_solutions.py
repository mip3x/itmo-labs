"""Check iterative solutions against examples and the standard calendar."""

import unittest
from datetime import date

from solutions import count_sundays, days_in_month, is_leap_year, sum_even_fibonacci


class SolutionsTests(unittest.TestCase):
    def test_fibonacci_limits(self):
        cases = [(0, 0), (1, 0), (2, 2), (8, 10), (100, 44), (4_000_000, 4_613_732)]
        for limit, expected in cases:
            with self.subTest(limit=limit):
                self.assertEqual(sum_even_fibonacci(limit), expected)

    def test_leap_years(self):
        for year, expected in [(1900, False), (2000, True), (2024, True), (2023, False)]:
            with self.subTest(year=year):
                self.assertEqual(is_leap_year(year), expected)

    def test_month_lengths(self):
        self.assertEqual(days_in_month(1900, 2), 28)
        self.assertEqual(days_in_month(2000, 2), 29)
        self.assertEqual(days_in_month(1901, 4), 30)
        self.assertEqual(days_in_month(1901, 1), 31)

    def test_euler_result(self):
        self.assertEqual(count_sundays(), 171)

    def test_each_year_against_standard_calendar(self):
        for year in range(1900, 2001):
            with self.subTest(year=year):
                expected = sum(date(year, month, 1).weekday() == 6 for month in range(1, 13))
                self.assertEqual(count_sundays(year, year), expected)


if __name__ == "__main__":
    unittest.main()
