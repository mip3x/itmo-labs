import numpy as np
import pandas as pd

# Чтение данных из файлов
x = pd.read_csv("data.txt", header=None).iloc[:,0].values
y = pd.read_csv("generated_data.txt", header=None).iloc[:,0].values

# Проверим, что размеры совпадают
n = min(len(x), len(y))
x = x[:n]
y = y[:n]

# Средние значения
Mx = np.mean(x)
My = np.mean(y)

# Числитель и знаменатель
num = np.sum((x - Mx) * (y - My))
den = np.sqrt(np.sum((x - Mx)**2) * np.sum((y - My)**2))

# Коэффициент корреляции
r_xy = num / den

print("Коэффициент корреляции:", r_xy)
