from problem import CauchyProblem
from euler_method import solve_euler

def input_int(prompt, valid_func=lambda x: True, error_msg='Неверный ввод'):
    while True:
        try:
            val = int(input(prompt))
            if not valid_func(val):
                print(error_msg)
                continue
            return val
        except ValueError:
            print('Пожалуйста, введите целое число!')


def input_float(prompt, valid_func=lambda x: True, error_msg='Неверный ввод'):
    while True:
        try:
            val = float(input(prompt))
            if not valid_func(val):
                print(error_msg)
                continue
            return val
        except ValueError:
            print('Пожалуйста, введите число!')


def get_problems():
    return {
        1: ("y' = x + y", lambda x, y: x + y),
        2: ("y' = y - x**2 + 1", lambda x, y: y - x**2 + 1),
        3: ("y' = x * y", lambda x, y: x * y),
        4: ("y' = y + (1 + x) * y ** 2", lambda x, y: y + (1 + x) * y ** 2),
    }


def select_ode():
    problems = get_problems()

    print('Выберите ОДУ для решения:')
    for key, (desc, _) in problems.items():
        print(f'  {key}: {desc}')
    choice = input_int(
        'Номер уравнения: ',
        valid_func=lambda x: x in problems,
        error_msg='Выберите корректный номер из списка!'
    )

    desc, f = problems[choice]
    return f


def main():
    f = select_ode()

    x0 = input_float('Введите начальное значение x0: ')
    y0 = input_float('Введите начальное значение y0: ')
    x_end = input_float(
        'Введите конечное значение x: ',
        valid_func=lambda x: x > x0,
        error_msg='Конечное x должно быть больше начального x0.'
    )
    h = input_float(
        'Введите шаг h: ',
        valid_func=lambda x: x > 0 and x <= (x_end - x0),
        error_msg='Шаг должен быть положительным и не превышать интервал [x0, x].'
    )

    problem = CauchyProblem(f, x0, y0, x_end, h)
    xs, ys = solve_euler(problem)
    n = len(xs)

    print('\nРезультаты метода Эйлера:')
    print('i\tx\t\ty\t\tf(x,y)')
    for i in range(n):
        x = xs[i]
        y = ys[i]
        if i < n - 1:
            fx = f(x, y)
            print(f'{i}\t{x:.6f}\t{y:.6f}\t{fx:.6f}')
        else:
            print(f'{i}\t{x:.6f}\t{y:.6f}')


if __name__ == '__main__':
    main()
