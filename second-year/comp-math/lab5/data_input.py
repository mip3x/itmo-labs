import math

def get_points_number():
    while True:
        try:
            n = int(input("Задайте число точек (n >= 2): "))
            if n < 2:
                raise ValueError
            break
        except Exception:
            print("Ошибка: введите целое число n >= 2!")

    return n

def get_data():
    print("Способы задания данных:")
    print(" 1) Ввод с клавиатуры")
    print(" 2) Чтение из файла")
    print(" 3) По заданной функции")

    while True:
        try:
            mode = int(input("Выберите режим (1/2/3): "))
            if mode != 1 and mode != 2 and mode != 3:
                raise ValueError
            break
        except Exception:
            print("Ошибка: введите целое число от 1 до 3!")

    if mode == 1:
        n = get_points_number()
        print("Задайте координаты точек.\nПример ввода: Точка n (x, y): --> 3 4")

        xs, ys = [], []
        for i in range(n):
            while True:
                try:
                    x, y = map(float, input(f"Точка {i + 1} (x, y): ").split())
                    if i > 0 and (xs[-1] == x):
                        raise ValueError

                    xs.append(x)
                    ys.append(y)
                    break
                except Exception:
                    print("Неверный формат: введите два числа через пробел. Значения x не должны повторяться!\nПример ввода: Точка n (x, y): --> 3 4")

        return xs, ys, None

    if mode == 3:
        funcs = {1: math.sin, 2: math.cos, 3: math.exp}
        print(" 1) sin(x)\n 2) cos(x)\n 3) exp(x)")

        while True:
            try:
                c = int(input("Выберите функцию: "))
                if c < 1 or c > len(funcs):
                    raise ValueError
                break
            except Exception:
                print(f"Ошибка: введите целое число от 1 до {len(funcs)}!")

        f = funcs[c]
        a = float(input("Начало отрезка a: "))
        b = float(input("Начало отрезка b: "))

        n = get_points_number()
        xs = [a + i * (b - a)/(n - 1) for i in range(n)]
        ys = [f(x) for x in xs]

        return xs, ys, f
