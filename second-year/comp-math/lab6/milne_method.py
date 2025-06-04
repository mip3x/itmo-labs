from problem import CauchyProblem
from runge_kutta_method import solve_runge_kutta


def solve_milne(problem: CauchyProblem):
    h = problem.h
    f = problem.f 
    x0 = problem.x0
    n_steps = problem.n_steps
    eps = problem.eps

    xs_init, ys_init = solve_runge_kutta(problem)
    xs = xs_init[:4]
    ys = ys_init[:4]

    for i in range(4, n_steps + 1):
        xi = x0 + i * h

        # f_{i - 3}, f_{i - 2}, f_{i - 1}
        f_im3 = f(xs[i - 3], ys[i - 3])
        f_im2 = f(xs[i - 2], ys[i - 2])
        f_im1 = f(xs[i - 1], ys[i - 1])

        y_pred = ys[i - 4] + ((4 * h) / 3) * (2 * f_im3 - f_im2 + 2 * f_im1)
        f_pred = f(xi, y_pred)

        y_corr = ys[i - 2] + (h / 3) * (f_im2 + 4 * f_im1 + f_pred)

        max_iters = 5
        for _ in range(max_iters):
            if abs(y_corr - y_pred) <= eps:
                break

            f_corr = f(xi, y_corr)
            y_new = ys[i - 2] + (h / 3) * (f_im2 + 4 * f_im1 + f_corr)

            y_corr = y_new

        xs.append(xi)
        ys.append(y_corr)

    problem.last_h = h
    return xs, ys
