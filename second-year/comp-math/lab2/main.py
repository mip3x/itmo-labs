import math
from os import walk
import numpy as np

from dto.equation import Equation
from methods.secant_method import SecantMethod
from methods.simple_iterations_method import SimpleIterationsMethod
from methods.chord_method import ChordMethod

import system_of_equation
import mainboilerplate

methods = {
    1: ChordMethod,
    2: SecantMethod,
    3: SimpleIterationsMethod
}

predefined_functions = {
    1: Equation(lambda x: (-1.38*x**3 - 5.42*x**2 + 2.57*x + 10.95), '-1.38*x^3 - 5.42*x^2 + 2.57*x + 10.95'),
    2: Equation(lambda x: (x**3 - 1.89*x**2 - 2*x + 1.76), 'x^3 - 1.89*x^2 - 2*x + 1.76'),
    # https://cutt.ly/6zNbCha
    3: Equation(lambda x: (x / 2 - 2 * np.cbrt(x + 2.39)), 'x/2 - 2*(x + 2.39)^(1/3)'),
    # https://cutt.ly/MzNdHH5
    4: Equation(lambda x: (-x / 2 + math.e ** x + 5 * math.sin(x)), '-x/2 + e^x + 5*sin(x)'),
    5: Equation(lambda x: (x ** 3 - x + 4), 'x^3 - x + 4'),
    6: Equation(lambda x: (np.cos(x) - x), 'cos(x) - x')
}

ENABLE_LOGGING = True

while True:
    equation_type = mainboilerplate.choose_equation_type()

    if equation_type == 3:
      break

    if equation_type == 1:
      function = mainboilerplate.choose_equation(predefined_functions)
      try:
          function.draw(-50, 50)
      except Exception as e:
          print('(!) Не удалось построить график функции, ', e)

      method_number = mainboilerplate.choose_method_number(methods)

      while True:
          if method_number == 4:
              left, epsilon, decimal_places = mainboilerplate.read_initial_data_newton()
              right = 0
          else:
              left, right, epsilon, decimal_places = mainboilerplate.read_initial_data()

          method = methods[method_number](function, left, right, epsilon, decimal_places, ENABLE_LOGGING)
          try:
              verified, reason = method.check()
          except TypeError as te:
              print('(!) Ошибка при вычислении значения функции, возможно она не определена на всем интервале.')
              continue
          if not verified:
            print('(!) Введенные исходные данные для метода некорректны: ', reason)
            if input('\nВвести данные границ ещё раз? [д/н] ') != 'д':
                break
            continue
          break

      # try:
      #     function.draw(left, right)
      # except Exception as e:
      #     print('(!) Не удалось построить график функции, ', e)

      output_file_name = input("Введите имя файла для вывода результата или пустую строку, чтобы вывести в консоль: ")

      try:
          if ENABLE_LOGGING:
              print('Процесс решения: ')
          result = method.solve()
      except Exception as e:
          print(e)
          print('(!) Что-то пошло не так при решении: ', e)
          continue

      mainboilerplate.print_result(result, output_file_name)

      if input('\nЕще раз? [д/н] ') != 'д':
          break
    else:
      system_of_equation.run()

print('Спасибо за использование программы!')
