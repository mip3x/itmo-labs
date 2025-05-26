from problem import CauchyProblem


def calculate_runge_kutta(problem: CauchyProblem):
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


def solve_runge_kutta(problem: CauchyProblem):
    f, x0, y0, x_end, eps = (
        problem.f,
        problem.x0,
        problem.y0,
        problem.x_end,
        problem.eps,
    )
    p = 4
    h = problem.h

    max_step_splitting = 20
    cur_step_splitting = 0

    while True:
        if cur_step_splitting > max_step_splitting or h < 1e-12:
            print("\nШаг слишком мал или решение расходится")
            print(f"Последний h={h:.20f}")
            exit(1)

        try:
            # step = h 
            prob_h = CauchyProblem(f, x0, y0, x_end, h, eps)
            xs_h, ys_h = calculate_runge_kutta(prob_h)
            y_end_h = ys_h[-1]
            if abs(y_end_h) > 1e12:
                raise OverflowError

            # step = h/2
            prob_h2 = CauchyProblem(f, x0, y0, x_end, h/2, eps)
            _, ys_h2 = calculate_runge_kutta(prob_h2)
            y_end_h2 = ys_h2[-1]
            if abs(y_end_h2) > 1e12:
                raise OverflowError

        except OverflowError:
            print(f"Переполнение при h = {h:.20f}, уменьшаем шаг --> {h/2:.20f}")
            cur_step_splitting += 1
            h /= 2
            continue

        R = abs(y_end_h2 - y_end_h) / (2 ** p - 1)

        print("Оценка погрешности RK4 на конце интервала:")
        print(f"  h = {h:.6f} ; y_end^h = {y_end_h:.6f}")
        print(f"  h/2 = {h/2:.6f} ; y_end^(h/2) = {y_end_h2:.6f}")
        print(f"  R = |y_end^(h/2) - y_end^h|/(2^{p}-1) = {R:.20f}")

        if R <= eps:
            print(f"Точность достигнута. Последний шаг: {h:.20f}.\nУзловые точки:")
            problem.last_h = h
            return xs_h, ys_h

        cur_step_splitting += 1
        print(f"Точность НЕ достигнута (R > ε={eps}), уменьшаем шаг: {h:.20f} --> {h/2:.20f}\n")
        h /= 2
