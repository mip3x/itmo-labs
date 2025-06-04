import matplotlib.pyplot as plt

def plot_interpolation(xs, ys, f, interpolation_funcs, X):
    grid = [xs[0] + i * (xs[-1] - xs[0]) / 300 for i in range(301)]

    for name, func in interpolation_funcs.items():
        plt.figure(figsize=(10, 6))

        if f:
            plt.plot(grid, [f(x) for x in grid], label="Исходная функция", linewidth=2)

        y_interp = [func(xs, ys, x) for x in grid]
        plt.plot(grid, y_interp, label=f"Интерполяция: {name}")

        plt.scatter(xs, ys, color='red', label="Узлы", zorder=5)
        plt.axvline(X, linestyle='--', color='gray', label=f"X = {X}")

        yX = func(xs, ys, X)
        plt.scatter([X], [yX], color='green', s=80, marker='x', label=f"f_interp({X}) = {yX:.4f}", zorder=6)

        plt.title(f"Метод: {name}")
        plt.xlabel("x")
        plt.ylabel("y")
        plt.legend()
        plt.grid(True)
        plt.tight_layout()
        plt.show(block=False)

    input("Нажмите Enter, чтобы закрыть все графики")
    plt.close('all')
