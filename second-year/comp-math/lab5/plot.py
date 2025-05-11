import matplotlib.pyplot as plt

def plot_interpolation(xs, ys, f, interp_func, X):
    grid = [xs[0] + i*(xs[-1]-xs[0])/300 for i in range(301)]
    plt.figure(figsize=(8,5))
    if f:
        plt.plot(grid, [f(x) for x in grid], label="Исходная")
    plt.plot(grid, [interp_func(xs, ys, x) for x in grid], label="Интерполяция")
    plt.scatter(xs, ys, color='red', label="Узлы")
    plt.axvline(X, linestyle='--', label=f"X={X}")
    plt.legend(); plt.grid(True); plt.show()
