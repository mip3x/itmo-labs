import math

def calc_rms(y_true, y_approx):
    n = len(y_true)
    S = sum((yp - yt) ** 2 for yt, yp in zip(y_true, y_approx))
    return math.sqrt(S / n)

def calc_r2(y_true, y_approx):
    mean_y = sum(y_true) / len(y_true)
    SS_res = sum((yt - yp) ** 2 for yt, yp in zip(y_true, y_approx))
    SS_tot = sum((yt - mean_y) ** 2 for yt in y_true)
    return 1 - SS_res / SS_tot

def calc_pearson(x, y):
    n = len(x)
    sum_x = sum(x); sum_y = sum(y)
    sum_xy = sum(xi*yi for xi, yi in zip(x, y))
    sum_x2 = sum(xi*xi for xi in x); sum_y2 = sum(yi*yi for yi in y)
    num = n*sum_xy - sum_x*sum_y
    den = math.sqrt((n*sum_x2 - sum_x**2)*(n*sum_y2 - sum_y**2))
    return num/den
