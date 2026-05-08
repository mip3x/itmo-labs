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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №3]
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

\
= Условие

Цель работы - ознакомиться с методами и средствами построения отказоустойчивых решений на базе СУБД Postgres; получить практические навыки восстановления работы системы после отказа.

Работа рассчитана на двух человек и выполняется в три этапа: настройка, симуляция и обработка сбоя, восстановление.

\
_Требования к выполнению работы_

- В качестве хостов использовать одинаковые виртуальные машины.
- В первую очередь необходимо обеспечить сетевую связность между ВМ.
- Для подключения к СУБД (например, через psql), использовать отдельную виртуальную или физическую машину.
- Демонстрировать наполнение базы и доступ на запись на примере не менее, чем двух таблиц, столбцов, строк, транзакций и клиентских сессий.

\
*Этап 1. Конфигурация*

Развернуть postgres на двух узлах в режиме трансляции логов. Не использовать дополнительные пакеты. Продемонстрировать доступ в режиме чтение/запись на основном сервере. Продемонстрировать, что новые данные синхронизируются на резервный сервер.

\
*Этап 2. Симуляция и обработка сбоя*

_Подготовка:_

- Установить несколько клиентских подключений к СУБД.
- Продемонстрировать состояние данных и работу клиентов в режиме чтение/запись.

\
_Сбой:_

Симулировать переполнение дискового пространства на основном узле - заполнить всё свободное пространство раздела с PGDATA “мусорными” файлами.

\
_Обработка:_

- Найти и продемонстрировать в логах релевантные сообщения об ошибках.
- Выполнить переключение (failover) на резервный сервер.
- Продемонстрировать состояние данных и работу клиентов в режиме чтение/запись.

\
*Этап 3. Восстановление*

- Восстановить работу основного узла - откатить действие, выполненное с виртуальной машиной на этапе 2.2.
- Актуализировать состояние базы на основном узле - накатить все изменения данных, выполненные на этапе 2.3.
- Восстановить исправную работу узлов в исходной конфигурации (в соответствии с этапом 1).
- Продемонстрировать состояние данных и работу клиентов в режиме чтение/запись.

\
= Выполнение

Узлы:
- Основной: `primary`
- Резервный: `standby`

\
== Этап 0. Подготовка окружения

\
*Настройка хостов*

Хосты были настроены через `docker-compose.yml`:

```yaml
services:
  primary:
    container_name: primary
    build:
      context: ./primary
    restart: unless-stopped
    ports:
      - "5432:5432"
    environment:
      - PGDATA=/var/lib/postgresql/data
      - PGENCODING=UTF8
      - PGLOCALE=en_US.UTF8
      - PGUSERNAME=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - ./primary/data:/var/lib/postgresql/data
    networks:
      - pg_net

  standby:
    container_name: standby
    build:
      context: ./standby
    restart: unless-stopped
    ports:
      - "5433:5432"
    depends_on:
      - primary
    environment:
      - PGDATA=/var/lib/postgresql/data
      - PGENCODING=UTF8
      - PGLOCALE=en_US.UTF8
      - PGUSERNAME=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - ./standby/data:/var/lib/postgresql/data
    networks:
      - pg_net

networks:
  pg_net:
    driver: bridge
```

Соединение между хостами установлено через `docker`-сеть `pg_net`, `primary` использует порт 5432, а `standby` - 5433

\
*Использованная файловая структура*

```sh
├── docker-compose.yml
├── primary
│   ├── conf
│   │   ├── pg_hba.conf
│   │   └── postgresql.conf
│   ├── Dockerfile
│   ├── init
│   │   └── init-primary.sh
│   └── scripts
│       ├── init-db.sql
│       ├── read_client.sh
│       └── write_client.sh
└── standby
    ├── conf
    │   ├── pg_hba.conf
    │   └── postgresql.conf
    ├── Dockerfile
    ├── init
    │   └── init-standby.sh
    └── scripts
        ├── auto_promote.sh
        └── read_client.sh
```

\
== Этап 1. Конфигурация

Рассмотрим использованные файлы

=== `primary`-узел

\
*Dockerfile*

```Dockerfile
FROM postgres:latest

COPY conf/* /etc/postgresql/
COPY scripts/* /home/scripts/
COPY init/init-primary.sh /home/init/init-primary.sh
RUN chmod +x /home/scripts/read_client.sh
RUN chmod +x /home/scripts/write_client.sh
RUN chmod +x /home/init/init-primary.sh
```

По варианту должен использоваться режим трансляции логов. Это значит, что основной сервер будет постоянно асинхронно передавать резервному серверу журныла изменений `WAL`. Из документации:

#quote[
  Резервный сервер может читать файлы WAL из архива WAL (см. restore_command) или напрямую с главного сервера по соединению TCP (потоковая репликация)
]

Первое уже было реализовано во второй лабораторной работе, поэтому было решено использовать потоковую репликацию

