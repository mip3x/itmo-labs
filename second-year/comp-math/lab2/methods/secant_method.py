from dto.result import Result
from methods.method import Method

class SecantMethod(Method):
    name = 'Метод секущих'

    def check(self):
        root_exists = self.equation.root_exists(self.left, self.right)
        return root_exists, 'Отсутствует корень на заданном промежутке или корней > 2' if not root_exists else ''

    def solve(self) -> Result:
        f = self.equation.function
        x0 = self.left
        x1 = self.left + self.epsilon

        epsilon = self.epsilon
        iteration = 0

        while True:
            iteration += 1
            f0 = f(x0)
            f1 = f(x1)

            x2 = x1 - f1 * (x1 - x0) / (f1 - f0)

            if self.log:
                print(f'{iteration}: x0 = {x0:.3f}, x1 = {x1:.3f}, f(x0) = {f0:.3f}, f(x1) = {f1:.3f}, '
                      f'x2 = {x2:.3f}, |x2 - x1| = {abs(x2 - x1):.3e}')

            if abs(x2 - x1) < epsilon and abs(f(x2)) < epsilon:
                x1 = x2
                break

            x0, x1 = x1, x2

        return Result(x1, f(x1), iteration, self.decimal_places)

