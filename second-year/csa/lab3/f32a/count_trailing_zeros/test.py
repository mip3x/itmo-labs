def count_trailing_zeros(n):
    """Count the number of trailing zeros in the binary representation of an integer.

    Args:
        n (int): The integer to count trailing zeros for.

    Returns:
        int: The number of trailing zeros.
    """
    if n == 0:
        return 32
    count = 0
    while (n & 1) == 0:
        count += 1
        n >>= 1
    return count


assert count_trailing_zeros(1) == 0
assert count_trailing_zeros(2) == 1
assert count_trailing_zeros(16) == 4