Для подключения резервного узла к основному был создан отдельный пользователь `replicator`, который имеет право `REPLICATION`, что позволяет получать `WAL`-записи

\
*init-primary.sh*

```sh
#!/bin/bash

set -e

psql -v ON_ERROR_STOP=1 --username "postgres" -c "CREATE ROLE replicator WITH REPLICATION PASSWORD 'replicator_password' LOGIN;"
psql -v ON_ERROR_STOP=1 --username "postgres" -f "/home/scripts/init-db.sql"

cp /etc/postgresql/postgresql.conf "$PGDATA/postgresql.conf"
cp /etc/postgresql/pg_hba.conf "$PGDATA/pg_hba.conf"

echo "Configuration files copied!"
```

`listen_addresses` выставлен в `'*'` для возможности получения подключений из `docker`-сети

`wal_level` выставлен в `replica` для записи в `WAL` объёма, достаточного для репликации

\
*postgresql.conf*

```conf
listen_addresses = '*'
wal_level = replica
wal_keep_size = 64MB
max_wal_senders = 10
archive_mode = on
archive_command = 'echo "dummy command, archive_command called"'
log_connections = on
log_disconnections = on
log_duration = on
wal_log_hints = on
```

Последняя строка разрешает пользователю `replicator` подключаться к БД `replication` с любого адреса `docker`-сети по паролю

\
*pg_hba.conf*

```conf
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     trust
host    all             all             0.0.0.0/0               md5
host    replication     replicator      0.0.0.0/0               md5
```

=== `standby`-узел

\
*Dockerfile*

```Dockerfile
FROM postgres:latest

COPY conf/* /etc/postgresql/
COPY scripts/* /home/scripts/
COPY init/init-standby.sh /docker-entrypoint-initdb.d/init-standby.sh
RUN chmod +x /home/scripts/read_client.sh
RUN chmod +x /docker-entrypoint-initdb.d/init-standby.sh
RUN chmod +x /home/scripts/auto_promote.sh
```

\
*init-standby.sh*

До запуска кластера делаем `pg_basebackup` с основного узла, после чего создаём сигнальный файл, который переводит сервер в режим ожидания

```sh
#!/bin/bash

set -e

# Wait for primary to be ready
until pg_isready -h primary -p 5432 -U postgres; do
  echo "Waiting for primary to be ready..."
  sleep 2
done

# Stop the server
pg_ctl stop -D "$PGDATA"

# Clean up the data directory
rm -rf "$PGDATA"/*
echo "Data directory cleaned up"

# Perform base backup
PGPASSWORD='replicator_password' pg_basebackup -h primary -D /var/lib/postgresql/data -U replicator -v -P --wal-method=stream
echo "Base backup completed"

# Create standby.signal file
touch "$PGDATA/standby.signal"

# Set permissions
chown -R postgres:postgres "$PGDATA"

# Copy conf files
cp /etc/postgresql/postgresql.conf "$PGDATA/postgresql.conf"
cp /etc/postgresql/pg_hba.conf "$PGDATA/pg_hba.conf"
echo "Conf files copied"

# Start the server
pg_ctl -D "$PGDATA" start
```

`hot_standby` позволяет выполнять запросы на чтение данных с резервного сервера

`primary_conninfo` задаёт параметры подключения

\
*postgresql.conf*

```conf
listen_addresses = '*'
hot_standby = on
primary_conninfo = 'host=primary port=5432 user=replicator password=replicator_password'
wal_log_hints = on
log_connections = on
log_disconnections = on
log_duration = on
wal_log_hints = on
```

\
*pg_hba.conf*

```conf
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     trust
host    all             all             0.0.0.0/0               md5
host    replication     replicator      0.0.0.0/0               md5
```

=== Запуск

\
*primary*

Запуск контейнера:

```sh
docker compose up -d --build primary
```

Проверим логи и убедимся в работоспособности:

```sh
...
primary  | PostgreSQL init process complete; ready for start up.
primary  |
primary  | 2026-05-08 06:55:33.226 UTC [1] LOG:  starting PostgreSQL 18.3 (Debian 18.3-1.pgdg13+1) on x86_64-pc-linux-gnu, compiled by gcc (Debian 14.2.0-19) 14.2.0, 64-bit
primary  | 2026-05-08 06:55:33.227 UTC [1] LOG:  listening on IPv4 address "0.0.0.0", port 5432
primary  | 2026-05-08 06:55:33.227 UTC [1] LOG:  listening on IPv6 address "::", port 5432
primary  | 2026-05-08 06:55:33.232 UTC [1] LOG:  listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
primary  | 2026-05-08 06:55:33.240 UTC [69] LOG:  database system was shut down at 2026-05-08 06:55:33 UTC
primary  | 2026-05-08 06:55:33.245 UTC [1] LOG:  database system is ready to accept connections
```

