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
#set text(lang: "ru")
#show raw.where(block: true): it => block(
  width: 100%, inset: 6pt, fill: white, breakable: false,
  stroke: 0.4pt + luma(80%),
  [
    #set par(leading: 0.55em, justify: false)
    #text(size: 7pt, fill: black, it)
  ],
)

#let organization = "ФГБОУ высшего профессионального образования «Санкт-Петербургский национальный исследовательский университет информационных технологий, механики и оптики»"
#let faculty = "Факультет программной инженерии и компьютерной техники (ФПИиКТ)"

#let work_title = "Дисциплина «Администрирование систем и сетей»"

#let student1_name = "Иван Иванович Иванов"
#let student2_name = "Дмитрий Дмитриевич Дмитров"

#let group = "Группа P3411"

#let teacher_name = "Афанасьев Дмитрий Борисович"

#let year = "2026"

#align(center)[
  #v(10mm)
  #text(weight: "bold")[#organization]
  #v(3mm)
  #faculty
  #v(25mm)

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №1]
  #v(6mm)
  #text(weight: "bold")[#work_title]
  #v(2mm)
]

#v(30mm)

#align(right)[
  Выполнили: \
  #student1_name \
  #student2_name \
  #group \
  \
  Желаемая оценка: 3 \
  \
  Преподаватель практики: #teacher_name
]

#v(25mm)

#align(center)[
  #year
]

#pagebreak()

#outline(title: [Оглавление], indent: auto)
#pagebreak()
#set page(numbering: "1")

= Топология и адресация

#figure(
  [
    #image("img/topology.png", width: 90%)
    #v(3mm)
    #text(size: 9pt)[
      LoopBack0: AR1 - 10.0.1.1/32; AR2 - 10.0.1.2/32; AR3 - 10.0.1.3/32.
    ]
  ],
  caption: [Топология сети],
)

Адресация интерфейсов (GE - сокращение от GigabitEthernet):

#table(
  columns: (auto, auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*Интерфейс*], [*Сосед*], [*IP-адрес/маска*]),
  [R1], [GE0/0/0], [R2], [10.0.12.1/24],
  [R1], [GE0/0/1], [R3], [10.0.13.1/24],
  [R2], [GE0/0/0], [R1], [10.0.12.2/24],
  [R2], [GE0/0/1], [R3], [10.0.23.2/24],
  [R3], [GE0/0/1], [R1], [10.0.13.3/24],
  [R3], [GE0/0/0], [R2], [10.0.23.3/24],
  [R1], [LoopBack0], [—], [10.0.1.1/32],
  [R2], [LoopBack0], [—], [10.0.1.2/32],
  [R3], [LoopBack0], [—], [10.0.1.3/32],
)

= Ход работы

== Шаг 1. Задание имён устройств

Команда `system-view` переводит устройство из пользовательского режима
в режим настройки. Команда `sysname` задаёт имя маршрутизатора.

Маршрутизатор R1:

```text
<Huawei>system-view
Enter system view, return user view with Ctrl+Z.
[Huawei]sysname R1
[R1]
```

Маршрутизатор R2:

```text
<Huawei>system-view
Enter system view, return user view with Ctrl+Z.
[Huawei]sysname R2
[R2]
```

Маршрутизатор R3:

```text
<Huawei>system-view
Enter system view, return user view with Ctrl+Z.
[Huawei]sysname R3
[R3]
```

== Шаг 2. Исходное состояние интерфейсов и таблицы маршрутизации

Команда `display ip interface brief` выводит краткую информацию об IP-адресах
и состоянии интерфейсов. На R1 получен следующий вывод:

```text
[R1]display ip interface brief
*down: administratively down
^down: standby
(l): loopback
(s): spoofing
The number of interface that is UP in Physical is 3
The number of interface that is DOWN in Physical is 1
The number of interface that is UP in Protocol is 1
The number of interface that is DOWN in Protocol is 3

Interface                   IP Address/Mask     Physical   Protocol
GigabitEthernet0/0/0         unassigned          up         down
GigabitEthernet0/0/1         unassigned          up         down
GigabitEthernet0/0/2         unassigned          down       down
NULL0                       unassigned          up         up(s)
```

IP-адреса ещё не назначены (`unassigned`). У GE0/0/0 и GE0/0/1 физическое
соединение активно, но состояние протокола - `down`. GE0/0/2 неактивен.

Команда `display ip routing-table` показывает таблицу маршрутизации:

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 4        Routes : 4

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

== Шаг 3. Настройка адресов физических интерфейсов

Назначены адреса физических интерфейсов с маской /24 (255.255.255.0).

Маршрутизатор R1:

