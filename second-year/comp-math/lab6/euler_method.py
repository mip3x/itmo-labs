from problem import CauchyProblem


def calculate_euler(problem: CauchyProblem):
    x = problem.x0
    y = problem.y0
    
    xs = [x]
    ys = [y]

    for _ in range(problem.n_steps):
        # y(x_{i + 1}) = y(x_{i}) + h * f(x_i, y_i)
        y = y + problem.h * problem.f(x, y)
        x += problem.h
        xs.append(x)
        ys.append(y)

    return xs, ys


def solve_euler(problem: CauchyProblem):
    f, x0, y0, x_end, eps = (
        problem.f,
        problem.x0,
        problem.y0,
        problem.x_end,
        problem.eps,
    )

    h = problem.h
    p = 1

    max_step_splitting = 20
    cur_step_splitting = 0
    max_abs_y = 1e12

    while True:
        if cur_step_splitting > max_step_splitting or h < 1e-12:
            print("\nШаг слишком мал или решение расходится")
            print(f"Последний шаг h = {h:.20f}")
            exit(1)

        try:
            # step = h
            prob_h = CauchyProblem(f, x0, y0, x_end, h, eps)
            xs_h, ys_h = calculate_euler(prob_h)
            y_end_h = ys_h[-1]
            if abs(y_end_h) > max_abs_y:
                    raise OverflowError

            # step = h/2
            prob_h2 = CauchyProblem(f, x0, y0, x_end, h/2, eps)
            _, ys_h2 = calculate_euler(prob_h2)
            y_end_h2 = ys_h2[-1]
            if abs(y_end_h2) > max_abs_y:
                    raise OverflowError

        except OverflowError:
            print(f"Переполнение при h = {h:.20f}, уменьшаем шаг --> {h/2:.20f}")
            cur_step_splitting += 1
            h /= 2
            continue

        R = abs(y_end_h - y_end_h2) / (2 ** p - 1)

        print("Оценка погрешности Эйлера на конце интервала:")
        print(f"  h = {h:.6f} ; y_end^h = {y_end_h:.6f}")
        print(f"  h/2 = {h/2:.6f} ; y_end^(h/2) = {y_end_h2:.6f}")
        print(f"  R = |y_end^(h/2) - y_end^h|/(2^{p}-1) = {R:.20f}")

        if R <= eps:
            print("Точность достигнута:")
            return xs_h, ys_h

        cur_step_splitting += 1
        print(f"Точность НЕ достигнута (R > ε={eps}), уменьшаем шаг: {h} --> {h/2}\n")
        h /= 2