Запустим скрипт инициализации:

```sh
❯ docker exec -it primary bash
root@2aa47f30d1ad:/# ./home/init/init-primary.sh
CREATE ROLE
CREATE DATABASE
You are now connected to database "test" as user "postgres".
CREATE TABLE
CREATE TABLE
INSERT 0 2
INSERT 0 2
Configuration files copied!
```

Перезапустим кластер (этого требуют изменённые параметры):

```sh
❯ docker restart primary
primary
```

\
*standby*

Запуск контейнера:

```sh
docker compose up -d --build standby
```

=== Заполнение БД

Скрипт для `primary`-узла был исполнен скриптом `init-primary.sh`:

\
*init-db.sql*

```sql
CREATE DATABASE test;

\c test;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    product VARCHAR(255)
);

INSERT INTO users (name) VALUES ('Alice'), ('Bob');
INSERT INTO orders (user_id, product) VALUES (1, 'Laptop'), (2, 'Smartphone');
```

Проверим данные на `primary`:

```sh
❯ docker exec -it primary bash
root@2aa47f30d1ad:/# psql -U postgres
psql (18.3 (Debian 18.3-1.pgdg13+1))
Type "help" for help.

postgres=# \c test
You are now connected to database "test" as user "postgres".
test=# SELECT * from users;
 id | name
----+-------
  1 | Alice
  2 | Bob
(2 rows)

test=# insert into users (name) values ('Brad');
INSERT 0 1
test=# SELECT * from users;
 id | name
----+-------
  1 | Alice
  2 | Bob
  5 | Brad
(3 rows)

test=#
\q
root@2aa47f30d1ad:/#
exit
```

Теперь проверим на `standby`:

```sh
❯ docker exec -it standby bash
root@ac1c0fbb2eb9:/# psql -U postgres
psql (18.3 (Debian 18.3-1.pgdg13+1))
Type "help" for help.

postgres=# \c test
You are now connected to database "test" as user "postgres".
test=# select * from users;
 id | name
----+-------
  1 | Alice
  2 | Bob
  5 | Brad
(3 rows)
```

Попробуем записать что-нибудь со `standby`:

```sh
test=# insert into users (name) values ('alex');
ERROR:  cannot execute INSERT in a read-only transaction
```

\
== Этап 2. Симуляция и обработка сбоя

\
=== Этап 2.1. Подготовка

Скрипт для симуляции чтения (для обоих хостов):

```sh
#!/bin/bash

while true; do
    psql -U postgres -d test -c "SELECT * FROM users;"
    psql -U postgres -d test -c "SELECT * FROM orders;"
    sleep 2
done
```

Скрипт для симуляции записи (только для основного узла):

```sh
#!/bin/bash

while true; do
    psql -U postgres -d test -c "INSERT INTO users (name) VALUES ('User_$(date +%s)');"

    items=("TV" "Mouse" "Keyboard" "HDMI cable")
    random_item=${items[$RANDOM % ${#items[@]}]}
    last_user_id=$(psql -U postgres -d test -t -c "SELECT id FROM users ORDER BY id DESC LIMIT 1;")
    psql -U postgres -d test -c "INSERT INTO orders (user_id, product) VALUES ($last_user_id, '$random_item');"
    sleep 2
done
```

Запустим скрипты:

```sh
docker exec -it primary bash /home/scripts/read_client.sh
docker exec -it primary bash /home/scripts/write_client.sh
docker exec -it standby bash /home/scripts/read_client.sh
```

Ожидается, что на `standby` новые данные с `primary` будут показываться автоматически

#figure(
  image("img/reads_writes.png"),
  caption: [Демонстрация в режиме чтение/запись (слева - основной, справа - резерв)]
)

\
= Вывод

В ходе лабораторной работы были изучены и применены основные механизмы обеспечения отказоустойчивости `PostgreSQL`: физическое резервное копирование, архивирование `WAL` и восстановление данных. Было смоделировано как физическое повреждение (утрата `WAL`), так и логическая ошибка (удаление данных), после чего выполнено восстановление с использованием базового бэкапа, архивов `WAL` (`PITR`) и логического дампа. В результате показано, что различные методы резервирования позволяют восстанавливать систему как до консистентного состояния, так и до конкретного момента времени, минимизируя потери данных.

\
= Вопросы для подготовки к защите

- Синхронная и асинхронная репликация: отличия, ограничения и область применения.
- Кластер в режиме Active-Active и Active-Standby: отличия, ограничения и область применения.
- Балансировка нагрузки: описание и область применения.
- От чего зависит время простоя системы в случае отказа?

\
= Использованные ресурсы

- #link("https://postgrespro.ru/docs/enterprise/current/warm-standby")[Трансляция журналов на резервные серверы]
- #link("https://postgresqlco.nf/doc/en/param/wal_level/")[`WAL` уровень]
