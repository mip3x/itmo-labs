import math

def f1(x):
    return x * x

def f2(x):
    return math.sin(x)

def f3(x):
    return math.exp(-x*x)

def f4(x):
    return math.log(1 + x*x)

def f5(x):
    return 1.0 / (1.0 + x*x)

def f6(x):
    return (x ** 3 - (3 * x ** 2) + 7 * x - 10)

FUNCTIONS = {
    1: ("x^2", f1),
    2: ("sin(x)", f2),
    3: ("exp(-x^2)", f3),
    4: ("ln(1 + x^2)", f4),
    5: ("1 / (1 + x^2)", f5),
    6: ("x^3 - 3x^2 + 7x - 10", f6)
}
