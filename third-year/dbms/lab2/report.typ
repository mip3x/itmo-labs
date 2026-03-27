#set page(
  paper: "a4",
  margin: (left: 25mm, right: 15mm, top: 20mm, bottom: 20mm),
)

#set text(
  font: "JetBrains Mono",
  size: 11pt,
)

#set par(
  leading: 1.4em,
  justify: true,
)

#set heading(numbering: "1.")

#let organization = "ФГБОУ высшего профессионального образования «Санкт-Петербургский национальный исследовательский университет информационных технологий, механики и оптики»"
#let faculty = "Факультет программной инженерии и компьютерной техники (ФПИиКТ)"

#let work_title = "Дисциплина «РСХД»"

#let student_name = "* * *"
#let group = "Группа P3311"

#let teacher_name = "Заболотняя Ольга Михайловна"

#let year = "2026"

#align(center)[
  #v(10mm)
  #text(weight: "bold")[#organization]
  #v(3mm)
  #faculty
  #v(25mm)

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №2]
  #v(6mm)
  #text(weight: "bold")[#work_title]
  #v(2mm)
]

#v(30mm)

#align(right)[
  Выполнил: #student_name \
  #group \
  \
  Преподаватель практики: #teacher_name
]

#v(25mm)

#align(center)[
  #year
]

#pagebreak()

= Лабораторная работа №2

\
== Условие

Цель работы - настроить процедуру периодического резервного копирования базы данных, сконфигурированной в ходе выполнения лабораторной работы №2, а также разработать и отладить сценарии восстановления в случае сбоев.

Узел из предыдущей лабораторной работы используется в качестве основного. Новый узел используется в качестве резервного. Учётные данные для подключения к новому узлу выдаёт преподаватель. В сценариях восстановления необходимо использовать копию данных, полученную на первом этапе данной лабораторной работы.

\
_Требования к отчёту_

Отчет должен быть самостоятельным документом (без ссылок на внешние ресурсы), содержать всю последовательность команд и исходный код скриптов по каждому пункту задания. Для демонстрации результатов приводить команду вместе с выводом (самой наглядной частью вывода, при необходимости).

\
*Этап 1. Резервное копирование*

Настроить резервное копирование с основного узла на резервный следующим образом:

1. Периодические полные копии + непрерывное архивирование.
2. Включить для СУБД режим архивирования WAL; настроить копирование WAL (scp) на резервный узел; настроить полное резервное копирование (pg_basebackup) по расписанию (cron) раз в неделю. Созданные полные копии должны сразу копироваться (scp) на резервный хост. Срок хранения копий на основной системе - 1 неделя, на резервной - 4 недели. По истечении срока хранения, старые архивы и неактуальные WAL должны автоматически уничтожаться.
3. Подсчитать, каков будет объем резервных копий спустя месяц работы системы, исходя из следующих условий:
    - Средний объем новых данных в БД за сутки: 500МБ.
    - Средний объем измененных данных за сутки: 100МБ.
6. Проанализировать результаты.

\
*Этап 2. Потеря основного узла*

Этот сценарий подразумевает полную недоступность основного узла. Необходимо восстановить работу СУБД на РЕЗЕРВНОМ узле, продемонстрировать успешный запуск СУБД и доступность данных.

\
*Этап 3. Повреждение файлов БД*

Этот сценарий подразумевает потерю данных (например, в результате сбоя диска или файловой системы) при сохранении доступности основного узла. Необходимо выполнить полное восстановление данных из резервной копии и перезапустить СУБД на ОСНОВНОМ узле.

Ход работы:

Симулировать сбой:

1. удалить с диска директорию WAL со всем содержимым.
2. Проверить работу СУБД, доступность данных, перезапустить СУБД, проанализировать результаты.
3. Выполнить восстановление данных из резервной копии, учитывая следующее условие:
        исходное расположение директории PGDATA недоступно - разместить данные в другой директории и скорректировать конфигурацию.
4. Запустить СУБД, проверить работу и доступность данных, проанализировать результаты.

\
*Этап 4. Логическое повреждение данных*

