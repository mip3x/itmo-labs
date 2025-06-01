def sum_n(n):
    """Calculate the sum of numbers from 1 to n"""
    if n <= 0:
        return -1
    total = 0
    for i in range(1, n + 1):
        total += i
    return total


assert sum_n(5) == 15
assert sum_n(10) == 55
