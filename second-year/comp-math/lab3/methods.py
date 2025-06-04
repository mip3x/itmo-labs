def integrate_rectangle(f, a, b, n, variant="left"):
    h = (b - a) / n
    total = 0.0

    if variant == "left":
        for i in range(n):
            x_i = a + i * h
            total += f(x_i)
        return total * h

    elif variant == "right":
        for i in range(1, n + 1):
            x_i = a + i * h
            total += f(x_i)
        return total * h

    elif variant == "mid":
        for i in range(n):
            x_left = a + i * h
            x_mid = x_left + 0.5 * h
            total += f(x_mid)
        return total * h

    else:
        raise ValueError("Ошибка: неизвестный вариант метода прямоугольников!")


def integrate_trapezoid(f, a, b, n):
    h = (b - a) / n
    total = 0.5 * (f(a) + f(b))
    for i in range(1, n):
        x_i = a + i * h
        total += f(x_i)
    return total * h


def integrate_simpson(f, a, b, n):
    if n % 2 != 0:
        raise ValueError("Ошибка: для метода Симпсона n должно быть четным!")
    h = (b - a) / n
    total = f(a) + f(b)

    # нечётные узлы
    for i in range(1, n, 2):
        x_i = a + i * h
        total += 4 * f(x_i)

    # чётные узлы
    for i in range(2, n, 2):
        x_i = a + i * h
        total += 2 * f(x_i)

    return total * h / 3


METHODS = {
    1: ("Метод левых прямоугольников", "rect_left", 2),
    2: ("Метод правых прямоугольников", "rect_right", 2),
    3: ("Метод средних прямоугольников", "rect_mid", 2),
    4: ("Метод трапеций", "trapezoid", 2),
    5: ("Метод Симпсона", "simpson", 4),
}