Этот сценарий подразумевает частичную потерю данных (в результате нежелательной или ошибочной операции) при сохранении доступности основного узла. Необходимо выполнить восстановление данных на ОСНОВНОМ узле следующим способом:

    Генерация файла на резервном узле с помощью pg_dump и последующее применение файла на основном узле.

Ход работы:

1. В каждую таблицу базы добавить 2-3 новые строки, зафиксировать результат.
2. Зафиксировать время и симулировать ошибку:
        удалить каждую вторую строку в любой таблице (DELETE)
3. Продемонстрировать результат.
4. Выполнить восстановление данных указанным способом.
5. Продемонстрировать и проанализировать результат.

\
== Выполнение

Узлы:
- Основной: `postgres1@129`
- Резервный: `postgres0@135`

\
=== Этап 1. Резервное копирование

На резервном узле создадим директории:

```sh
mkdir -p "$HOME/backup_lab2/base"
mkdir -p "$HOME/backup_lab2/wal"
```

\
*Настройка `SSH`-ключей*

Настроим `ssh`-ключи для работы `scp` без пароля

```sh
[postgres1@pg129 ~]$ ssh-keygen -t ed25519
[postgres1@pg129 ~]$ ssh-copy-id -i ~/.ssh/id_ed25519.pub postgres0@pg135
```

Проверка работоспособности:

```sh
[postgres1@pg129 ~]$ ssh postgres0@pg135
Last login: Fri Mar 27 07:40:55 2026 from 192.168.11.129
[postgres0@pg135 ~]$ exit
выход
Connection to pg135.cs.ifmo.ru closed.
```

Добавим в `ssh`-конфиг алиас для `backup-node`:

```sh
[postgres1@pg129 ~]$ cat > .ssh/config
Host backup-node
    HostName pg135
    User postgres0
[postgres1@pg129 ~]$ ssh backup-node
Last login: Fri Mar 27 08:12:27 2026 from 192.168.11.129
[postgres0@pg135 ~]$ exit
выход
Connection to pg135.cs.ifmo.ru closed.
```

\
*Режим архивирования `WAL`*

Включим режим архивирования `WAL`:

```sh
[postgres1@pg129 ~]$ vi /var/db/postgres1/zas34/postgresql.conf
...
archive_mode = on               # enables archiving; off, on, or always
archive_command = 'scp %p backup-node:/var/db/postgres0/backup_lab2/wal/%f'
                                # command to use to archive a WAL file
                                # placeholders: %p = path of file to archive
                                #               %f = file name only
                                # e.g. 'test ! -f /mnt/server/archivedir/%f && cp %p /mnt/server/archivedir/%f'
...
[postgres1@pg129 ~]$ pg_ctl -D "$PGDATA" restart
ожидание завершения работы сервера.... готово
сервер остановлен
ожидание запуска сервера....2026-03-27 08:19:48.829 MSK [14293] СООБЩЕНИЕ:  передача вывода в протокол процессу сбора протоколов
2026-03-27 08:19:48.829 MSK [14293] ПОДСКАЗКА:  В дальнейшем протоколы будут выводиться в каталог "log".
 готово
сервер запущен
[postgres1@pg129 ~]$ psql -p 9066 -d postgres -U postgres1 -Atqc "show archive_command;"
scp %p backup-node:/var/db/postgres0/backup_lab2/wal/%f
[postgres1@pg129 ~]$ psql -p 9066 -d postgres -U postgres1 -Atqc "show archive_mode;"
on
```

Для тестирования архивирования воспользуемся `pg_switch_wal`:

```
pg_switch_wal () → pg_lsn

Forces the server to switch to a new write-ahead log file, which allows the current file to be archived (assuming you are using continuous archiving). The result is the ending write-ahead log location plus 1 within the just-completed write-ahead log file. If there has been no write-ahead log activity since the last write-ahead log switch, pg_switch_wal does nothing and returns the start location of the write-ahead log file currently in use.

This function is restricted to superusers by default, but other users can be granted EXECUTE to run the function.
```

Она заставляет закрыть текущий `WAL`-файл и начать писать в новый, после закрытия файла вызывается `archive_command`

