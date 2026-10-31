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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №2]
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
  image("img/topology.png", width: 100%),
  caption: [Топология сети и распределение устройств по VLAN],
)

На S3 и S4 указанные IP-адреса назначены интерфейсам Vlanif3.
Соединение S1–S2 — trunk для VLAN 2, 3 и 10.

= Ход работы

== Шаг 1. Имена устройств и отключение ненужных портов

S1:

```text
<Huawei>system-view
[Huawei]sysname S1
[S1]int g0/0/11
[S1-GigabitEthernet0/0/11]shutdown
[S1-GigabitEthernet0/0/11]q
[S1]int g0/0/12
[S1-GigabitEthernet0/0/12]shutdown
[S1-GigabitEthernet0/0/12]q
```

S2:

```text
<Huawei>system-view
[Huawei]sysname S2
[S2]int g0/0/11
[S2-GigabitEthernet0/0/11]shutdown
[S2-GigabitEthernet0/0/11]q
[S2]int g0/0/12
[S2-GigabitEthernet0/0/12]shutdown
[S2-GigabitEthernet0/0/12]q
```

== Шаг 2. Настройка IP-адресов

#table(
  columns: (auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*Интерфейс*], [*IP-адрес/маска*]),
  [R1], [GE0/0/1], [10.1.2.1/24],
  [R3], [GE0/0/2], [10.1.10.1/24],
  [S3], [Vlanif3], [10.1.3.1/24],
  [S4], [Vlanif3], [10.1.3.2/24],
)

R1:

```text
<Huawei>system-view
[Huawei]sysname R1
[R1]int g0/0/1
[R1-GigabitEthernet0/0/1]ip address 10.1.2.1 24
```

R3:

```text
<Huawei>system-view
[Huawei]sysname R3
[R3]int g0/0/2
[R3-GigabitEthernet0/0/2]ip address 10.1.10.1 24
```

На S3 и S4 использован сценарий 2: физические порты включены в VLAN 3,
IP-адреса назначены интерфейсам Vlanif3.

S3 (перед настройкой access-порта восстановлен режим L2 командой `portswitch`):

```text
[S3]vlan 3
[S3-vlan3]q
[S3]int g0/0/1
[S3-GigabitEthernet0/0/1]portswitch
[S3-GigabitEthernet0/0/1]port link-type access
[S3-GigabitEthernet0/0/1]port default vlan 3
[S3-GigabitEthernet0/0/1]q
[S3]int Vlanif 3
[S3-Vlanif3]ip address 10.1.3.1 24
```

S4:

```text
[S4]vlan 3
[S4-vlan3]q
[S4]int g0/0/2
[S4-GigabitEthernet0/0/2]portswitch
Error: The interface is already a L2 interface.
[S4-GigabitEthernet0/0/2]port link-type access
[S4-GigabitEthernet0/0/2]port default vlan 3
[S4-GigabitEthernet0/0/2]q
[S4]int Vlanif 3
[S4-Vlanif3]ip address 10.1.3.2 24
```

== Шаг 3. Создание VLAN

```text
[S1]vlan batch 2 to 3 10
Info: This operation may take a few seconds. Please wait for a moment...done.
[S2]vlan batch 2 to 3 10
Info: This operation may take a few seconds. Please wait for a moment...done.
```

```text
[S1]dis vlan
The total number of vlans is : 4
--------------------------------------------------------------------------------
U: Up;         D: Down;         TG: Tagged;         UT: Untagged;
MP: Vlan-mapping;               ST: Vlan-stacking;
#: ProtocolTransparent-vlan;    *: Management-vlan;
--------------------------------------------------------------------------------

VID  Type    Ports
--------------------------------------------------------------------------------
1    common  UT:GE0/0/1(U)     GE0/0/2(D)     GE0/0/3(D)     GE0/0/4(D)
                GE0/0/5(D)     GE0/0/6(D)     GE0/0/7(D)     GE0/0/8(D)
                GE0/0/9(D)     GE0/0/10(U)    GE0/0/11(D)    GE0/0/12(D)
                GE0/0/13(U)    GE0/0/14(D)    GE0/0/15(D)    GE0/0/16(D)
                GE0/0/17(D)    GE0/0/18(D)    GE0/0/19(D)    GE0/0/20(D)
                GE0/0/21(D)    GE0/0/22(D)    GE0/0/23(D)    GE0/0/24(D)

2    common
3    common
10   common

VID  Status  Property      MAC-LRN Statistics Description
--------------------------------------------------------------------------------
1    enable  default       enable  disable    VLAN 0001
2    enable  default       enable  disable    VLAN 0002
3    enable  default       enable  disable    VLAN 0003
10   enable  default       enable  disable    VLAN 0010
```

