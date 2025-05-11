import numpy as np

from data_input import get_data
import diff_tables
import methods
from plot import plot_interpolation

def get_method():
    print("Методы:\n 1) Лагранж\n 2) Ньютон (разделённые разности)\n 3) Ньютон (конечные разности)\n")
    while True:
        try:
            method = int(input("Выбор метода: "))
            if method != 1 and method != 2 and method != 3:
                raise ValueError
            break
        except Exception:
            print("Ошибка: введите целочисленное число от 1 до 3!")
    return method

def get_interpolation_point():
    while True:
        try:
            X = float(input("\nЗадайте точку интерполяции X: "))
            break
        except Exception:
            print("Ошибка: введите число")
    return X

def main():
    xs, ys, f = get_data()
    print(xs, ys)
    paired = sorted(zip(xs, ys), key=lambda p: p[0])
    xs, ys = [p[0] for p in paired], [p[1] for p in paired]

    print("\nУзлы интерполяции:")
    for i, (x, y) in enumerate(zip(xs, ys)):
        print(f"  x = {x:.4f}, y = {y:.4f}")

    print("\nТаблица конечных разностей:")
    fd = diff_tables.finite_diff_table(ys)
    for i, lvl in enumerate(fd):
        print(f" delta {i}:", [round(v, 6) for v in lvl])

    print("\nТаблица разделённых разностей:")
    dd = diff_tables.divided_diff_table(xs, ys)
    for row in dd:
        print([round(v,6) for v in row])

    X = get_interpolation_point()
    method = get_method()

    h0 = xs[1] - xs[0]
    uniform = all(abs((xs[i + 1] - xs[i]) - h0) < 1e-8 for i in range(len(xs) - 1))
    # print(f"uniform: {uniform}")

    if method == 1:
        name, val = "Лагранж", methods.lagrange(xs, ys, X)
    elif method == 2:
        name, val = "Ньютон (разделённые разности)", methods.newton_divided(xs, ys, X)
    elif method == 3:
        if not uniform:
            print("Сетка неравномерна – вместо конечных разностей будут использованы разделённые")
            name, val = "Ньютон (разделённые разности)", methods.newton_divided(xs, ys, X)
        else:
            if X - xs[0] < xs[-1] - X:
                name, val = "Ньютон вперёд", methods.newton_forward(xs, ys, X)
            else:
                name, val = "Ньютон назад", methods.newton_backward(xs, ys, X)

    print(f"\n{name} в X={X}: {val:.6f}")
    if f:
        true = f(X)
        print(f"Истинное: {true:.6f}, ошибка: {abs(true-val):.2e}")

    # график
    plot_interpolation(xs, ys, f, 
        lambda xs, ys, x: {
            "Лагранж": methods.lagrange,
            "Ньютон (разделённые разности)": methods.newton_divided,
            "Ньютон вперёд": methods.newton_forward,
            "Ньютон назад": methods.newton_backward,
        }[name](xs, ys, x),
        X)

if __name__ == "__main__":
    main()