```sh
[postgres1@pg129 ~]$ psql -p 9066 -d postgres -U postgres1 -Atqc "select pg_switch_wal();"
0/2F1DD30
[postgres0@pg135 ~]$ ls -la backup_lab2/wal/
total 5818
drwxr-xr-x  2 postgres0 postgres        3 27 марта 08:25 .
drwxr-xr-x  5 postgres0 postgres        5 27 марта 08:15 ..
-rw-------  1 postgres0 postgres 16777216 27 марта 08:25 000000010000000000000002
```

\
*Бэкапирование*

Создадим на основном узле директорию для бэкапа и протестируем сам бэкап:

```sh
[postgres1@pg129 ~]$ mkdir -p "$HOME/backup_lab2/tmp"
[postgres1@pg129 ~]$ pg_basebackup \
  -D "$HOME/backup_lab2/tmp/base_test" \
  -p 9066 \
  -U postgres1 \
  -Fp \
  -X stream
pg_basebackup: ошибка: подключиться к серверу через сокет "/tmp/.s.PGSQL.9066" не удалось: ВАЖНО:  в pg_hba.conf нет записи, разрешающей подключение для репликации с компьютера "[local]" для пользователя "postgres1", без шифрования
```

В чём проблема? `pg_basebackup` подключается не как обычный клиент к базе `postgres`, а в режиме `replication protocol`. Поэтому в `pg_hba.conf` для базы postgres нужна отдельная запись. Добавим разрешения для пользователя `postgres1` и перечитаем конфиг:

```sh
[postgres1@pg129 ~]$ tail -8 /var/db/postgres1/zas34/pg_hba.conf
# Allow replication connections from localhost, by a user with the
# replication privilege.
#local   replication     all                                     trust
#host    replication     all             127.0.0.1/32            trust
#host    replication     all             ::1/128                 trust
local   replication   postgres1                   trust
host    replication   postgres1   127.0.0.1/32    trust
host    replication   postgres1   ::1/128         trust
[postgres1@pg129 ~]$ pg_ctl -D "$HOME/zas34" reload
сигнал отправлен серверу
```

Попытаемся вновь:

```sh
[postgres1@pg129 ~]$ pg_basebackup \
  -D "$HOME/backup_lab2/tmp/base_test" \
  -p 9066 \
  -U postgres1 \
  -Fp \
  -X stream
pg_basebackup: ошибка: каталог "/var/db/postgres1/grj79" существует, но он не пуст
pg_basebackup: удаление каталога данных "/var/db/postgres1/backup_lab2/tmp/base_test"
```

Проблема с `tablespace`. Определим для него путь:

```sh
[postgres1@pg129 ~]$ mkdir -p "$HOME/backup_lab2/tmp/base_test_ts/grj79"
[postgres1@pg129 ~]$ pg_basebackup \
  -D "$HOME/backup_lab2/tmp/base_test" \
  -p 9066 \
  -U postgres1 \
  -Fp \
  -X stream \
  -T "/var/db/postgres1/grj79=$HOME/backup_lab2/tmp/base_test_ts/grj79"
```

Напишем скрипт `backup.sh` для бэкапирования:

```sh
#!/usr/bin/env bash

set -e

DATE=$(date +%Y-%m-%d-%H-%M-%S)
ROOT="$HOME/backup_lab2"
TMP="$ROOT/tmp"
BASE="$ROOT/base"
REMOTE="postgres0@pg135:/var/db/postgres0/backup_lab2"

# создание директорий
mkdir -p "$TMP/ts/grj79" "$BASE"

# создание бэкапа локально
pg_basebackup -D "$TMP/base" -p 9066 -U postgres1 -Fp -X stream -T "/var/db/postgres1/grj79=$TMP/ts/grj79"

# архивирование
tar -czf "$BASE/base-$DATE.tar.gz" -C "$TMP" base ts

# перенос на резервный узел
scp "$BASE/base-$DATE.tar.gz" "$REMOTE/base/"

# удаление локальной копии
rm -rf "$TMP/base" "$TMP/ts"

# очистка старых архивов локально и удалённо
find "$BASE" -name 'base-*.tar.gz' -mtime +7 -delete
ssh postgres0@pg135 "find /var/db/postgres0/backup_lab2/base -name 'base-*.tar.gz' -mtime +28 -delete; find /var/db/postgres0/backup_lab2/wal -type f -mtime +28 -delete"
```

