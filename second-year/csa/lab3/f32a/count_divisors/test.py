def count_divisors(n):
    """Count the number of divisors of a natural number"""
    if n < 1:
        return -1
    count = 0
    for i in range(1, n + 1):
        if n % i == 0:
            count += 1
    return count


assert count_divisors(2) == 2
assert count_divisors(4) == 3
assert count_divisors(6) == 4
assert count_divisors(10) == 4
