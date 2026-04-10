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

#let work_title = "Дисциплина «Компьютерные сети»"

#let student_name = "*"
#let group = "Группа P3311"

#let teacher_name = "Зинатулин Артём Витальевич"

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

== Вариант

- класс A: `18.17.24.13`
- класс B: `145.24.18.13`
- класс C: `216.18.17.13`

```
N1 = 3, N2 = 3, N3 = 4, класс C
```

\
== Этап 1. Локальная сеть с концентратором (Сеть 1)

\
=== Выполнение

В среде `Cisco Packet Tracer` была построена простейшая модель сети, состоящая из трёх маршрутизаторов и хаба

#figure(
  image("stage1_scheme.png"),
  caption: [Схема сети]
)

На интерфейсах маршрутизаторов были назначены IP-адреса одной подсети:

- `R1`: `216.18.17.13/24`
- `R2`: `216.18.17.14/24`
- `R3`: `216.18.17.15/24`

После назначения адресов и активации интерфейсов была проверена связность между всеми узлами сети. Так как все маршрутизаторы подключены к одному концентратору и находятся в одной подсети, обмен ICMP-пакетами выполняется напрямую

Проверка с `R1`:

```text
Router#ping 216.18.17.14

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.14, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.17.15

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.15, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms
```

Проверка с `R2`:

```text
Router#ping 216.18.17.13

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.13, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.17.15

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.15, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms
```

Проверка с `R3`:

```text
Router#ping 216.18.17.13

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.13, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.17.14

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.14, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms
```

=== Анализ таблиц маршрутизации

Так как все три маршрутизатора находятся в одной подсети `216.18.17.0/24` и подключены к общему концентратору, в таблицах маршрутизации присутствуют только записи о непосредственно подключённой сети и локальном адресе интерфейса. Дополнительные маршруты не требуются.

Таблица маршрутизации `R1` (`216.18.17.13`):

```text
Routing Table for 216.18.17.13

Type  Network          Port               Next Hop IP  Metric
C     216.18.17.0/24   GigabitEthernet0/0 ---          0/0
L     216.18.17.13/32  GigabitEthernet0/0 ---          0/0
```

Запись `C` означает, что сеть `216.18.17.0/24` подключена непосредственно к интерфейсу `GigabitEthernet0/0`. Запись `L` указывает на собственный адрес маршрутизатора `216.18.17.13/32`, назначенный на тот же интерфейс.

Таблица маршрутизации `R2` (`216.18.17.14`):

```text
Routing Table for 216.18.17.14

Type  Network          Port               Next Hop IP  Metric
C     216.18.17.0/24   GigabitEthernet0/0 ---          0/0
L     216.18.17.14/32  GigabitEthernet0/0 ---          0/0
```

Для `R2` таблица имеет ту же структуру. Это показывает, что маршрутизатор видит только локальный сегмент и собственный адрес, а поле `Next Hop IP` остаётся пустым, так как пересылка в пределах данной сети происходит без промежуточного узла.

Таблица маршрутизации `R3` (`216.18.17.15`):

```text
Routing Table for 216.18.17.15

Type  Network          Port               Next Hop IP  Metric
C     216.18.17.0/24   GigabitEthernet0/0 ---          0/0
L     216.18.17.15/32  GigabitEthernet0/0 ---          0/0
```

Аналогично, `R3` содержит только маршрут к общей сети и маршрут к собственному интерфейсу

=== Анализ ARP-таблицы

Содержимое ARP-таблиц на маршрутизаторах после обмена пакетами показано ниже.

ARP-таблица `R1` (`216.18.17.13`):

```text
ARP Table for 216.18.17.13

IP Address    Hardware Address  Interface
216.18.17.13  00E0.A311.B301    GigabitEthernet0/0
216.18.17.14  0001.966D.5201    GigabitEthernet0/0
```

В таблице `R1` присутствует запись для собственного интерфейса `216.18.17.13`, связанная с MAC-адресом `00E0.A311.B301`.

ARP-таблица `R2` (`216.18.17.14`):

