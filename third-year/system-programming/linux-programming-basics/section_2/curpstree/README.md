# curpstree

Программа, позволяющая получить дерево процессов от `init` до текущего процесса.

# Пример использования:

```bash
make
./curpstree
```

Вывод:

```
systemd(1)
  login(629)
    zsh(728)
      startx(751)
        xinit(771)
          dwm(792)
            st(18269)
              zsh(18270)
```

Удаление бинаря :)

```bash
make clean
```
