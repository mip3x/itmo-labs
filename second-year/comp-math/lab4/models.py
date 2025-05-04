import math

# метод Гаусса для решения системы A·u = B
def gaussian_solve(A, B):
    n = len(B)
    M = [row[:] for row in A]
    b = B[:]

    for k in range(n):
        max_row = max(range(k, n), key=lambda i: abs(M[i][k]))
        M[k], M[max_row] = M[max_row], M[k]
        b[k], b[max_row] = b[max_row], b[k]
        pivot = M[k][k]
        for j in range(k, n):
            M[k][j] /= pivot
        b[k] /= pivot
        for i in range(k + 1, n):
            factor = M[i][k]
            for j in range(k, n):
                M[i][j] -= factor * M[k][j]
            b[i] -= factor * b[k]
    x = [0.0] * n
    for i in range(n-1, -1, -1):
        x[i] = b[i] - sum(M[i][j] * x[j] for j in range(i + 1, n))
    return x

# линейная: y = a*x + b
def fit_linear(x, y):
    n = len(x)
    SX = sum(x)
    SY = sum(y)
    SXX = sum(xi * xi for xi in x)
    SXY = sum(xi * yi for xi, yi in zip(x, y))

    # метод Крамера
    D = SXX * n - SX * SX
    D1 = SXY * n - SX * SY
    D2 = SXX * SY - SX * SXY
    a = D1 / D
    b = D2 / D

    y_pred = [a * xi + b for xi in x]
    return y_pred, (a, b)

# квадратичная: y = a0 + a1*x + a2*x^2
def fit_quad(x, y):
    n = len(x)
    SX = sum(x)
    SXX = sum(xi ** 2 for xi in x)
    SXXX = sum(xi ** 3 for xi in x)
    SXXXX = sum(xi ** 4 for xi in x)
    SY = sum(y)
    SXY = sum(xi * yi for xi, yi in zip(x, y))
    SX2Y = sum((xi ** 2) * yi for xi, yi in zip(x, y))

    A = [
        [n,   SX,  SXX],
        [SX,  SXX, SXXX],
        [SXX, SXXX, SXXXX],
    ]
    B = [SY, SXY, SX2Y]

    a0, a1, a2 = gaussian_solve(A, B)
    y_pred = [a0 + a1 * xi + a2 * xi ** 2 for xi in x]

    return y_pred, (a0, a1, a2)

# кубическая: y = a0 + a1*x + a2*x^2 + a3*x^3
def fit_cubic(x, y):
    n = len(x)
    SX = sum(x)
    SY = sum(y)
    SXX = sum(xi ** 2 for xi in x)
    SXXX = sum(xi ** 3 for xi in x)
    SXXXX = sum(xi ** 4 for xi in x)
    SX5 = sum(xi ** 5 for xi in x)
    SX6 = sum(xi ** 6 for xi in x)

    SXY = sum(xi * yi for xi, yi in zip(x, y))
    SX2Y = sum((xi ** 2) * yi for xi, yi in zip(x, y))
    SX3Y = sum((xi ** 3) * yi for xi, yi in zip(x, y))

    A = [
        [n,   SX,   SXX,  SXXX],
        [SX,  SXX,  SXXX, SXXXX],
        [SXX, SXXX, SXXXX, SX5],
        [SXXX,SXXXX, SX5,   SX6],
    ]
    B = [SY, SXY, SX2Y, SX3Y]
    a0, a1, a2, a3 = gaussian_solve(A, B)
    y_pred = [a0 + a1 * xi + a2 * xi ** 2 + a3 * xi ** 3 for xi in x]
    return y_pred, (a0, a1, a2, a3)

# экспоненциальная: y = A * e^(Bx)
# lny = ln(a) + bx
def fit_exp(x, y):
    valid = [(xi, yi) for xi, yi in zip(x, y) if yi > 0]
    if len(valid) < 2:
        raise ValueError("Недостаточно положительных y для экспоненты")
    xs, ys = zip(*valid)

    ln_y = [math.log(yi) for yi in ys]
    n = len(xs)
    Sx = sum(xs)
    Sln = sum(ln_y)
    Sxx = sum(xi * xi for xi in xs)
    Sxln = sum(xi * ly for xi, ly in zip(xs, ln_y))

    B = (n * Sxln - Sx * Sln) / (n * Sxx - Sx ** 2)
    lnA = (Sln - B * Sx) / n
    A = math.exp(lnA)
    y_pred = [A * math.exp(B * xi) for xi in x]

    return y_pred, (A, B)

# логарифмическая: y = B * ln(x) + A
def fit_log(x, y):
    if any(xi <= 0 for xi in x):
        raise ValueError("все x должны быть => 0")

    ln_x = [math.log(xi) for xi in x]
    n = len(x)
    Sln = sum(ln_x)
    Sy = sum(y)
    Slnln = sum(xi * xi for xi in ln_x)
    Slny = sum(xi * yi for xi, yi in zip(ln_x, y))

    B = (n * Slny - Sln * Sy) / (n * Slnln - Sln ** 2)
    A = (Sy - B * Sln) / n
    y_pred = [A + B * math.log(xi) for xi in x]

    return y_pred, (A, B)

# степенная: y = A * x^B
def fit_pow(x, y):
    if any(xi <= 0 for xi in x) or any(yi <= 0 for yi in y):
        raise ValueError("все x и y должны быть => 0")

    ln_x = [math.log(xi) for xi in x]
    ln_y = [math.log(yi) for yi in y]
    n = len(x)
    Slnx = sum(ln_x)
    Slny = sum(ln_y)
    Slnxlnx = sum(xi * xi for xi in ln_x)
    Slnxlny = sum(xi * yi for xi, yi in zip(ln_x, ln_y))

    B = (n * Slnxlny - Slnx * Slny) / (n * Slnxlnx - Slnx ** 2)
    lnA = (Slny - B * Slnx) / n
    A = math.exp(lnA)
    y_pred = [A * (xi ** B) for xi in x]

    return y_pred, (A, B)
