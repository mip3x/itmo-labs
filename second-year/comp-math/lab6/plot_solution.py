import matplotlib.pyplot as plt

def plot_solution(xs, ys, exact, method_name):
    plt.ion()
    plt.figure()
    plt.plot(xs, ys, marker='o', linestyle='-', label=f'{method_name} (приближённое)')
    if exact is not None:
        ys_exact = [exact(x) for x in xs]
        plt.plot(xs, ys_exact, linestyle='--', label='Точное решение')
    plt.title(f'{method_name}: приближённое | точное')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.legend()
    plt.grid(True)
    plt.show(block=False)