```text
ARP Table for 216.18.17.14

IP Address    Hardware Address  Interface
216.18.17.14  0001.966D.5201    GigabitEthernet0/0
216.18.17.15  0001.96C4.1901    GigabitEthernet0/0
```

В таблице `R2` содержатся записи для собственного адреса и для узла `R3`, что подтверждает разрешение MAC-адресов в пределах локального сегмента.

ARP-таблица `R3` (`216.18.17.15`):

```text
ARP Table for 216.18.17.15

IP Address    Hardware Address  Interface
216.18.17.14  0001.966D.5201    GigabitEthernet0/0
216.18.17.15  0001.96C4.1901    GigabitEthernet0/0
```

Аналогично, на `R3` имеются записи для собственного интерфейса и для адреса `R2`. Наличие ARP-записей показывает, что устройства успешно определяют MAC-адреса соседей при обмене кадрами внутри одной локальной сети.

=== Тестирование передачи UDP

Для дополнительной проверки в режиме `Simulation` был отправлен UDP-пакет с помощью `Complex PDU`. Пакет был успешно передан внутри локальной сети через концентратор и достиг узла назначения.

#figure(
  image("stage1_complex_pdu.png"),
  caption: [Передача UDP-пакета в режиме Simulation]
)

#figure(
  image("stage1_udp_osi.png"),
  caption: [Стек протоколов UDP-пакета]
)

На транспортном уровне в составе PDU присутствует заголовок `UDP`, а на канальном уровне пакет инкапсулируется в Ethernet-кадр и передаётся всем устройствам сегмента. Концентратор не выполняет маршрутизацию или фильтрацию, поэтому кадр ретранслируется на все порты, а обрабатывается только устройством с нужным IP-адресом.



\
== Этап 2. Локальная сеть с коммутатором (Сеть 2)

=== Выполнение

На втором этапе концентратор был заменён на коммутатор. Все три маршрутизатора по-прежнему находятся в одной подсети, однако теперь передача кадров выполняется не всем узлам сразу, а через таблицу коммутации свитча.

#figure(
  image("stage2_scheme.png"),
  caption: [Схема сети с коммутатором]
)

=== Таблица коммутации

После первых кадров коммутатор заполняет таблицу коммутации, в которой хранится соответствие `MAC`-адресов и портов. Благодаря этому свитч при известном адресе назначения пересылает кадр только на нужный порт.

#figure(
  image("stage2_mac_table.png"),
  caption: [Таблица коммутации коммутатора]
)

По данной схеме видно, что коммутатор запомнил адреса всех подключённых устройств и связал каждый `MAC` с отдельным интерфейсом. Основное отличие от концентратора: после обучения свитч выполняет адресную, а не широковещательную передачу кадров.

=== Таблица маршрутизации

Так как все маршрутизаторы остаются в сети `216.18.17.0/24`, таблица маршрутизации содержит только запись о непосредственно подключённой сети и запись о локальном адресе интерфейса.

#figure(
  image("stage2_routing_table.png"),
  caption: [Таблица маршрутизации маршрутизатора]
)

Маршрутизация на этом этапе не изменилась по сравнению с первым: пакеты передаются внутри одной локальной сети без дополнительных маршрутов и без следующего перехода.

=== ARP-таблица

ARP-таблица формируется после обмена кадрами и содержит соответствия между `IP`-адресами и `MAC`-адресами узлов локального сегмента.

#figure(
  image("stage2_arp_table.png"),
  caption: [ARP-таблица маршрутизатора]
)

Для отчёта достаточно рассмотреть один узел: наличие записей в `ARP` подтверждает, что маршрутизатор успешно определил канальные адреса соседей.

=== Тестирование передачи UDP

Передача UDP-пакета была проверена в режиме `Simulation` с использованием `Complex PDU`.

#figure(
  image("stage2_udp_osi.png"),
  caption: [Стек протоколов UDP-пакета]
)

На транспортном уровне в составе пакета присутствует заголовок `UDP`. В отличие от этапа с концентратором, коммутатор направляет Ethernet-кадр только на тот порт, за которым находится устройство назначения.

