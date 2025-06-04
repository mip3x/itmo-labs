def finite_diff_table(ys):
    table = [ys[:]]

    while (len(table[-1])) > 1:
        prev = table[-1]
        next = [prev[i + 1] - prev[i] for i in range(len(prev) - 1)]
        table.append(next)

    return table


def divided_diff_table(xs, ys):
    n = len(xs)
    dd = [[0.0] * n for _ in range(n)]

    for i in range(n):
        dd[i][0] = ys[i]

    # for x in range(len(dd)):
    #     print(dd[x])

    for j in range(1, n):
        for i in range(n - j):
            dd[i][j] = (dd[i + 1][j - 1] - dd[i][j - 1]) / (xs[i + j] - xs[i])
            # for x in range(len(dd)):
            #     print(dd[x])

    return dd
