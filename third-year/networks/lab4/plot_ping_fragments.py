import matplotlib.pyplot as plt


sizes = [100, 500, 1000, 1500, 2000, 3000, 5000, 10000, 15000, 20000]
fragments = [1, 1, 1, 2, 2, 3, 4, 7, 11, 14]

plt.figure(figsize=(8, 5))
plt.plot(sizes, fragments, marker="o")
plt.title("Зависимость количества фрагментов от размера ping-пакета")
plt.xlabel("Размер ping-пакета, байт")
plt.ylabel("Количество фрагментов")
plt.grid(True)
plt.xticks(sizes, rotation=45)
plt.tight_layout()
plt.savefig("ping_fragments_graph.png", dpi=200)