```text
[S2]dis vlan
The total number of vlans is : 4
--------------------------------------------------------------------------------
U: Up;         D: Down;         TG: Tagged;         UT: Untagged;
MP: Vlan-mapping;               ST: Vlan-stacking;
#: ProtocolTransparent-vlan;    *: Management-vlan;
--------------------------------------------------------------------------------

VID  Type    Ports
--------------------------------------------------------------------------------
1    common  UT:GE0/0/1(U)     GE0/0/2(D)     GE0/0/3(D)     GE0/0/4(D)
                GE0/0/5(D)     GE0/0/6(D)     GE0/0/7(D)     GE0/0/8(D)
                GE0/0/9(D)     GE0/0/10(U)    GE0/0/11(D)    GE0/0/12(D)
                GE0/0/13(D)    GE0/0/14(U)    GE0/0/15(D)    GE0/0/16(D)
                GE0/0/17(D)    GE0/0/18(D)    GE0/0/19(D)    GE0/0/20(D)
                GE0/0/21(D)    GE0/0/22(D)    GE0/0/23(D)    GE0/0/24(D)

2    common
3    common
10   common

VID  Status  Property      MAC-LRN Statistics Description
--------------------------------------------------------------------------------
1    enable  default       enable  disable    VLAN 0001
2    enable  default       enable  disable    VLAN 0002
3    enable  default       enable  disable    VLAN 0003
10   enable  default       enable  disable    VLAN 0010
```

VLAN 2, 3 и 10 созданы на обоих коммутаторах; порты им пока не назначены.
VLAN 1 существует по умолчанию и на данном этапе содержит все порты.


== Шаг 4. Настройка VLAN на основе портов

#table(
  columns: (auto, auto, auto, auto), inset: 5pt,
  table.header([*Устройство*], [*Порт*], [*Тип*], [*VLAN*]),
  [S1], [GE0/0/1], [access], [2],
  [S1], [GE0/0/13], [access], [3],
  [S2], [GE0/0/14], [access], [3],
  [S1, S2], [GE0/0/10], [trunk], [2, 3],
)

S1:

```text
[S1]int g0/0/1
[S1-GigabitEthernet0/0/1]port link-type access
[S1-GigabitEthernet0/0/1]port default vlan 2
[S1-GigabitEthernet0/0/1]q
[S1]int g0/0/13
[S1-GigabitEthernet0/0/13]port link-type access
[S1-GigabitEthernet0/0/13]port default vlan 3
[S1-GigabitEthernet0/0/13]q
[S1]int g0/0/10
[S1-GigabitEthernet0/0/10]port link-type trunk
[S1-GigabitEthernet0/0/10]port trunk allow-pass vlan 2 3
[S1-GigabitEthernet0/0/10]undo port trunk allow-pass vlan 1
```

S2:

```text
[S2]int g0/0/14
[S2-GigabitEthernet0/0/14]port link-type access
[S2-GigabitEthernet0/0/14]port default vlan 3
[S2-GigabitEthernet0/0/14]q
[S2]int g0/0/10
[S2-GigabitEthernet0/0/10]port link-type trunk
[S2-GigabitEthernet0/0/10]port trunk allow-pass vlan 2 3
[S2-GigabitEthernet0/0/10]undo port trunk allow-pass vlan 1
```


== Шаг 5. Настройка VLAN на основе MAC-адресов

Первоначально к VLAN 10 привязан адрес из методички `a008-6fe1-0c46`.
Исправление на фактический MAC-адрес R3 приведено в шаге 6.

```text
[S2]vlan 10
[S2-vlan10]mac-vlan mac-address a008-6fe1-0c46
[S2-vlan10]q
```

Порты S2 GE0/0/1–GE0/0/3 настроены как hybrid с передачей кадров VLAN 10 без тега.

```text
[S2]int g0/0/1
[S2-GigabitEthernet0/0/1]port link-type hybrid
[S2-GigabitEthernet0/0/1]port hybrid untagged vlan 10
[S2-GigabitEthernet0/0/1]q
[S2]int g0/0/2
[S2-GigabitEthernet0/0/2]port link-type hybrid
[S2-GigabitEthernet0/0/2]port hybrid untagged vlan 10
[S2-GigabitEthernet0/0/2]q
[S2]int g0/0/3
[S2-GigabitEthernet0/0/3]port link-type hybrid
[S2-GigabitEthernet0/0/3]port hybrid untagged vlan 10
[S2-GigabitEthernet0/0/3]q
```

На trunk-портах S1 и S2 дополнительно разрешена VLAN 10.