Проверим работоспособность:

```sh
[postgres1@pg129 ~/backup_lab2]$ ./backup.sh
base-2026-03-27-09-05-10.tar.gz                                                                              100% 5424KB 102.7MB/s   00:00
[postgres0@pg135 ~/backup_lab2/base]$ ls -lh
total 4773
-rw-r--r--  1 postgres0 postgres  5,3M 27 марта 09:05 base-2026-03-27-09-05-10.tar.gz
```

Теперь добавим правила `cron`:

```sh
[postgres1@pg129 ~]$ crontab -e
0 3 * * 0 /var/db/postgres1/backup_lab2/backup.sh
```

Эта запись означает, что в `03:00` каждого месяца каждого воскресенья будет выполняться скрипт `backup.sh`

\
*Оценка объёма резервных копий за месяц*

Исходные данные:
- средний объём *новых* данных в базе за сутки: 500 МБ
- средний объём *изменённых* данных в базе за сутки: 100 МБ

В реализованной схеме резервного копирования используются:
- полные физические резервные копии (`pg_basebackup`) раз в неделю
- непрерывное архивирование WAL

\
#underline[1. Оценка роста объёма базы данных]

Пусть:
- $V_"new" = 500$ МБ/сутки — объём новых данных
- $T = 30$ суток — рассматриваемый период

Тогда прирост объёма базы данных за месяц составит:

$
V_"db" = V_"new" dot T = 500 dot 30 = 15000 " МБ"
$

Следовательно,

$
15000 " МБ" approx 15 " ГБ"
$

Таким образом, если в начале периода объём базы был пренебрежимо мал по сравнению с месячным приростом, то через месяц работы размер базы составит приблизительно 15 ГБ

\
#underline[2. Оценка объёма `WAL`-архива]

`WAL` фиксирует все изменения, происходящие в базе данных. Для приближённой оценки примем, что объём WAL за сутки пропорционален сумме:
- объёма новых данных
- объёма изменённых данных

Тогда суточный объём `WAL` можно оценить как:

$
V_"wal_day" = V_"new" + V_"chg" = 500 + 100 = 600 " МБ/сутки"
$

где $V_"chg" = 100$ МБ/сутки — объём изменённых данных

За месяц объём `WAL`-архива составит:

$
V_"wal_month" = V_"wal_day" dot T = 600 dot 30 = 18000 " МБ"
$

Следовательно,

$
18000 " МБ" approx 18 " ГБ"
$

Таким образом, за месяц работы `СУБД` архив `WAL` составит приблизительно 18 ГБ

\
#underline[3. Оценка объёма полных резервных копий]

Полная резервная копия выполняется раз в неделю. За месяц будет создано примерно 4 полные копии. Так как база данных увеличивается постепенно, размеры полных копий также будут расти от недели к неделе

Приближённо можно оценить размеры четырёх недельных копий так:

- после 1-й недели:
$
V_1 = V_"new" dot 7 = 500 dot 7 = 3500 " МБ" approx 3.5 " ГБ"
$

- после 2-й недели:
$
V_2 = 500 dot 14 = 7000 " МБ" approx 7 " ГБ"
$

- после 3-й недели:
$
V_3 = 500 dot 21 = 10500 " МБ" approx 10.5 " ГБ"
$

- после 4-й недели:
$
V_4 = 500 dot 28 = 14000 " МБ" approx 14 " ГБ"
$

Тогда суммарный объём полных резервных копий за месяц:

$
V_"base_total" = V_1 + V_2 + V_3 + V_4
$

$
V_"base_total" = 3500 + 7000 + 10500 + 14000 = 35000 " МБ"
$

Следовательно,

$
35000 " МБ" approx 35 " ГБ"
$

Таким образом, суммарный объём еженедельных полных резервных копий за месяц составит приблизительно 35 ГБ

