import numpy as np
import matplotlib.pyplot as plt

def system1(xy):
    x, y = xy
    return np.array([x**2 + y**2 - 1, x**2 - y - 0.5])

def system2(xy):
    x, y = xy
    return np.array([x**2 + y**2 - 4, 3*x**2 - y])

def jacobian(f, x, dx=1e-6):
    x = np.asarray(x, dtype=float)
    n = x.size
    J = np.zeros((n, n))
    
    for j in range(n):
        perturb = np.zeros(n)
        perturb[j] = dx
        f_forward = f(x + perturb)
        f_backward = f(x - perturb)
        J[:, j] = (f_forward - f_backward) / (2 * dx)
    
    return J


def plot_system(system):
    x = np.linspace(-2, 2, 400)
    y = np.linspace(-2, 2, 400)
    X, Y = np.meshgrid(x, y)

    Z1 = np.array([system([x_, y_])[0] for x_, y_ in zip(np.ravel(X), np.ravel(Y))]).reshape(X.shape)
    Z2 = np.array([system([x_, y_])[1] for x_, y_ in zip(np.ravel(X), np.ravel(Y))]).reshape(X.shape)

    plt.contour(X, Y, Z1, levels=[0], colors='r')
    plt.contour(X, Y, Z2, levels=[0], colors='b')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.show()

def newton_method(system, jacobian, x0, epsilon, max_iterations=1000):
    x = np.array(x0, dtype=float)

    for iteration in range(max_iterations):
        f_val = system(x)
        J = jacobian(system, x)
        try:
            delta = np.linalg.solve(J, f_val)
        except np.linalg.LinAlgError:
            print(f"Якобиан вырожден в точке {x} на итерации {iteration}")
            return None, iteration
        x_next = x - delta
        print(f"{iteration}. x = {x}, delta = {delta}, f = {f_val}, norm(delta) = {np.linalg.norm(delta)}")
        if np.linalg.norm(delta) < epsilon:
            return x_next, iteration
        x = x_next
    print("Метод Ньютона не сошелся за заданное количество итераций")
    return x, max_iterations

def choose_system_of_equations(functions):
    print("Выберите систему уравнений:")
    for key, value in functions.items():
        print(f"{key}: {value[1]}")
    try:
        equations_number = int(input("Введите номер системы: "))
    except ValueError:
        print("(!) Вы ввели не число")
        return choose_system_of_equations(functions)
    if equations_number < 1 or equations_number > len(functions):
        print("(!) Такого номера нет.")
        return choose_system_of_equations(functions)
    return equations_number

def run():
    systems = {
        1: [system1, "x^2 + y^2 - 1, x^2 - y - 0.5"],
        2: [system2, "x^2 + y^2 - 4, 3x^2 - y"]
    }
    
    eq_num = choose_system_of_equations(systems)
    chosen_system = systems[eq_num][0]
    
    plot_system(chosen_system)
    try:
        x0, y0 = map(float, input("Введите начальные приближения (x0 y0): ").split())
    except ValueError:
        print("Ошибка ввода! Введите два числа, разделённых пробелом")
        return
    try:
        epsilon = float(input("Введите погрешность вычисления: "))
    except ValueError:
        print("Ошибка ввода погрешности!")
        return
        
    solution, iterations = newton_method(chosen_system, jacobian, (x0, y0), epsilon)
    
    if solution is not None:
        f_val = chosen_system(solution)
        print(f"\nНайденное решение: x = {solution[0]:.5f}, y = {solution[1]:.5f}")
        print(f"Количество итераций: {iterations}")
    else:
        print("Решение не найдено.")
