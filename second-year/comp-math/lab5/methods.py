import diff_tables
import math


def lagrange(xs, ys, X):
    total = 0.0
    n = len(xs)
    for i in range(n):
        term = ys[i]
        for j in range(n):
            if i != j:
                term *= (X - xs[j])/(xs[i] - xs[j])
        total += term
    return total


def newton_forward(xs, ys, X):
    h = xs[1] - xs[0]
    t = (X - xs[0]) / h
    diffs = diff_tables.finite_diff_table(ys)
    res = ys[0]
    prod = 1.0
    for n in range(1, len(ys)):
        prod *= (t - (n - 1))
        res += diffs[n][0] * prod / math.factorial(n)
    return res


def newton_backward(xs, ys, X):
    h = xs[1] - xs[0]
    t = (X - xs[-1]) / h
    diffs = diff_tables.finite_diff_table(ys)
    res = ys[-1]
    prod = 1.0
    n = len(ys)
    for k in range(1, n):
        prod *= (t + (k - 1))
        res += diffs[k][-1] * prod / math.factorial(k)
    return res


def newton_divided(xs, ys, X):
    dd = diff_tables.divided_diff_table(xs, ys)
    n = len(xs)
    res = dd[0][0]
    prod = 1.0
    for j in range(1, n):
        prod *= (X - xs[j - 1])
        res += dd[0][j] * prod
    return res