```text
[R1]interface GigabitEthernet 0/0/1
[R1-GigabitEthernet0/0/1]ip ad 10.0.13.1 24
[R1-GigabitEthernet0/0/1]quit
[R1]interface GigabitEthernet 0/0/0
[R1-GigabitEthernet0/0/0]ip ad 10.0.12.1 24
[R1-GigabitEthernet0/0/0]quit
```

Маршрутизатор R2:

```text
[R2]interface GigabitEthernet 0/0/0
[R2-GigabitEthernet0/0/0]ip ad 10.0.12.2 24
[R2-GigabitEthernet0/0/0]quit
[R2]interface GigabitEthernet 0/0/1
[R2-GigabitEthernet0/0/1]ip ad 10.0.23.2 24
[R2-GigabitEthernet0/0/1]quit
```

Маршрутизатор R3:

```text
[R3]interface GigabitEthernet 0/0/1
[R3-GigabitEthernet0/0/1]ip ad 10.0.13.3 24
[R3-GigabitEthernet0/0/1]quit
[R3]interface GigabitEthernet 0/0/0
[R3-GigabitEthernet0/0/0]ip ad 10.0.23.3 24
[R3-GigabitEthernet0/0/0]quit
```

=== Проверка связи

Команда `ping` проверяет доступность адресата с помощью ICMP Echo.
С R1 выполнены проверки физических интерфейсов R2 и R3:

```text
[R1]ping 10.0.12.2
  PING 10.0.12.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.12.2: bytes=56 Sequence=1 ttl=255 time=160 ms
    Reply from 10.0.12.2: bytes=56 Sequence=2 ttl=255 time=30 ms
    Reply from 10.0.12.2: bytes=56 Sequence=3 ttl=255 time=20 ms
    Reply from 10.0.12.2: bytes=56 Sequence=4 ttl=255 time=10 ms
    Reply from 10.0.12.2: bytes=56 Sequence=5 ttl=255 time=30 ms

  --- 10.0.12.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 10/50/160 ms
```

```text
[R1]ping 10.0.13.3
  PING 10.0.13.3: 56 data bytes, press CTRL_C to break
    Reply from 10.0.13.3: bytes=56 Sequence=1 ttl=255 time=90 ms
    Reply from 10.0.13.3: bytes=56 Sequence=2 ttl=255 time=20 ms
    Reply from 10.0.13.3: bytes=56 Sequence=3 ttl=255 time=20 ms
    Reply from 10.0.13.3: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.13.3: bytes=56 Sequence=5 ttl=255 time=30 ms

  --- 10.0.13.3 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 20/36/90 ms
```

В обеих проверках получены ответы на все пять запросов.
Связь R1 с физическими интерфейсами R2 и R3 подтверждена.

=== Таблица маршрутизации после настройки

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 10        Routes : 10

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.12.0/24         Direct  0    0     D      10.0.12.1      GigabitEthernet0/0/0
10.0.12.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Автоматически появились прямые маршруты к двум подсетям, собственным и широковещательным адресам. Всего 10 маршрутов.

== Шаг 4. Создание loopback-интерфейсов

Созданы логические интерфейсы LoopBack0 с маской /32 (255.255.255.255).

R1:
```text
[R1]interface LoopBack 0
[R1-LoopBack0]ip ad 10.0.1.1 32
```

R2:
```text
[R2]interface LoopBack 0
[R2]ip address 10.0.1.2 32
```

R3:
```text
[R3]interface LoopBack 0
[R3]ip address 10.0.1.3 32
```

После создания LoopBack0 в таблице R1 появился прямой маршрут 10.0.1.1/32:

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 11        Routes : 11

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.12.0/24         Direct  0    0     D      10.0.12.1      GigabitEthernet0/0/0
10.0.12.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Проверим доступность LoopBack'ов:

```text
[R1]ping -a 10.0.1.1 10.0.1.2
  PING 10.0.1.2: 56 data bytes, press CTRL_C to break
    Request time out
    Request time out
    Request time out
    Request time out
    Request time out

  --- 10.0.1.2 ping statistics ---
    5 packet(s) transmitted
    0 packet(s) received
    100.00% packet loss
```

Потери - 100%. В таблице R1 отсутствует маршрут к 10.0.1.2.

== Шаг 5. Настройка статических маршрутов

Команда `ip route-static` задаёт сеть или адрес назначения, длину префикса
и следующий переход. На R1 настроены маршруты к LoopBack0 R2 и R3:

```text
[R1]ip route-static 10.0.1.2 32 10.0.12.2
[R1]ip route-static 10.0.1.3 32 10.0.13.3
```

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 13        Routes : 13

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.2/32          Static  60   0     RD     10.0.12.2      GigabitEthernet0/0/0
10.0.1.3/32          Static  60   0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.12.0/24         Direct  0    0     D      10.0.12.1      GigabitEthernet0/0/0
10.0.12.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Добавлены две записи `Static`.
Для того, что ответ от R2 дошёл до R1, необходимо настроить и обратный маршрут:

