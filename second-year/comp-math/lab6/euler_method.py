from problem import CauchyProblem

def solve_euler(problem):
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
