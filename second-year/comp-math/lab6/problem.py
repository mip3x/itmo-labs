class CauchyProblem:
    def __init__(self, f, x0, y0, x_end, h) -> None:
        self.f = f
        self.x0 = x0
        self.y0 = y0
        self.x_end = x_end
        self.h = h
        self.n_steps = int((x_end - x0) / h)
