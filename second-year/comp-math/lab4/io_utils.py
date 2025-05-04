import sys

def read_input():
    if len(sys.argv) > 1:
        filename = sys.argv[1]
        xs, ys = [], []
        with open(filename, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                xi, yi = map(float, line.split())
                xs.append(xi)
                ys.append(yi)
        return xs, ys

    print("Введите пары x y (по одной на строке), завершите ввод пустой строкей:")
    xs, ys = [], []
    while True:
        try:
            line = input().strip()
        except EOFError:
            break
        if not line:
            break
        xi, yi = map(float, line.split())
        xs.append(xi)
        ys.append(yi)
    return xs, ys