```text
[R2]ip route-static 10.0.1.1 32 10.0.12.1
```

```text
[R1]ping -a 10.0.1.1 10.0.1.2
  PING 10.0.1.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.1.2: bytes=56 Sequence=1 ttl=255 time=40 ms
    Reply from 10.0.1.2: bytes=56 Sequence=2 ttl=255 time=20 ms
    Reply from 10.0.1.2: bytes=56 Sequence=3 ttl=255 time=10 ms
    Reply from 10.0.1.2: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.1.2: bytes=56 Sequence=5 ttl=255 time=20 ms

  --- 10.0.1.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 10/22/40 ms
```

Связь между LoopBack0 R1 и R2 установлена.
Затем добавим остальные маршруты:

```text
[R2]ip route-static 10.0.1.3 32 10.0.23.3
[R3]ip route-static 10.0.1.1 32 10.0.13.1
[R3]ip route-static 10.0.1.2 32 10.0.23.2
```

```text
[R1]ping -a 10.0.1.1 10.0.1.3
  PING 10.0.1.3: 56 data bytes, press CTRL_C to break
    Reply from 10.0.1.3: bytes=56 Sequence=1 ttl=255 time=50 ms
    Reply from 10.0.1.3: bytes=56 Sequence=2 ttl=255 time=20 ms
    Reply from 10.0.1.3: bytes=56 Sequence=3 ttl=255 time=20 ms
    Reply from 10.0.1.3: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.1.3: bytes=56 Sequence=5 ttl=255 time=10 ms

  --- 10.0.1.3 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 10/24/50 ms
```

Связь между LoopBack0 R1 и R3 также подтверждена.

== Шаг 6. Настройка и проверка резервного маршрута

На R1 и R2 настроены резервные маршруты через R3:

```text
[R1]ip route-static 10.0.1.2 32 10.0.13.3 preference 100
[R2]ip route-static 10.0.1.1 32 10.0.23.3 preference 100
```

Основные маршруты имеют предпочтение 60, резервные - 100.
Для одинакового префикса предпочтительно меньшее значение.

=== До отключения прямого канала

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 13        Routes : 13

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.2/32          Static  60   0     RD     10.0.12.2      GigabitEthernet0/0/0
10.0.1.3/32          Static  60   0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.12.0/24         Direct  0    0     D      10.0.12.1      GigabitEthernet0/0/0
10.0.12.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

```text
[R2]display ip routing-tabl
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 13        Routes : 13

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Static  60   0     RD     10.0.12.1      GigabitEthernet0/0/0
10.0.1.2/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.3/32          Static  60   0     RD     10.0.23.3      GigabitEthernet0/0/1
10.0.12.0/24         Direct  0    0     D      10.0.12.2      GigabitEthernet0/0/0
10.0.12.2/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.23.0/24         Direct  0    0     D      10.0.23.2      GigabitEthernet0/0/1
10.0.23.2/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.23.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

В активных таблицах остаются основные маршруты через сеть 10.0.12.0/24.
Резервные маршруты настроены, но пока не выбраны, поэтому их нет в таблице.

=== Отключение канала R1–R2

В режиме настройки GE0/0/0 на обоих маршрутизаторах выполнена команда `shutdown`, отключающая интерфейс:

```text
[R1-GigabitEthernet0/0/0]shutdown
[R2-GigabitEthernet0/0/0]shutdown
```

```text
[R1-GigabitEthernet0/0/0]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 10        Routes : 10

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.2/32          Static  100  0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.1.3/32          Static  60   0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

```text
[R2-GigabitEthernet0/0/0]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 10        Routes : 10

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Static  100  0     RD     10.0.23.3      GigabitEthernet0/0/1
10.0.1.2/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.3/32          Static  60   0     RD     10.0.23.3      GigabitEthernet0/0/1
10.0.23.0/24         Direct  0    0     D      10.0.23.2      GigabitEthernet0/0/1
10.0.23.2/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.23.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Маршруты сети 10.0.12.0/24 исчезли. Основные статические маршруты стали неактивными, вместо них выбраны резервные с предпочтением 100.

#table(
  columns: (auto, auto, auto, auto), inset: 5pt,
  table.header([*Узел*], [*Назначение*], [*До: NextHop / Pre*], [*После: NextHop / Pre*]),
  [R1], [10.0.1.2/32], [10.0.12.2 / 60], [10.0.13.3 / 100],
  [R2], [10.0.1.1/32], [10.0.12.1 / 60], [10.0.23.3 / 100],
)

=== Проверка резервного пути

```text
[R1]ping -a 10.0.1.1 10.0.1.2
  PING 10.0.1.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.1.2: bytes=56 Sequence=1 ttl=254 time=30 ms
    Reply from 10.0.1.2: bytes=56 Sequence=2 ttl=254 time=30 ms
    Reply from 10.0.1.2: bytes=56 Sequence=3 ttl=254 time=30 ms
    Reply from 10.0.1.2: bytes=56 Sequence=4 ttl=254 time=40 ms
    Reply from 10.0.1.2: bytes=56 Sequence=5 ttl=254 time=40 ms

  --- 10.0.1.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 30/34/40 ms
