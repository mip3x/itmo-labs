from functions import FUNCTIONS
import methods


def runge_check(I_h, I_h2, k):
    return abs(I_h2 - I_h) / (2 ** k - 1)


def compute_with_precision(f, a, b, eps, method, k):
    n = 4

    if method == "rect_left":
        I_h = methods.integrate_rectangle(f, a, b, n, variant="left")
    elif method == "rect_right":
        I_h = methods.integrate_rectangle(f, a, b, n, variant="right")
    elif method == "rect_mid":
        I_h = methods.integrate_rectangle(f, a, b, n, variant="mid")
    elif method == "trapezoid":
        I_h = methods.integrate_trapezoid(f, a, b, n)
    elif method == "simpson":
        I_h = methods.integrate_simpson(f, a, b, n)
    else:
        raise ValueError("Ошибка: неизвестный метод интегрирования!")

    while True:
        n2 = n * 2
        if method == "rect_left":
            I_h2 = methods.integrate_rectangle(f, a, b, n2, variant="left")
        elif method == "rect_right":
            I_h2 = methods.integrate_rectangle(f, a, b, n2, variant="right")
        elif method == "rect_mid":
            I_h2 = methods.integrate_rectangle(f, a, b, n2, variant="mid")
        elif method == "trapezoid":
            I_h2 = methods.integrate_trapezoid(f, a, b, n2)
        elif method == "simpson":
            I_h2 = methods.integrate_simpson(f, a, b, n2)

        R = runge_check(I_h, I_h2, k)
        if R < eps:
            return I_h, n

        n = n2
        I_h = I_h2


def print_menu_functions():
    print("Доступные функции:")
    for idx, (name, _) in FUNCTIONS.items():
        print(f"  {idx}. {name}")
    print()


def print_menu_methods():
    print("Доступные методы интегрирования:")
    for idx, (name, _, _) in methods.METHODS.items():
        print(f"  {idx}. {name}")


def main():
    print("\n=== Лабораторная работа №3. Численное интегрирование ===\n")

    print("--- Выбор функции ---")

    print_menu_functions()
    while True:
        try:
            choice_f = int(input(f"Введите номер функции (1-{len(FUNCTIONS)}): "))
            if choice_f not in FUNCTIONS:
                raise ValueError
            break
        except ValueError:
            print(f"Ошибка: введите число от 1 до {len(FUNCTIONS)}!")
    f_name, f = FUNCTIONS[choice_f]
    print(f"\nВы выбрали функцию: {f_name}\n")

    print("--- Ввод границ интегрирования ---")

    while True:
        try:
            a = float(input("Введите левую границу интегрирования a: "))
            b = float(input("Введите правую границу интегрирования b: "))
            if b <= a:
                raise ValueError
            break
        except ValueError:
            print("Ошибка: введите два числа такие, чтобы b > a!\n")

    while True:
        try:
            eps = float(input("Введите желаемую точность (eps > 0): "))
            if eps <= 0:
                raise ValueError
            break
        except ValueError:
            print("Ошибка: точность должна быть положительным числом!\n")

    print()
    print("--- Выбор метода интегрирования ---")
    print_menu_methods()
    while True:
        try:
            choice_m = int(input("Введите номер метода (1–5): "))
            if choice_m not in range(1, len(methods.METHODS) + 1):
                raise ValueError
            break
        except ValueError:
            print(f"Ошибка: введите число от 1 до {len(methods.METHODS)}!\n")

    method_name, method, k = methods.METHODS[choice_m]
    print(f"\nВы выбрали {method_name}\n")

    print("---Выполнение вычислений...---\n")
    I_approx, n_used = compute_with_precision(f, a, b, eps, method, k)

    print(f"Результат интегрирования функции {f_name} на [{a}, {b}]:")
    print(f"  Приближенное значение: {I_approx:.12f}")
    print(f"  Использовано разбиений n = {n_used}")
    print(f"  Достигнутая точность: {eps}")
    print("\n=== Конец программы ===\n")


if __name__ == "__main__":
    main()

