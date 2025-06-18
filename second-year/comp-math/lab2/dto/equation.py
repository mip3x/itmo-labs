from typing import Callable
import matplotlib.pyplot as plt
import numpy as np


dx = 0.00001

def derivative(f, x, dx=1e-5):
    return (f(x + dx) - f(x - dx)) / (2 * dx)


class Equation:
    def __init__(self, function: Callable, text: str):
        self.text = text
        self.function = function

    def draw(self, left: float, right: float):
        x = np.linspace(left, right, 100000)
        y = [self.function(xi) for xi in x]

        plt.figure(figsize=(8, 6))
        plt.plot(x, y, label=f'f(x) = {self.text}')
        plt.xlabel('x')
        plt.ylabel('f(x)')
        plt.title('График функции')
        plt.ylim(-50, 50)
        plt.legend()
        plt.xticks(np.arange(-50, 50, 5))
        plt.yticks(np.arange(-50, 50, 5))
        plt.grid(True)
        plt.show()

    def root_exists(self, left: float, right: float):
        return (self.function(left) * self.function(right) < 0) \
               and (derivative(self.function, left, dx) * derivative(self.function, left, dx) > 0)
