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

#let student1_name = "Иванов Иван Иванович"
#let student2_name = "Эээ Ууу Ыыы"

#let group = "Группа P3411"

#let teacher_name = "Афанасьев Дмитрий Борисович"

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

Настройка локального механизма AAA

#figure(
  image("img/topology.png", width: 100%),
  caption: [Топология сети для настройки локального AAA],
)

Вместо GE0/0/3 использованы GE0/0/0.

#table(
  columns: (auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*Интерфейс*], [*IP-адрес/маска*]),
  [R1], [GE0/0/0], [10.0.12.1/24],
  [R2], [GE0/0/0], [10.0.12.2/24],
)

= Ход работы

== Шаг 1. Имена устройств и настройка IP-адресов

R1:

```text
<Huawei>sys
Enter system view, return user view with Ctrl+Z.
[Huawei]sysname R1
[R1]int g0/0/0
[R1-GigabitEthernet0/0/0]ip ad 10.0.12.1 24
[R1-GigabitEthernet0/0/0]q
```

R2:

```text
<Huawei>sys
Enter system view, return user view with Ctrl+Z.
[Huawei]sysname R2
[R2]int g0/0/0
[R2-GigabitEthernet0/0/0]ip ad 10.0.12.2 24
[R2-GigabitEthernet0/0/0]q
```

Проверка состояния интерфейсов R1:

```text
[R1]dis ip int brief
*down: administratively down
^down: standby
(l): loopback
(s): spoofing
The number of interface that is UP in Physical is 2
The number of interface that is DOWN in Physical is 2
The number of interface that is UP in Protocol is 2
The number of interface that is DOWN in Protocol is 2

Interface                   IP Address/Mask     Physical   Protocol
GigabitEthernet0/0/0         10.0.12.1/24        up         up
GigabitEthernet0/0/1         unassigned          down       down
GigabitEthernet0/0/2         unassigned          down       down
NULL0                       unassigned          up         up(s)
```

Проверка связи R1–R2:

```text
[R1]ping 10.0.12.2
  PING 10.0.12.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.12.2: bytes=56 Sequence=1 ttl=255 time=330 ms
    Reply from 10.0.12.2: bytes=56 Sequence=2 ttl=255 time=30 ms
    Reply from 10.0.12.2: bytes=56 Sequence=3 ttl=255 time=30 ms
    Reply from 10.0.12.2: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.12.2: bytes=56 Sequence=5 ttl=255 time=30 ms

  --- 10.0.12.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 20/88/330 ms
```

Связь между маршрутизаторами подтверждена.

== Шаг 2. Настройка схем аутентификации и авторизации

На R2 созданы схемы локальной аутентификации и авторизации `datacom`.

```text
[R2]aaa
[R2-aaa]authentication-scheme datacom
Info: Create a new authentication scheme.
[R2-aaa-authen-datacom]authentication-mode local
[R2-aaa-authen-datacom]q
[R2-aaa]authorization-scheme datacom
Info: Create a new authorization scheme.
[R2-aaa-author-datacom]authorization-mode local
```

== Шаг 3. Создание домена

```text
[R2-aaa]domain datacom
Info: Success to create a new domain.
[R2-aaa-domain-datacom]authentication-scheme datacom
[R2-aaa-domain-datacom]q
```

```text
[R2]aaa
[R2-aaa]domain datacom
[R2-aaa-domain-datacom]authorization-scheme datacom
[R2-aaa-domain-datacom]q
```

== Шаг 4. Настройка локального пользователя

Создан пользователь домена `datacom` с доступом через Telnet и уровнем прав 3.

```text
[R2-aaa]local-user hcia@datacom password cipher HCIA-Datacom
Info: Add a new user.
[R2-aaa]local-user hcia@datacom service-type telnet
[R2-aaa]local-user hcia@datacom privilege level 3
```

== Шаг 5. Включение Telnet и аутентификации AAA

Telnet-сервер уже включён. Для линий VTY 0–4 задана аутентификация AAA.

```text
[R2]telnet server enable
Error: TELNET server has been enabled
[R2]user-interface vty 0 4
[R2-ui-vty0-4]authentication-mode aaa
[R2-ui-vty0-4]q
```

== Шаг 6. Проверка конфигурации

С R1 выполнен вход на R2 через Telnet с учётной записью домена `datacom`.

```text
<R1>telnet 10.0.12.2
  Press CTRL_] to quit telnet mode
  Trying 10.0.12.2 ...
  Connected to 10.0.12.2 ...

Login authentication

Username:hcia@datacom
Password:
<R2>dis users
  User-Intf    Delay    Type   Network Address     AuthenStatus    AuthorcmdFlag
  0   CON 0   00:02:39                                   pass
  Username : Unspecified

+ 129 VTY 0   00:00:00  TEL    10.0.12.1                 pass
  Username : hcia@datacom
```

Строка VTY 0 подтверждает успешный вход пользователя `hcia@datacom`
с R1 (10.0.12.1). Знак `+` отмечает текущую сессию.

= Вывод

На R2 настроены локальные схемы аутентификации и авторизации,
домен `datacom` и пользователь с уровнем прав 3. Успешный вход с R1
через Telnet и вывод `display users` подтвердили работу локального AAA.