```

Связь сохраняется без потерь. Команда `tracert` показывает промежуточные переходы; параметр `-a` задаёт исходный адрес:

```text
[R1]tracert -a 10.0.1.1 10.0.1.2
traceroute to 10.0.1.2(10.0.1.2), max hops: 30,
packet length: 40,press CTRL_C to break

1 10.0.13.3 40 ms 10 ms 20 ms
2 10.0.23.2 30 ms 30 ms 30 ms
```

Первый переход - R3 (10.0.13.3), второй - R2 (10.0.23.2).
Трассировка подтверждает использование пути R1–R3–R2.

== Шаг 7. Настройка маршрута по умолчанию

На R1 выполнено включение GE0/0/0 командой `undo shutdown` и удаление основного и резервного маршрутов к 10.0.1.2/32:

```text
[R1]interface GigabitEthernet 0/0/0
[R1-GigabitEthernet0/0/0]undo shutdown
[R1-GigabitEthernet0/0/0]quit
[R1]undo ip route-static 10.0.1.2 255.255.255.255 10.0.13.3 preference 100
```

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 9        Routes : 9

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.3/32          Static  60   0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Настроим маршрут по умолчанию через R2. Префикс 0.0.0.0/0 применяется, когда для адреса назначения нет более специфичного маршрута:

```text
[R1]ip route-static 0.0.0.0 0 10.0.12.2
[R1]ip ro 0.0.0.0 0 10.0.12.2
```

Для восстановления прямого канала включим также интерфейс на стороне R2:

```text
[R2]interface GigabitEthernet 0/0/0
[R2-GigabitEthernet0/0/0]undo shutdown
```

После включения интерфейса на R1 получена следующая таблица маршрутизации:

```text
[R1]display ip routing-table
Route Flags: R - relay, D - download to fib
----------------------------------------------------------------------------------------------

Routing Tables: Public
Destinations : 13        Routes : 13

Destination/Mask     Proto   Pre  Cost  Flags  NextHop        Interface
0.0.0.0/0            Static  60   0     RD     10.0.12.2      GigabitEthernet0/0/0
10.0.1.1/32          Direct  0    0     D      127.0.0.1      LoopBack0
10.0.1.3/32          Static  60   0     RD     10.0.13.3      GigabitEthernet0/0/1
10.0.12.0/24         Direct  0    0     D      10.0.12.1      GigabitEthernet0/0/0
10.0.12.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.12.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/0
10.0.13.0/24         Direct  0    0     D      10.0.13.1      GigabitEthernet0/0/1
10.0.13.1/32         Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
10.0.13.255/32       Direct  0    0     D      127.0.0.1      GigabitEthernet0/0/1
127.0.0.0/8          Direct  0    0     D      127.0.0.1      InLoopBack0
127.0.0.1/32         Direct  0    0     D      127.0.0.1      InLoopBack0
127.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
255.255.255.255/32   Direct  0    0     D      127.0.0.1      InLoopBack0
```

Запросы к 10.0.1.2 теперь направляются по маршруту по умолчанию.

```text
[R1]ping -a 10.0.1.1 10.0.1.2
  PING 10.0.1.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.1.2: bytes=56 Sequence=1 ttl=255 time=30 ms
    Reply from 10.0.1.2: bytes=56 Sequence=2 ttl=255 time=10 ms
    Reply from 10.0.1.2: bytes=56 Sequence=3 ttl=255 time=30 ms
    Reply from 10.0.1.2: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.1.2: bytes=56 Sequence=5 ttl=255 time=20 ms

  --- 10.0.1.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 10/22/30 ms
```

Связь между LoopBack0 R1 и R2 с использованием маршрута по умолчанию на R1 подтверждена.

= Вывод

Настроены IPv4-адреса, статические маршруты и маршрут по умолчанию.
Проверки ping подтвердили связь между loopback-интерфейсами R1 и R2/R3.
При отключении канала R1–R2 связь сохранилась через R3,
что подтверждено трассировкой.