```text
[S1]int g0/0/10
[S1-GigabitEthernet0/0/10]port trunk allow-pass vlan 10
[S1-GigabitEthernet0/0/10]q
[S2]int g0/0/10
[S2-GigabitEthernet0/0/10]port trunk allow-pass vlan 10
[S2-GigabitEthernet0/0/10]q
```

На hybrid-портах S2 включено назначение VLAN по MAC-адресу источника.

```text
[S2]int g0/0/1
[S2-GigabitEthernet0/0/1]mac-vlan enable
[S2-GigabitEthernet0/0/1]q
[S2]int g0/0/2
[S2-GigabitEthernet0/0/2]mac-vlan enable
[S2-GigabitEthernet0/0/2]q
[S2]int g0/0/3
[S2-GigabitEthernet0/0/3]mac-vlan enable
[S2-GigabitEthernet0/0/3]q
```


== Шаг 6. Проверка конфигурации

```text
[S1]dis vlan
The total number of vlans is : 4
--------------------------------------------------------------------------------
U: Up;         D: Down;         TG: Tagged;         UT: Untagged;
MP: Vlan-mapping;               ST: Vlan-stacking;
#: ProtocolTransparent-vlan;    *: Management-vlan;
--------------------------------------------------------------------------------

VID  Type    Ports
--------------------------------------------------------------------------------
1    common  UT:GE0/0/2(D)     GE0/0/3(D)     GE0/0/4(D)     GE0/0/5(D)
                GE0/0/6(D)     GE0/0/7(D)     GE0/0/8(D)     GE0/0/9(D)
                GE0/0/11(D)    GE0/0/12(D)    GE0/0/14(D)    GE0/0/15(D)
                GE0/0/16(D)    GE0/0/17(D)    GE0/0/18(D)    GE0/0/19(D)
                GE0/0/20(D)    GE0/0/21(D)    GE0/0/22(D)    GE0/0/23(D)
                GE0/0/24(D)

2    common  UT:GE0/0/1(U)
             TG:GE0/0/10(U)
3    common  UT:GE0/0/13(U)
             TG:GE0/0/10(U)
10   common  TG:GE0/0/10(U)

VID  Status  Property      MAC-LRN Statistics Description
--------------------------------------------------------------------------------
1    enable  default       enable  disable    VLAN 0001
2    enable  default       enable  disable    VLAN 0002
3    enable  default       enable  disable    VLAN 0003
10   enable  default       enable  disable    VLAN 0010
```

```text
<S2>dis vlan
The total number of vlans is : 4
--------------------------------------------------------------------------------
U: Up;         D: Down;         TG: Tagged;         UT: Untagged;
MP: Vlan-mapping;               ST: Vlan-stacking;
#: ProtocolTransparent-vlan;    *: Management-vlan;
--------------------------------------------------------------------------------

VID  Type    Ports
--------------------------------------------------------------------------------
1    common  UT:GE0/0/1(U)     GE0/0/2(D)     GE0/0/3(D)     GE0/0/4(D)
                GE0/0/5(D)     GE0/0/6(D)     GE0/0/7(D)     GE0/0/8(D)
                GE0/0/9(D)     GE0/0/11(D)    GE0/0/12(D)    GE0/0/13(D)
                GE0/0/15(D)    GE0/0/16(D)    GE0/0/17(D)    GE0/0/18(D)
                GE0/0/19(D)    GE0/0/20(D)    GE0/0/21(D)    GE0/0/22(D)
                GE0/0/23(D)    GE0/0/24(D)

2    common  TG:GE0/0/10(U)
3    common  UT:GE0/0/14(U)
             TG:GE0/0/10(U)
10   common  UT:GE0/0/1(U)      GE0/0/2(D)      GE0/0/3(D)
             TG:GE0/0/10(U)

VID  Status  Property      MAC-LRN Statistics Description
--------------------------------------------------------------------------------
1    enable  default       enable  disable    VLAN 0001
2    enable  default       enable  disable    VLAN 0002
3    enable  default       enable  disable    VLAN 0003
10   enable  default       enable  disable    VLAN 0010
```

```text
<S2>dis mac-vlan vlan 10
---------------------------------------------------
MAC Address     MASK            VLAN    Priority
---------------------------------------------------
a008-6fe1-0c46   ffff-ffff-ffff  10      0

Total MAC VLAN address count: 1
```

Trunk GE0/0/10 на S1 и S2 передаёт VLAN 2, 3 и 10 с тегами.
На S2 порты GE0/0/1–GE0/0/3 включены в VLAN 10 без тегов;
настроена одна привязка MAC-адреса к VLAN 10.

=== Проверка связи и таблиц MAC-адресов