\
== Этап 3. Многосегментная локальная сеть (Сеть 3)

==== Построение сети

==== Настройка сети

На интерфейсах
=== Этап 2. 3 устройства

==== Построение сети


==== Выполнение

Подключим аналогично `R3` и подключим его к `GE0/1` роутера `R2`. Настроим `R2`:

```
Router#configure terminal 
Router(config)#interface GigabitEthernet 0/1
Router(config-if)#ip address 216.18.17.15 255.255.255.0
% 216.18.17.0 overlaps with GigabitEthernet0/0
```

Нужно назначить на другой интерфейс:

```
Router#configure terminal 
Router(config)#interface GigabitEthernet 0/1
Router(config-if)#ip address 216.18.18.1 255.255.255.0
Router(config-if)#no shutdown 
Router(config-if)#end
```

Теперь для `R3`:

```
Router#configure terminal 
Router(config)#interface GigabitEthernet 0/0
Router(config-if)#ip address 216.18.18.2 255.255.255.0
Router(config-if)#no shutdown 
Router(config-if)#end
```

На `R1`:
```
Router#ping 216.18.18.1

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.18.1, timeout is 2 seconds:
.....
Success rate is 0 percent (0/5)
```

Ничего не получилось

Потому что теперь нужна маршрутизация. На `R1`:

```
Router#configure terminal 
Enter configuration commands, one per line.  End with CNTL/Z.
Router(config)#ip route 216.18.18.0 255.255.255.0 216.18.17.14
Router(config)#end
Router#
%SYS-5-CONFIG_I: Configured from console by console

Router#
Router#show ip route
Codes: L - local, C - connected, S - static, R - RIP, M - mobile, B - BGP
       D - EIGRP, EX - EIGRP external, O - OSPF, IA - OSPF inter area
       N1 - OSPF NSSA external type 1, N2 - OSPF NSSA external type 2
       E1 - OSPF external type 1, E2 - OSPF external type 2, E - EGP
       i - IS-IS, L1 - IS-IS level-1, L2 - IS-IS level-2, ia - IS-IS inter area
       * - candidate default, U - per-user static route, o - ODR
       P - periodic downloaded static route

Gateway of last resort is not set

     216.18.17.0/24 is variably subnetted, 2 subnets, 2 masks
C       216.18.17.0/24 is directly connected, GigabitEthernet0/0
L       216.18.17.13/32 is directly connected, GigabitEthernet0/0
S    216.18.18.0/24 [1/0] via 216.18.17.14
```

Буква `S` означает статическую маршрутизацию

На `R2` маршрутизация не нужна. На `R3`:

```
Router#configure terminal 
Enter configuration commands, one per line.  End with CNTL/Z.
Router(config)#ip route 216.18.17.0 255.255.255.0 216.18.18.1
Router(config)#end
Router#
%SYS-5-CONFIG_I: Configured from console by console

Router#
Router#ping 216.18.17.14

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.14, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.17.13

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.13, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#
```

Теперь попробуем с `R1`:

```
Router#ping 216.18.18.1

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.18.1, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.18.2

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.18.2, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms
```

==== Тестирование передачи UDP


=== Этап 3. 3 устройства в полносвязной сети

==== Построение сети


==== Конфигурация сети

Объединим `R1` и `R3` в сеть `216.18.19.0`

Теперь `R1` и `R3` могут пинговать друг друга. Нужно настроить ещё таблицу маршрутизации для `R2`:

```
Router#configure terminal 
Enter configuration commands, one per line.  End with CNTL/Z.
Router(config)#ip route 216.18.19.0 255.255.255.0 216.18.17.13
Router(config)#ip route 216.18.19.0 255.255.255.0 216.18.18.2
Router(config)#end
```

Теперь маршрутизация работает и для `R2`:

```
Router#ping 216.18.19.2

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.19.2, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.19.1

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.19.1, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#ping 216.18.19.2

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.19.2, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms

Router#
```

==== Тестирование передачи UDP


== Вывод