\
#underline[4. Общий объём резервных данных за месяц]

Общий объём резервных данных складывается из:
- объёма полных резервных копий
- объёма WAL-архива

Тогда:

$
V_"total" = V_"base_total" + V_"wal_month"
$

$
V_"total" = 35000 + 18000 = 53000 " МБ"
$

Следовательно,

$
53000 " МБ" approx 53 " ГБ"
$

Итак, спустя месяц работы системы объём резервных данных составит примерно 53 ГБ

\
#underline[5. Анализ результатов]

Наибольшую долю пространства занимают полные резервные копии, так как каждая новая копия содержит всё текущее состояние базы данных целиком

`WAL`-архив тоже занимает заметный объём, однако он хранит только изменения, происходящие между полными копиями. Благодаря этому использование непрерывного архивирования `WAL` значительно эффективнее, чем создание полной копии каждый день

Схема "полная копия + `WAL`" является компромиссом между:
- затратами дискового пространства
- скоростью восстановления
- возможностью восстановления на произвольный момент времени

Выбранная схема резервного копирования является более рациональной, чем хранение только полных копий или только логических дампов

\
=== Этап 2. Потеря основного узла

Остановим кластер №1:

```sh
[postgres1@pg129 ~]$ pg_ctl -D "$PGDATA" stop
ожидание завершения работы сервера.... готово
сервер остановлен
[postgres1@pg129 ~]$ pg_ctl -D "$PGDATA" status
pg_ctl: сервер не работает
```

На резервном узле создадим директорию для восстановления:

```sh
mkdir -p "$HOME/backup_lab2/restore"
```

Выберем последнюю копию и распакуем в `restore`:

```sh
[postgres0@pg135 ~]$ cd backup_lab2/
[postgres0@pg135 ~/backup_lab2]$ ls -l base
total 19086
-rw-r--r--  1 postgres0 postgres 5554435 27 марта 09:05 base-2026-03-27-09-05-10.tar.gz
-rw-r--r--  1 postgres0 postgres 5560291 27 марта 09:13 base-2026-03-27-09-13-42.tar.gz
-rw-r--r--  1 postgres0 postgres 5555837 27 марта 09:19 base-2026-03-27-09-19-00.tar.gz
-rw-r--r--  1 postgres0 postgres 5548802 27 марта 09:20 base-2026-03-27-09-20-00.tar.gz
[postgres0@pg135 ~/backup_lab2]$ cd restore/
[postgres0@pg135 ~/backup_lab2/restore]$ ls
[postgres0@pg135 ~/backup_lab2/restore]$ tar -xzf ../base/base-2026-03-27-09-20-00.tar.gz
[postgres0@pg135 ~/backup_lab2/restore]$ ls -la
total 10
drwxr-xr-x   4 postgres0 postgres  4 27 марта 10:15 .
drwxr-xr-x   5 postgres0 postgres  5 27 марта 08:15 ..
drwx------  20 postgres0 postgres 29 27 марта 09:20 base
drwxr-xr-x   3 postgres0 postgres  3 27 марта 09:20 ts
```

Назначим `PGDATA` и проверим корректность симлинка дополнительного табличного пространства:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ export PGDATA="$HOME/backup_lab2/restore/base"
[postgres0@pg135 ~/backup_lab2/restore]$ ls -l $PGDATA/pg_tblspc/
total 1
lrwx------  1 postgres0 postgres 42 27 марта 09:20 16388 -> /var/db/postgres1/backup_lab2/tmp/ts/grj79
```

Симлинк указывает на табличное пространство по пути пользователя с `primary`-узла. Исправим это:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ rm $PGDATA/pg_tblspc/16388
[postgres0@pg135 ~/backup_lab2/restore]$ ln -s "$HOME/backup_lab2/restore/ts/grj79" "$PGDATA/pg_tblspc/16388"
[postgres0@pg135 ~/backup_lab2/restore]$ ls -l $PGDATA/pg_tblspc/
total 1
lrwxr-xr-x  1 postgres0 postgres 46 27 марта 10:27 16388 -> /var/db/postgres0/backup_lab2/restore/ts/grj79
```