```text
[S4]ping 10.1.3.1
  PING 10.1.3.1: 56 data bytes, press CTRL_C to break
    Reply from 10.1.3.1: bytes=56 Sequence=1 ttl=255 time=80 ms
    Reply from 10.1.3.1: bytes=56 Sequence=2 ttl=255 time=60 ms
    Reply from 10.1.3.1: bytes=56 Sequence=3 ttl=255 time=60 ms
    Reply from 10.1.3.1: bytes=56 Sequence=4 ttl=255 time=100 ms
    Reply from 10.1.3.1: bytes=56 Sequence=5 ttl=255 time=80 ms

  --- 10.1.3.1 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 60/76/100 ms
```

```text
<R1>ping 10.1.3.1
  PING 10.1.3.1: 56 data bytes, press CTRL_C to break
    Request time out
    Request time out
    Request time out
    Request time out
    Request time out

  --- 10.1.3.1 ping statistics ---
    5 packet(s) transmitted
    0 packet(s) received
    100.00% packet loss
```

```text
<R1>ping 10.1.3.2
  PING 10.1.3.2: 56 data bytes, press CTRL_C to break
    Request time out
    Request time out
    Request time out

  --- 10.1.3.2 ping statistics ---
    3 packet(s) transmitted
    0 packet(s) received
    100.00% packet loss
```

```text
<R1>ping 10.1.10.1
  PING 10.1.10.1: 56 data bytes, press CTRL_C to break
    Request time out
    Request time out
    Request time out
    Request time out
    Request time out

  --- 10.1.10.1 ping statistics ---
    5 packet(s) transmitted
    0 packet(s) received
    100.00% packet loss
```

Связь S4–S3 в VLAN 3 работает без потерь. С R1 ответы не получены;
между IP-подсетями маршрутизация не настроена.

```text
[S1]dis mac-address verbose
MAC address table of slot 0:
-------------------------------------------------------------------------------
MAC Address    VLAN/       PEVLAN CEVLAN Port            Type      LSP/LSR-ID
               VSI/SI                                            MAC-Tunnel
-------------------------------------------------------------------------------
4c1f-ccb5-2ff5  3           -      -      GE0/0/13        dynamic   0/-
4c1f-cc39-68ce  3           -      -      GE0/0/10        dynamic   0/-
-------------------------------------------------------------------------------
Total matching items on slot 0 displayed = 2
```

```text
<S2>dis mac-address verbose
MAC address table of slot 0:
-------------------------------------------------------------------------------
MAC Address    VLAN/       PEVLAN CEVLAN Port            Type      LSP/LSR-ID
               VSI/SI                                            MAC-Tunnel
-------------------------------------------------------------------------------
4c1f-cc39-68ce  3           -      -      GE0/0/14        dynamic   0/-
4c1f-ccb5-2ff5  3           -      -      GE0/0/10        dynamic   0/-
-------------------------------------------------------------------------------
Total matching items on slot 0 displayed = 2
```

На S1 адрес S3 изучен через GE0/0/13, адрес S4 — через trunk GE0/0/10.
На S2 адрес S4 изучен через GE0/0/14, адрес S3 — через trunk GE0/0/10.
Обе динамические записи относятся к VLAN 3.



=== Исправление MAC-привязки и сохранение конфигурации

Фактический MAC-адрес GE0/0/2 на R3 — `00e0-fc0c-3a70`.
Фрагмент диагностического вывода:

```text
<R3>dis int g0/0/2
GigabitEthernet0/0/2 current state : UP
Line protocol current state : UP
...
Internet Address is 10.1.10.1/24
IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 00e0-fc0c-3a70
```

Привязка адреса из методички удалена, добавлен фактический адрес R3:

```text
[S2]vlan 10
[S2-vlan10]undo mac-vlan mac-address a008-6fe1-0c46
[S2-vlan10]mac-vlan mac-address 00e0-fc0c-3a70
[S2-vlan10]q
[S2]dis mac-vlan vlan 10
---------------------------------------------------
MAC Address     MASK            VLAN    Priority
---------------------------------------------------
00e0-fc0c-3a70   ffff-ffff-ffff  10      0

Total MAC VLAN address count: 1
```

В таблице осталась одна привязка, соответствующая MAC-адресу R3.
Конфигурация S2 сохранена:

```text
[S2]return
<S2>save
The current configuration will be written to the device.
Are you sure to continue?[Y/N]Y
Now saving the current configuration to the slot 0.
Save the configuration successfully.
```

= Вывод

Настроены access-, trunk- и hybrid-порты, VLAN 2, 3 и 10 и MAC-привязка
к VLAN 10. Связь S3–S4 в VLAN 3 подтверждена ping и таблицами MAC-адресов.
R1 не получил ответов от устройств других подсетей.
MAC-привязка VLAN 10 исправлена на фактический адрес R3; конфигурация S2 сохранена.
