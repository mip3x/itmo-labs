import math

from problem import CauchyProblem
from euler_method import solve_euler
from runge_kutta_method import solve_runge_kutta
from milne_method import solve_milne
from plot_solution import plot_solution

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
        1: (
            "y' = x + y",
            lambda x, y: x + y,
            lambda x0, y0: (lambda x: -(x + 1) + (y0 + x0 + 1) * math.exp(x - x0)),
        ),
        2: (
            "y' = y - x**2 + 1",
            lambda x, y: y - x ** 2 + 1,
            lambda x0, y0: (lambda x: (x + 1)**2 + (y0 - (x0 + 1)**2)*math.exp(x - x0)),
        ),
        3: (
            "y' = x * y",
            lambda x, y: x * y,
            lambda x0, y0: (lambda x: y0 * math.exp((x**2 - x0**2)/2)),
        ),
        4: (
            "y' = y + (1 + x) * y ** 2",
            lambda x, y: y + (1 + x) * y ** 2,
            lambda x0, y0: (lambda x: (-1 / x))
        ),
        5: (
            "y' = x ** 2 - 2 * y",
            lambda x, y: x ** 2 - 2 * y,
            lambda x0, y0: (lambda x: (x ** 2/2 - x/2 + 1/4) +
                (y0 - (x0 ** 2/2 - x0/2 + 1/4)) * math.exp(2*(x0 - x))
            )
        ),
    }


def select_ode():
    problems = get_problems()

    print("Выберите ОДУ:")
    for k, (desc, _, _) in problems.items():
        print(f"  {k}: {desc}")

    choice = int(input("Номер: "))
    desc, f, exact_factory = problems[choice]

    return f, exact_factory


def run_methods(problem: CauchyProblem):
    methods = [
        ('Милна', solve_milne),
        ('Эйлера', solve_euler),
        ('Рунге–Кутта 4го порядка', solve_runge_kutta),
    ]
    x0 = problem.x0
    h = problem.h
    eps = problem.eps
    exact = problem.exact

    for name, solver in methods:
        print('\n' + '='*40)
        print(f'РЕЗУЛЬТАТЫ метода {name} (ε = {eps})')
        print('-'*40)
        xs, ys = solver(problem)
        
        if exact is not None:
            print('i\tx\t\ty\t\ty_точн\t\t|y_точн - y|')
        else:
            print('i\tx\ty')

        x_prev = x0
        for i, (x, y) in enumerate(zip(xs, ys)):
            y = ys[i]

            if abs(x_prev - x) < 1e-8:
                if exact is not None:
                    y_ex = exact(x)
                    difference = abs(y_ex - y)
                    print(f'{i}\t{x:.6f}\t{y:.6f}\t{y_ex:.6f}\t{difference:.12f}')
                else:
                    print(f'{i}\t{x:.6f}\t{y:.6f}')

                x_prev += h

        if name == 'Милна' and exact is not None:
            max_err = 0.0
            for x, y in zip(xs, ys):
                err = abs(exact(x) - y)
                if err > max_err:
                    max_err = err

            print('-'*40)
            print(f'Фактическая ошибка max|y_exact−y_approx| = {max_err:.12f}')
            print(f'Допуск ε = {problem.eps:.12f}')
            print(f'Последний шаг h = {problem.last_h:.12f}')

            if max_err <= problem.eps:
                print('Погрешность в пределах допустимого (max_err <= ε)')
            else:
                print('Погрешность превышает допустимый допуск (max_err > ε)')
                
        print('='*40)


        plot_solution(xs, ys, exact, name)
        input("\nГрафики открыты — нажмите Enter, чтобы перейти к следующему методу...")


def main():
    f, exact_factory = select_ode()

    x0 = input_float('Введите начальное значение x0: ')
    y0 = input_float('Введите начальное значение y0: ')
    x_end = input_float(
        'Введите конечное значение x: ',
        valid_func=lambda x: x > x0,
        error_msg='Конечное x должно быть больше начального x0!'
    )
    h = input_float(
        'Введите шаг h: ',
        valid_func=lambda h: h > 0 and h <= (x_end - x0),
        error_msg='Шаг должен быть положительным и не превышать интервал [x0, x]!'
    )
    eps = input_float(
        'Введите точность ε: ',
        valid_func=lambda e: e > 0,
        error_msg='Точность должна быть положительным числом!'
    )

    if exact_factory:
        exact = exact_factory(x0, y0)
    else:
        exact = None

    problem = CauchyProblem(f, x0, y0, x_end, h, eps, exact)
    run_methods(problem)


if __name__ == '__main__':
    main()
