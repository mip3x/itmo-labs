from problem import CauchyProblem


def solve_runge_kutta(problem: CauchyProblem):
    x = problem.x0
    y = problem.y0
    h = problem.h
    f = problem.f

    xs = [x]
    ys = [y]

    for _ in range(problem.n_steps):
        k1 = h * f(x, y)
        k2 = h * f(x + h / 2, y + k1 / 2)
        k3 = h * f(x + h / 2, y + k2 / 2)
        k4 = h * f(x + h, y + k3)
        y = y + ((k1 + 2 * k2 + 2 * k3 + k4) / 6)
        x = x + h
        xs.append(x)
        ys.append(y)

    return xs, ys
