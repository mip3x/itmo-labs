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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №1]
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

= Лабораторная работа №1

== Условие

Вариант: `216.18.17.13`

=== Этап 1. Модель простейшей сети из двух компьютеров

*Цель этапа:* изучить принципы построения и настройки простейшей модели
компьютерной сети, а также проанализировать передачу данных между узлами.

В рамках этапа необходимо:

1. *Построение сети*

  - Построить модель сети из двух компьютеров
  - Соединить компьютеры сетевым кабелем
  - Назначить каждому компьютеру уникальный идентификатор
  - Настроить отображение IP- и MAC-адресов узлов (при необходимости)
  - Ознакомиться с журналами и параметрами устройств

2. *Настройка сети*

  - Настроить сетевые интерфейсы компьютеров, назначив им IP-адреса
    из заданного пула адресов
  - Использовать маску подсети, соответствующую классу C
  - Проанализировать служебные сообщения, передаваемые после назначения IP-адресов,
    и определить их назначение

3. *Анализ таблиц*

  - Проанализировать таблицы маршрутизации компьютеров
  - Проанализировать ARP-таблицы узлов
  - Определить, какая информация содержится в таблицах и каким образом
    формируются записи

4. *Тестирование сети*

  - Выполнить передачу данных между компьютерами
  - Проанализировать последовательность передаваемых пакетов и кадров
  - Определить, какая информация содержится в передаваемых пакетах
  - Зафиксировать изменения в ARP- и маршрутных таблицах

== Выполнение

=== Этап 1. Модель простейшей сети

==== Построение сети

В среде `Cisco Packet Tracer` была построена простейшая модель сети, состоящая из двух узлов. В качестве узлов использовались маршрутизаторы `R1` и `R2`, соединённые напрямую через интерфейсы `GigabitEthernet0/0`.

#figure(
  image("scheme_stage1.png"),
  caption: [Схема сети]
)

==== Настройка сети

На интерфейсах маршрутизаторов были назначены IP-адреса:

- `R1`: `216.18.17.13/24`
- `R2`: `216.18.17.14/24`

Интерфейсы (`14` в рассматриваемом примере) были активированы следующей последовательностью команд:

```
Router#enable
Router#configure terminal
Enter configuration commands, one per line.  End with CNTL/Z.
Router(config)#interface GigabitEthernet 0/0
Router(config)#interface GigabitEthernet 0/0
Router(config-if)#ip address 216.18.17.14 255.255.255.0
Router(config-if)#no shutdown 
Router(config-if)#end
```

==== Проверка связи

Для проверки работоспособности сети была выполнена передача пакетов с использованием команды `ping`. Результаты показали успешную доставку
пакетов между узлами сети

#block[
```text
Router#ping 216.18.17.14

Type escape sequence to abort.
Sending 5, 100-byte ICMP Echos to 216.18.17.14, timeout is 2 seconds:
!!!!!
Success rate is 100 percent (5/5), round-trip min/avg/max = 0/0/0 ms
```
]

==== Анализ таблицы маршрутизации

#block[
```text
Router>show ip route
Codes: L - local, C - connected, S - static, R - RIP, M - mobile, B - BGP
D - EIGRP, EX - EIGRP external, O - OSPF, IA - OSPF inter area
N1 - OSPF NSSA external type 1, N2 - OSPF NSSA external type 2
E1 - OSPF external type 1, E2 - OSPF external type 2, E - EGP
i - IS-IS, L1 - IS-IS level-1, L2 - IS-IS level-2, ia - IS-IS inter area
* - candidate default, U - per-user static route, o - ODR
P - periodic downloaded static route

Gateway of last resort is not set

216.18.17.0/24 is variably subnetted, 2 subnets, 2 masks
C 216.18.17.0/24 is directly connected, GigabitEthernet0/0
L 216.18.17.13/32 is directly connected, GigabitEthernet0/0
```
]

Анализ таблицы маршрутизации показал наличие записи о непосредственно
подключённой сети:

- C 216.18.17.0/24: непосредственно подключённая сеть  
- L 216.18.17.14/32: локальный адрес интерфейса  

Это означает, что передача пакетов осуществляется напрямую через подключённый интерфейс без использования дополнительных маршрутов.

==== Анализ ARP-таблицы

#block[
```text
Router>show ip arp
Protocol Address Age (min) Hardware Addr Type Interface
Internet 216.18.17.13 - 00D0.D3CC.D701 ARPA GigabitEthernet0/0
Internet 216.18.17.14 12 0000.0CD2.A101 ARPA GigabitEthernet0/0

Анализ ARP-таблицы показал наличие динамических записей, содержащих соответствие IP-адресов и MAC-адресов узлов сети. Записи формируются
автоматически в процессе обмена пакетами
```
]

==== Тестирование передачи UDP

Передача данных была протестирована в режиме Simulation среды Cisco Packet Tracer с использованием Complex PDU. Был отправлен UDP-пакет от
маршрутизатора R1 к маршрутизатору R2

Анализ показал, что пакет успешно достиг узла назначения, что подтверждается наличием заголовка UDP на транспортном уровне и корректной доставкой кадра на интерфейс GigabitEthernet0/0

Ответный пакет от R2 не формировался, поскольку протокол UDP не требует обязательного подтверждения доставки, а на принимающем узле отсутствует приложение, использующее указанный порт

#figure(
  image("stage1_udp.png"),
  caption: [Передача UDP-пакета]
)

=== Этап 2. 3 устройства

==== Построение сети

#figure(
  image("scheme_stage1.png"),
  caption: [Схема сети]
)

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

#figure(
  image("stage2_udp.png"),
  caption: [Передача UDP-пакета]
)

=== Этап 3. 3 устройства в полносвязной сети

==== Построение сети

#figure(
  image("scheme_stage3.png"),
  caption: [Схема сети]
)

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

#figure(
  image("stage3_udp.png"),
  caption: [Передача UDP-пакета]
)

== Вывод

В ходе выполнения лабораторной работы были изучены принципы построения и настройки компьютерных сетей различной топологии в среде моделирования Cisco Packet Tracer.

В первом этапе была построена простейшая сеть из двух маршрутизаторов. Выполнена настройка IP-адресов из пула 216.18.17.0/24 и проверка связности с помощью команды ping. Анализ таблиц маршрутизации показал наличие записей о непосредственно подключённых сетях, а ARP-таблиц — динамическое формирование соответствий IP и MAC-адресов в процессе обмена данными. Тестирование передачи UDP-пакетов подтвердило корректность настроек.

Во втором этапе была реализована линейная топология из трёх маршрутизаторов с использованием двух подсетей: 216.18.17.0/24 (между R1 и R2) и 216.18.18.0/24 (между R2 и R3). Для обеспечения связности между крайними узлами (R1 и R3) были добавлены статические маршруты. Анализ таблиц маршрутизации показал, что центральный узел (R2) имеет информацию об обеих сетях, в то время как крайние узлы знают только о своей сети и используют статические маршруты для достижения удалённых сетей. В процессе передачи данных было зафиксировано использование ARP-протокола для разрешения адресов перед отправкой ICMP-пакетов.

В третьем этапе сеть была преобразована в полносвязную топологию путём добавления прямого соединения между R1 и R3 с использованием третьей подсети 216.18.19.0/24. После добавления статических маршрутов на всех трёх маршрутизаторах была достигнута полная связность. Анализ таблиц маршрутизации показал, что у центрального узла (R2) появилось два альтернативных маршрута к сети 216.18.19.0/24 (через R1 и через R3), что демонстрирует возможность многопутевой маршрутизации.

