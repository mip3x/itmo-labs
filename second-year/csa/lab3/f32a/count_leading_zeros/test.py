def count_leading_zeros(n):
    """Count the number of leading zeros in the binary representation of an integer.

    Args:
        n (int): The integer to count leading zeros for.

    Returns:
        int: The number of leading zeros.
    """
    if n == 0:
        return 32
    count = 0
    for i in range(31, -1, -1):
        if (n >> i) & 1 == 0:
            count += 1
        else:
            break
    return count


assert count_leading_zeros(1) == 31
assert count_leading_zeros(2) == 30
assert count_leading_zeros(16) == 27
