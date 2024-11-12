import csv

import matplotlib.pyplot as plt

with open('additional2_data.csv') as file:
    reader = csv.reader(file, delimiter=",", quotechar='"')
    next(reader, None)
    data_read = [row for row in reader]

data_open = [[], [], [], []]
data_high = [[], [], [], []]
data_low = [[], [], [], []]
data_close = [[], [], [], []]

dates = {'13/09/18': 0, '15/10/18': 1, '13/11/18': 2, '13/12/18': 3}
invalid_dates = {v: k for k, v in dates.items()}

for raw in data_read:
    id = dates[raw[2]]
    data_open[id].append(int(raw[4]))
    data_high[id].append(int(raw[5]))
    data_low[id].append(int(raw[6]))
    data_close[id].append(int(raw[7]))

data = []
for i in range(4):
    data.append(data_open[i])
    data.append(data_high[i])
    data.append(data_low[i])
    data.append(data_close[i])

fig = plt.figure("Дополнительное задание №3")

ax = fig.add_axes([0.15, 0.21, 0.8, 0.8])
labels = list()
for date in dates:
    labels.append(f'{date} - open')
    labels.append(f'{date} - max')
    labels.append(f'{date} - min')
    labels.append(f'{date} - close')

bplot = ax.boxplot(data,
                   vert=True,
                   patch_artist=True)

colors = ['blue', 'orange', 'grey', 'yellow',
          'lightblue', 'lightgreen', 'darkblue', 'brown',
          'darkgrey', 'brown', 'darkblue', 'darkgreen',
          'blue', 'orange', 'lightgrey', 'lightyellow']

for patch, color in zip(bplot['boxes'], colors):
    patch.set_facecolor(color)

ax.set_xlabel('Значения')
ax.set_ylabel('Ящики')

xtickNames = plt.setp(ax, xticklabels=labels)
plt.setp(xtickNames, rotation=45, fontsize=8)
plt.show()