Создадим файл, переводящий `PostgreSQL` в режим восстановления:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ touch "$PGDATA/recovery.signal"
```

Настроим `restore_command`:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ vi "$PGDATA/postgresql.conf"
[postgres0@pg135 ~/backup_lab2/restore]$ cat $PGDATA/postgresql.conf |grep -A 3 restore_command
restore_command = 'cp /var/db/postgres0/backup_lab2/wal/%f %p'
                                # command to use to restore an archived WAL file
                                # placeholders: %p = path of file to restore
                                #               %f = file name only
```

Настроим `pg_hba.conf`:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ vi $PGDATA/pg_hba.conf
[postgres0@pg135 ~/backup_lab2/restore]$ tail -n 15 $PGDATA/pg_hba.conf | head -n 3
# "local" is for Unix domain socket connections only
local   all             all                                     trust
```

Запускаем сервер:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ pg_ctl -D "$PGDATA" -l "$PGDATA/server_recovery.log" start
ожидание запуска сервера.... готово
сервер запущен
[postgres0@pg135 ~/backup_lab2/restore]$ pg_ctl -D "$PGDATA" status
pg_ctl: сервер работает (PID: 33314)
/usr/local/bin/postgres "-D" "/var/db/postgres0/backup_lab2/restore/base"
```

Проверим работоспособность:

```sh
[postgres0@pg135 ~/backup_lab2/restore]$ psql -p 9066 -d postgres -U postgres1
psql (16.4)
Введите "help", чтобы получить справку.

postgres=# \l
                                                             Список баз данных
     Имя     | Владелец  | Кодировка | Провайдер локали |  LC_COLLATE  |   LC_CTYPE   | локаль ICU | Правила ICU |      Права доступа
-------------+-----------+-----------+------------------+--------------+--------------+------------+-------------+-------------------------
 postgres    | postgres1 | WIN1251   | libc             | ru_RU.CP1251 | ru_RU.CP1251 |            |             | =Tc/postgres1          +
             |           |           |                  |              |              |            |             | postgres1=CTc/postgres1+
             |           |           |                  |              |              |            |             | data_user=c/postgres1
 template0   | postgres1 | WIN1251   | libc             | ru_RU.CP1251 | ru_RU.CP1251 |            |             | =c/postgres1           +
             |           |           |                  |              |              |            |             | postgres1=CTc/postgres1
 template1   | postgres1 | WIN1251   | libc             | ru_RU.CP1251 | ru_RU.CP1251 |            |             | =c/postgres1           +
             |           |           |                  |              |              |            |             | postgres1=CTc/postgres1
 uglygraylaw | postgres1 | WIN1251   | libc             | ru_RU.CP1251 | ru_RU.CP1251 |            |             | =Tc/postgres1          +
             |           |           |                  |              |              |            |             | postgres1=CTc/postgres1+
             |           |           |                  |              |              |            |             | data_user=c/postgres1
(4 строки)

postgres=# select pg_is_in_recovery();
 pg_is_in_recovery
-------------------
 f
(1 строка)
```

\
=== Этап 3. Повреждение файлов БД

\
=== Этап 4

\
== Вывод


\
== Вопросы для подготовки к защите
- Резервные копии: назначение; локальные и удаленные; полные и инкрементные; холодные и горячие.
- Сравнение подходов SQL Dump, FS Level Backup, Continuous Archiving: применимость, преимущества, недостатки.
- Как рассчитать объем пространства для хранения резервных копий, от чего зависит?
- Как рассчитать требуемую производительность канала для выполнения резервных копий по сети, от чего зависит?
- Какие факторы оказывают влияние на скорость восстановления, почему?

\
== Использованные ресурсы

- #link("https://repo.postgrespro.ru/doc/pgsql/16.11/ru/postgres-A4.pdf")[Документация]
- #link("https://www.interdb.jp/pg/index.html")[PostgreSQL Internal]
- #link("https://www.postgresql.org/docs/current/continuous-archiving.html#BACKUP-PITR-RECOVERY")[Непрерывное архивирование и восстановление на момент времени (Point-in-Time Recovery, PITR)]
