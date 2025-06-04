import numpy as np

from data_input import get_data
import diff_tables
import methods
from plot import plot_interpolation

# def get_method():
#     print("Методы:\n 1) Лагранж\n 2) Ньютон (разделённые разности)\n 3) Ньютон (конечные разности)\n")
#     while True:
#         try:
#             method = int(input("Выбор метода: "))
#             if method != 1 and method != 2 and method != 3:
#                 raise ValueError
#             break
#         except Exception:
#             print("Ошибка: введите целочисленное число от 1 до 3!")
#     return method


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

    h0 = xs[1] - xs[0]
    uniform = all(abs((xs[i + 1] - xs[i]) - h0) < 1e-8 for i in range(len(xs) - 1))

    if uniform:
        print("\nТаблица конечных разностей:")
        fd = diff_tables.finite_diff_table(ys)
        for i, lvl in enumerate(fd):
            print(f" delta {i}:", [round(v, 6) for v in lvl])


    print("\nТаблица разделённых разностей:")
    dd = diff_tables.divided_diff_table(xs, ys)
    for row in dd:
        print([round(v,6) for v in row])

    X = get_interpolation_point()
    # method = get_method()

    # print(f"uniform: {uniform}")

    results = []
    interpolation_funcs = {
        "Лагранж": methods.lagrange,
        "Ньютон (разделённые разности)": methods.newton_divided,
    }

    val_lagrange = methods.lagrange(xs, ys, X)
    results.append(("Лагранж", val_lagrange))

    val_newton_div = methods.newton_divided(xs, ys, X)
    results.append(("Ньютон (разделённые разности)", val_newton_div))

    if not uniform:
        print("Сетка неравномерна – методы конечных разностей не применимы")
    else:
        if X - xs[0] < xs[-1] - X:
            val = methods.newton_forward(xs, ys, X)
            name = "Ньютон вперёд"
            method_fn = methods.newton_forward
        else:
            val = methods.newton_backward(xs, ys, X)
            name = "Ньютон назад"
            method_fn = methods.newton_backward

        results.append((name, val))
        interpolation_funcs[name] = method_fn

    print("\nСравнение методов:")
    for name, val in results:
        line = f"{name:<30} в X={X:<8.4f}: {val:<12.6f}"
        if f:
            true = f(X)
            line += f" | Истинное: {true:<10.6f} | Ошибка: {abs(true - val):.2e}"
        print(line)

    plot_interpolation(
        xs, ys, f,
        interpolation_funcs= {
            name: (lambda xs, ys, x, m=method_fn: m(xs, ys, x))
            for name, method_fn in interpolation_funcs.items()
        },
        X=X
    )


if __name__ == "__main__":
    main()
