import numpy as np

# Параметры Эрланга
k = 4                # порядок
scale = 1/(0.0154)   # параметр масштаба (1/lambda)
n = 300              # размер выборки

# Генерация выборки
samples = np.random.gamma(shape=k, scale=scale, size=n)

for sample in samples:
    print(sample)
