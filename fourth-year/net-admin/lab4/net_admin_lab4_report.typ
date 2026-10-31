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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №4]
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

Настройка FTP.
R1 — FTP-клиент, R2 — FTP-сервер.
Вместо GE0/0/3 используются GE0/0/0.

#figure(
  image("img/topology.png", width: 100%),
  caption: [Топология сети: R1 — FTP-клиент, R2 — FTP-сервер],
)

#table(
  columns: (auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*Интерфейс*], [*IP-адрес/маска*]),
  [R1], [GE0/0/0], [10.0.12.1/24],
  [R2], [GE0/0/0], [10.0.12.2/24],
)

= Ход работы

== Шаг 1. Основные параметры и сохранение тестовых файлов

R1:

```text
<Huawei>sys
[Huawei]sysname R1
[R1]int g0/0/0
[R1-GigabitEthernet0/0/0]ip ad 10.0.12.1 24
[R1-GigabitEthernet0/0/0]return
<R1>save test1.cfg
 Are you sure to save the configuration to test1.cfg? (y/n)[n]:y
 Configuration file had been saved successfully
 Note: The configuration file will take effect after being activated
<R1>dir
Directory of flash:/

  Idx  Attr     Size(Byte)  Date        Time(LMT)  FileName
    0  drw-              -  Oct 27 2026 21:09:11   dhcp
    1  -rw-        121,802  May 26 2014 09:20:58   portalpage.zip
    2  -rw-          2,263  Oct 27 2026 21:09:05   statemach.efs
    3  -rw-        828,482  May 26 2014 09:20:58   sslvpn.zip
    4  -rw-            249  Oct 27 2026 21:11:03   private-data.txt
    5  -rw-            846  Oct 27 2026 21:11:02   test1.cfg

1,090,732 KB total (784,448 KB free)
```

R2:

```text
<Huawei>sys
[Huawei]sysname R2
[R2]int g0/0/0
[R2-GigabitEthernet0/0/0]ip ad 10.0.12.2 24
[R2-GigabitEthernet0/0/0]return
<R2>save test2.cfg
 Are you sure to save the configuration to test2.cfg? (y/n)[n]:y
 Configuration file had been saved successfully
 Note: The configuration file will take effect after being activated
<R2>dir
Directory of flash:/

  Idx  Attr     Size(Byte)  Date        Time(LMT)  FileName
    0  drw-              -  Oct 27 2026 21:09:15   dhcp
    1  -rw-        121,802  May 26 2014 09:20:58   portalpage.zip
    2  -rw-          2,263  Oct 27 2026 21:09:09   statemach.efs
    3  -rw-        828,482  May 26 2014 09:20:58   sslvpn.zip
    4  -rw-            249  Oct 27 2026 21:11:21   private-data.txt
    5  -rw-            846  Oct 27 2026 21:11:21   test2.cfg

1,090,732 KB total (784,448 KB free)
```

Проверка связи:

```text
<R1>ping 10.0.12.2
  PING 10.0.12.2: 56 data bytes, press CTRL_C to break
    Reply from 10.0.12.2: bytes=56 Sequence=1 ttl=255 time=200 ms
    Reply from 10.0.12.2: bytes=56 Sequence=2 ttl=255 time=30 ms
    Reply from 10.0.12.2: bytes=56 Sequence=3 ttl=255 time=30 ms
    Reply from 10.0.12.2: bytes=56 Sequence=4 ttl=255 time=20 ms
    Reply from 10.0.12.2: bytes=56 Sequence=5 ttl=255 time=30 ms

  --- 10.0.12.2 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 20/62/200 ms
```

Связь установлена без потерь. Файлы test1.cfg на R1 и test2.cfg на R2
сохранены во flash:/; размер каждого — 846 байт.


== Шаг 2. Включение FTP-сервера

```text
[R2]ftp server enable
Info: Succeeded in starting the FTP server
```

== Шаг 3. Настройка пользователя FTP

В используемой версии VRP пароль задан с параметром `cipher`.

```text
[R2]aaa
[R2-aaa]local-user ftp-client password cipher Huawei@123
Info: Add a new user.
[R2-aaa]local-user ftp-client service-type ftp
[R2-aaa]local-user ftp-client privilege level 15
[R2-aaa]local-user ftp-client ftp-directory flash:/
```

== Шаг 4. Подключение к FTP-серверу

```text
<R1>ftp 10.0.12.2
Trying 10.0.12.2 ...

Press CTRL+K to abort
Connected to 10.0.12.2.
220 FTP service ready.
User(10.0.12.2:(none)):ftp-client
331 Password required for ftp-client.
Enter password:
230 User logged in.

[R1-ftp]
```

Соединение установлено, пользователь успешно аутентифицирован.

== Шаг 5. Передача и удаление файлов

```text
[R1-ftp]ascii
200 Type set to A.

[R1-ftp]get test2.cfg
200 Port command okay.
150 Opening ASCII mode data connection for test2.cfg.
226 Transfer complete.
FTP: 846 byte(s) received in 0.290 second(s) 2.91Kbyte(s)/sec.

[R1-ftp]delete test2.cfg
Warning: The contents of file test2.cfg cannot be recycled. Continue? (y/n)[n]:y
250 DELE command successful.

[R1-ftp]put test1.cfg
200 Port command okay.
150 Opening ASCII mode data connection for test1.cfg.
100%
226 Transfer complete.
FTP: 846 byte(s) sent in 0.300 second(s) 2.82Kbyte(s)/sec.

[R1-ftp]bye
221 Server closing.
```

В режиме ASCII файл test2.cfg скачан с R2 на R1, затем удалён на R2.
Файл test1.cfg передан с R1 на R2. Каждая передача составила 846 байт.

== Проверка файловых каталогов

```text
<R1>dir
Directory of flash:/

  Idx  Attr     Size(Byte)  Date        Time(LMT)  FileName
    0  drw-              -  Oct 27 2026 21:09:11   dhcp
    1  -rw-        121,802  May 26 2014 09:20:58   portalpage.zip
    2  -rw-          2,263  Oct 27 2026 21:09:05   statemach.efs
    3  -rw-        828,482  May 26 2014 09:20:58   sslvpn.zip
    4  -rw-            249  Oct 27 2026 21:11:03   private-data.txt
    5  -rw-            846  Oct 27 2026 21:19:04   test2.cfg
    6  -rw-            846  Oct 27 2026 21:11:02   test1.cfg

1,090,732 KB total (784,452 KB free)
```

```text
<R2>dir
Directory of flash:/

  Idx  Attr     Size(Byte)  Date        Time(LMT)  FileName
    0  drw-              -  Oct 27 2026 21:09:15   dhcp
    1  -rw-        121,802  May 26 2014 09:20:58   portalpage.zip
    2  -rw-          2,263  Oct 27 2026 21:09:09   statemach.efs
    3  -rw-        828,482  May 26 2014 09:20:58   sslvpn.zip
    4  -rw-            249  Oct 27 2026 21:11:21   private-data.txt
    5  -rw-            846  Oct 27 2026 21:19:39   test1.cfg

1,090,732 KB total (784,452 KB free)
```

#table(
  columns: (auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*test1.cfg*], [*test2.cfg*]),
  [R1], [846 байт], [846 байт],
  [R2], [846 байт], [Отсутствует],
)

Каталоги подтверждают успешное скачивание, удаление и выгрузку файлов.


= Вывод

На R2 настроены FTP-сервер и локальный пользователь. С R1 выполнены
вход, скачивание, удаление и выгрузка конфигурационных файлов.
Успешное выполнение операций подтверждено ответами FTP-сервера
и итоговыми списками файлов.
