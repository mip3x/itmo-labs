from io_utils import read_input
from models import (fit_linear, fit_quad, fit_cubic, fit_exp, fit_log, fit_pow)
from stats_utils import calc_rms, calc_r2, calc_pearson
from plot_utils import plot_all

def main():
    x, y = read_input()

    print('Функция y = 18x / (x ** 4 + 10)')

    funcs = [
        ('Линейная', fit_linear),
        ('Квадратичная', fit_quad),
        ('Кубическая', fit_cubic),
        ('Экспоненциальная', fit_exp),
        ('Логарифмическая', fit_log),
        ('Степенная', fit_pow)
    ]

    preds, metrics = {}, {}
    for name, fn in funcs:
        try:
            approx_func_vals, coeffs = fn(x, y)
        except ValueError as e:
            print(f"Пропуск {name[:-2]}ой функции: {e}")
            continue

        rms = calc_rms(y, approx_func_vals)
        r2 = calc_r2(y, approx_func_vals) # коэфф. детерминации
        entry = {'rms': rms, 'r2': r2}

        if name == 'Линейная':
            entry['pearson'] = calc_pearson(x,y)

        metrics[name] = entry;
        preds[name] = (approx_func_vals, coeffs)

    print("\nРезультаты:\n")

    minimum = ('DEFAULT', 10)
    for name, m in metrics.items():
        coeffs = preds[name][1]
        coeff_str = ", ".join(f"{c:.3f}" for c in coeffs)
        line = f"{name:17}| Коэфф.: {coeff_str:27} | СКО = {m['rms']:.5f} | R2 = {m['r2']:.5f}"

        if (m['rms'] < minimum[1]):
            minimum = (name, m['rms'])

        if 'pearson' in m:
            line += f" | pearson = {m['pearson']:.5f}"
        print(line)

    print()
    print('Наиболее подходящая функция:', minimum[0])
    name = minimum[0]

    p = preds[name][1]
    if name =='Линейная':
        a1, a0 = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {a1} * x + {a0}"
        print(msg)
    elif name == 'Квадратичная':
        a0, a1, a2 = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {a0} + {a1} * x + {a2} * x^2"
        print(msg)
    elif name == 'Кубическая':
        a0, a1, a2, a3 = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {a0} + {a1} * x + {a2} * x^2 + {a3} * x^3"
        print(msg)
    elif name == 'Экспоненциальная':
        A, B = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {A} * e^({B} * x)"
        print(msg)
    elif name == 'Логарифмическая':
        A, B = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {A} + {B} * ln(x)"
        print(msg)
    elif name == 'Степенная':
        A, B = p
        msg = f"Уравнение аппроксимирующей функции: phi(x) = {A} * x^({B})"
        print(msg)

    for fname, fn in funcs:
        if name == fname:
            approx_func_vals, coeffs = fn(x, y)
            for i in range(len(approx_func_vals)):
                print(x[i], y[i], approx_func_vals[i], abs(approx_func_vals[i] - y[i]))

    plot_all(x, y, preds, name)

if __name__=='__main__':
    main()
