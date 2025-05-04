import matplotlib.pyplot as plt
import math

def plot_all(x, y, preds, best_name):
    x_min, x_max = min(x), max(x)
    xs = [x_min + i*(x_max - x_min)/199 for i in range(200)]

    plt.figure()
    plt.scatter(x, y, label='Исходные точки')
    yp, params = preds[best_name]

    if best_name == 'Линейная':
        a1, a0 = params
        ys = [a1 * xi + a0 for xi in xs]
    elif best_name == 'Квадратичная':
        a0, a1, a2 = params
        ys = [a0 + a1 * xi + a2 * xi ** 2 for xi in xs]
    elif best_name == 'Кубическая':
        a0, a1, a2, a3 = params
        ys = [a0 + a1 * xi + a2 * xi ** 2 + a3 * xi ** 3 for xi in xs]
    elif best_name == 'Экспоненциальная':
        A, B = params
        ys = [A * math.exp(B * xi) for xi in xs]
    elif best_name == 'Логарифмическая':
        A, B = params
        ys = [A + B * math.log(xi) for xi in xs]
    elif best_name == 'Степенная':
        A, B = params
        ys = [A * xi ** B for xi in xs]

    plt.plot(xs, ys, label=best_name, linewidth=2)
    plt.title(f"Лучшая модель: {best_name}")
    plt.legend(); plt.grid(True)
    plt.xlabel('x'); plt.ylabel('y')

    plt.figure()
    plt.scatter(x, y, label='Исходные точки')
    for name, (yp, params) in preds.items():
        if name == best_name:
            continue
        if name == 'Линейная':
            a1, a0 = params
            ys = [a1 * xi + a0 for xi in xs]
        elif name == 'Квадратичная':
            a0, a1, a2 = params
            ys = [a0 + a1 * xi + a2 * xi ** 2 for xi in xs]
        elif name == 'Кубическая':
            a0, a1, a2, a3 = params
            ys = [a0 + a1 * xi + a2 * xi ** 2 + a3 * xi ** 3 for xi in xs]
        elif name == 'Экспоненциальная':
            A, B = params
            ys = [A * math.exp(B * xi) for xi in xs]
        elif name == 'Логарифмическая':
            A, B = params
            ys = [A + B * math.log(xi) for xi in xs]
        elif name == 'Степенная':
            A, B = params
            ys = [A * xi ** B for xi in xs]
        
        plt.plot(xs, ys, label=name)

    plt.title("Остальные модели")
    plt.legend(); plt.grid(True)
    plt.xlabel('x'); plt.ylabel('y')
    plt.show()
