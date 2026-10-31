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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №5]
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

Создание WLAN.
Контроллер AC управляет двумя точками доступа; пользовательский трафик
передаётся напрямую через проводную сеть.

#figure(
  image("img/topology.png", width: 100%),
  caption: [Реализованная топология WLAN с адресами интерфейсов, AP и STA],
)

VLAN 100 используется для управления AP, VLAN 101 — для клиентов
SSID HCIA-WLAN. Динамические адреса показаны на момент проверки.

#table(
  columns: (auto, auto), inset: 5pt,
  table.header([*Первый конец*], [*Второй конец*]),
  [AC GE0/0/1], [S1 GE0/0/10],
  [S1 GE0/0/13], [S3 GE0/0/1],
  [S1 GE0/0/14], [S4 GE0/0/1],
  [S3 GE0/0/4], [AP1 GE0/0/1],
  [S4 GE0/0/4], [AP2 GE0/0/1],
)

#table(
  columns: (auto, auto, auto), inset: 5pt,
  table.header([*Назначение*], [*VLAN*], [*Адресация*]),
  [Управление AP], [100], [192.168.100.0/24],
  [Беспроводные клиенты STA], [101], [192.168.101.0/24],
  [AC, Vlanif100], [100], [192.168.100.254/24],
  [S1, Vlanif101], [101], [192.168.101.254/24],
  [S1, LoopBack0], [—], [10.0.1.1/32],
)

= Ход работы

== Шаг 1. Основные параметры устройств

На S1 заданы имя и административное отключение неиспользуемых портов.

```text
<Huawei>sys
[Huawei]sysname S1
[S1]int g0/0/11
[S1-GigabitEthernet0/0/11]shutdown
[S1-GigabitEthernet0/0/11]q
[S1]int g0/0/12
[S1-GigabitEthernet0/0/12]shutdown
[S1-GigabitEthernet0/0/12]q
```

На S3 и S4 заданы имена и проверена возможность включения PoE.

```text
<Huawei>sys
[Huawei]sysname S3
[S3]int g0/0/4
[S3-GigabitEthernet0/0/4]poe enable
                         ^
Error: Unrecognized command found at '^' position.
```

```text
<Huawei>sys
[Huawei]sysname S4
[S4]int g0/0/4
[S4-GigabitEthernet0/0/4]poe enable
                         ^
Error: Unrecognized command found at '^' position.
```

В используемых виртуальных коммутаторах команда PoE не поддерживается.
Физическое питание виртуальных AP по Ethernet не требуется.

```text
<AC6005>sys
[AC6005]sysname AC
```

== Шаг 2. Проводная сеть и DHCP

=== VLAN и trunk-порты

S1:

```text
[S1]vlan batch 100 101
[S1]int g0/0/10
[S1-GigabitEthernet0/0/10]port link-type trunk
[S1-GigabitEthernet0/0/10]port trunk allow-pass vlan 100 10
[S1-GigabitEthernet0/0/10]port trunk allow-pass vlan 100 101
[S1-GigabitEthernet0/0/10]q
[S1]int g0/0/13
[S1-GigabitEthernet0/0/13]port link-type trunk
[S1-GigabitEthernet0/0/13]port trunk allow-pass vlan 100 101
[S1-GigabitEthernet0/0/13]q
[S1]int g0/0/14
[S1-GigabitEthernet0/0/14]port link-type trunk
[S1-GigabitEthernet0/0/14]port trunk allow-pass vlan 100 101
[S1-GigabitEthernet0/0/14]q
```

S3:

```text
[S3]vlan batch 100 101
[S3]int g0/0/1
[S3-GigabitEthernet0/0/1]port link-type trunk
[S3-GigabitEthernet0/0/1]port trunk allow-pass vlan 100 101
[S3-GigabitEthernet0/0/1]q
[S3]int g0/0/4
[S3-GigabitEthernet0/0/4]port link-type trunk
[S3-GigabitEthernet0/0/4]port trunk pvid vlan 100
[S3-GigabitEthernet0/0/4]port trunk allow-pass vlan 100 101
[S3-GigabitEthernet0/0/4]q
```

S4:

```text
[S4]vlan batch 100 101
[S4]int g0/0/1
[S4-GigabitEthernet0/0/1]port link-type trunk
[S4-GigabitEthernet0/0/1]port trunk allow-pass vlan 100 101
[S4-GigabitEthernet0/0/1]q
[S4]int g0/0/4
[S4-GigabitEthernet0/0/4]port link-type trunk
[S4-GigabitEthernet0/0/4]port trunk pvid vlan 100
[S4-GigabitEthernet0/0/4]port trunk allow-pass vlan 100 101
[S4-GigabitEthernet0/0/4]q
```

AC:

```text
[AC]vlan batch 100 101
[AC]int g0/0/1
[AC-GigabitEthernet0/0/1]port link-type trunk
[AC-GigabitEthernet0/0/1]port trunk allow-pass vlan 100 101
```

На порту S1 GE0/0/10 при вводе дополнительно разрешена VLAN 10;
её удаление из списка будет выполнено отдельно.

=== IP-интерфейсы и DHCP

S1 выдаёт адреса беспроводным клиентам из сети 192.168.101.0/24.

```text
[S1]int Vlanif 101
[S1-Vlanif101]ip ad 192.168.101.254 24
[S1-Vlanif101]q
[S1]int LoopBack 0
[S1-LoopBack0]ip ad 10.0.1.1 32
[S1-LoopBack0]q
[S1]dhcp enable
[S1]ip pool sta
[S1-ip-pool-sta]network 192.168.101.0 mask 24
[S1-ip-pool-sta]gateway-list 192.168.101.254
[S1-ip-pool-sta]q
[S1]int Vlanif 101
[S1-Vlanif101]dhcp select global
[S1-Vlanif101]q
```

AC выдаёт адреса точкам доступа из сети 192.168.100.0/24.

```text
[AC]int Vlanif 100
[AC-Vlanif100]ip ad 192.168.100.254 24
[AC-Vlanif100]q
[AC]dhcp enable
[AC]ip pool ap
[AC-ip-pool-ap]network 192.168.100.0 mask 24
[AC-ip-pool-ap]gateway-list 192.168.100.254
[AC-ip-pool-ap]q
[AC]int Vlanif 100
[AC-Vlanif100]dhcp select global
[AC-Vlanif100]q
```


=== Проверка интерфейса и пула AP

```text
[AC]dis ip int brief
*down: administratively down
^down: standby
(l): loopback
(s): spoofing
(E): E-Trunk down
The number of interface that is UP in Physical is 2
The number of interface that is DOWN in Physical is 0
The number of interface that is UP in Protocol is 2
The number of interface that is DOWN in Protocol is 0

Interface                   IP Address/Mask      Physical   Protocol
NULL0                       unassigned           up         up(s)
Vlanif100                    192.168.100.254/24    up         up
```

```text
[AC]dis ip pool name ap
  Pool-name        : ap
  Pool-No          : 0
  Lease            : 1 Days 0 Hours 0 Minutes
  Domain-name      : -
  DNS-server0      : -
  NBNS-server0     : -
  Netbios-type     : -
  Position         : Local           Status             : Unlocked
  Gateway-0        : 192.168.100.254
  Network          : 192.168.100.0
  Mask             : 255.255.255.0
  Logging          : Disable
  Conflicted address recycle interval: -
  Address Statistic: Total       :253       Used        :2
                     Idle        :251       Expired     :0
                     Conflict    :0         Disabled    :0

 -------------------------------------------------------------------------------
  Network section
         Start           End       Total    Used Idle(Expired) Conflict Disabled
 -------------------------------------------------------------------------------
   192.168.100.1 192.168.100.254     253       2        251(0)       0     0
 -------------------------------------------------------------------------------
```

Vlanif100 находится в состоянии up/up; в пуле заняты два адреса,
конфликтов нет. Принадлежность выданных адресов AP проверяется далее.

== Шаг 3. Подключение точек доступа к контроллеру

=== Создание группы AP

```text
[AC]wlan
[AC-wlan-view]ap-group name ap-group1
Info: This operation may take a few seconds. Please wait for a moment.done.
[AC-wlan-ap-group-ap-group1]q
```

=== Профиль регулирующего домена

```text
[AC-wlan-view]regulatory-domain-profile name default
[AC-wlan-regulate-domain-default]country-code cn
Info: The current country code is same with the input country code.
[AC-wlan-regulate-domain-default]q
[AC-wlan-view]ap-group name ap-group1
[AC-wlan-ap-group-ap-group1]regulatory-domain-profile default
Warning: Modifying the country code will clear channel, power and antenna gain
configurations of the radio and reset the AP. Continue?[Y/N]:Y
```

=== Интерфейс CAPWAP и первоначальное обнаружение AP

```text
[AC-wlan-ap-group-ap-group1]q
[AC-wlan-view]q
[AC]capwap source interface Vlanif 100
[AC]wlan
[AC-wlan-view]ap auth-mode mac-auth
[AC-wlan-view]dis ap unauthorized record
Info: There is no ap unauthorized record.
[AC-wlan-view]dis ap all
Info: This operation may take a few seconds. Please wait for a moment.done.
--------------------------------------------------------------------------------
ID   MAC            Name  Group  IP  Type  State  STA  Uptime
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Total: 0
```

На момент проверки AP в списках контроллера отсутствуют.

=== Проверка DHCP и связи AP1 с AC

Фрагмент вывода занятых адресов пула AP:

```text
[AC]dis ip pool name ap used
Index  IP               Client-ID       Type  Left   Status
131    192.168.100.132   00e0-fcbd-7c20   DHCP  85456  Used
144    192.168.100.145   00e0-fc38-7a30   DHCP  85459  Used
```

Фрагменты диагностического вывода AP1:

```text
[Huawei]dis ip int brief
Interface      IP Address/Mask       Physical   Protocol
NULL0          unassigned            up         up(s)
Vlanif1        192.168.100.145/24     up         up

[Huawei]dis interface g0/0/1
GigabitEthernet0/0/1 current state : UP
Line protocol current state : UP
Description:HUAWEI, AP Series, GigabitEthernet0/0/1 Interface
Switch Port, PVID : 1, TPID : 8100(Hex), The Maximum Frame Length is 1800
IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 00e0-fc38-7a30
```

```text
[Huawei]ping 192.168.100.254
  PING 192.168.100.254: 56 data bytes, press CTRL_C to break
    Reply from 192.168.100.254: bytes=56 Sequence=1 ttl=255 time=170 ms
    Reply from 192.168.100.254: bytes=56 Sequence=2 ttl=255 time=40 ms
    Reply from 192.168.100.254: bytes=56 Sequence=3 ttl=255 time=90 ms
    Reply from 192.168.100.254: bytes=56 Sequence=4 ttl=255 time=30 ms
    Reply from 192.168.100.254: bytes=56 Sequence=5 ttl=255 time=60 ms

  --- 192.168.100.254 ping statistics ---
    5 packet(s) transmitted
    5 packet(s) received
    0.00% packet loss
    round-trip min/avg/max = 30/78/170 ms
```

AP1 получила адрес по DHCP; IP-связь с контроллером подтверждена.

=== Регистрация AP1

```text
[AC]wlan
[AC-wlan-view]ap-id 0 ap-mac 00e0-fc38-7a30
[AC-wlan-ap-0]ap-name ap1
[AC-wlan-ap-0]ap-group ap-group1
Warning: This operation may cause AP reset. If the country code changes, it will
clear channel, power and antenna gain configurations of the radio,
Whether to continue? [Y/N]:Y
Info: This operation may take a few seconds. Please wait for a moment.. done.
[AC-wlan-ap-0]q
```

После первоначального состояния idle AP1 перешла в рабочее состояние nor.
Вывод с восстановленными переносами строк:

```text
[AC-wlan-view]dis ap all
Total AP information:
nor : normal [1]
ID MAC            Name Group     IP              Type     State STA Uptime
0  00e0-fc38-7a30 ap1  ap-group1 192.168.100.145 AP2050DN nor   0   1M:0S
Total: 1
```

=== Проверка адресов AP2

Фрагменты вывода AP2:

```text
<Huawei>dis interface g0/0/1
GigabitEthernet0/0/1 current state : UP
Line protocol current state : UP
Description:HUAWEI, AP Series, GigabitEthernet0/0/1 Interface
IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 00e0-fcbd-7c20

<Huawei>dis ip int brief
Interface      IP Address/Mask      Physical Protocol
NULL0          unassigned           up       up(s)
Vlanif1        192.168.100.132/24    up       up
```

=== Регистрация AP2 и проверка обеих AP

```text
[AC-wlan-view]ap-id 1 ap-mac 00e0-fcbd-7c20
[AC-wlan-ap-1]ap-name ap2
[AC-wlan-ap-1]ap-group ap-group1
Warning: This operation may cause AP reset. If the country code changes, it will
clear channel, power and antenna gain configurations of the radio,
Whether to continue? [Y/N]:Y
Info: This operation may take a few seconds. Please wait for a moment.. done.
[AC-wlan-ap-1]q
```

После первоначального состояния fault AP2 перешла в nor.
Итоговый вывод с восстановленными переносами строк:

```text
[AC-wlan-view]dis ap all
Total AP information:
nor : normal [2]
ID MAC            Name Group     IP              Type     State STA Uptime
0  00e0-fc38-7a30 ap1  ap-group1 192.168.100.145 AP2050DN nor   0   6M:55S
1  00e0-fcbd-7c20 ap2  ap-group1 192.168.100.132 AP2050DN nor   0   40S
Total: 2
```

Обе AP зарегистрированы, получили адреса по DHCP и находятся в рабочем состоянии.

== Шаг 4. Настройка сервисов WLAN

=== Профили безопасности и SSID

```text
[AC-wlan-view]security-profile name HCIA-WLAN
[AC-wlan-sec-prof-HCIA-WLAN]security wpa-wpa2 psk pass-phrase HCIA-Datacom aes
[AC-wlan-sec-prof-HCIA-WLAN]q
[AC-wlan-view]ssid-profile name HCIA-WLAN
[AC-wlan-ssid-prof-HCIA-WLAN]ssid HCIA-WLAN
Info: This operation may take a few seconds, please wait.done.
[AC-wlan-ssid-prof-HCIA-WLAN]q
```

Заданы SSID HCIA-WLAN, защита WPA/WPA2-PSK с AES и пароль HCIA-Datacom.

=== Профиль VAP и привязка к группе AP

```text
[AC-wlan-view]vap-profile name HCIA-WLAN
[AC-wlan-vap-prof-HCIA-WLAN]forward-mode direct-forward
[AC-wlan-vap-prof-HCIA-WLAN]service-vlan vlan-id 101
[AC-wlan-vap-prof-HCIA-WLAN]security-profile HCIA-WLAN
[AC-wlan-vap-prof-HCIA-WLAN]ssid-profile HCIA-WLAN
[AC-wlan-vap-prof-HCIA-WLAN]q
[AC-wlan-view]ap-group name ap-group1
[AC-wlan-ap-group-ap-group1]vap-profile HCIA-WLAN wlan 1 radio all
Info: This operation may take a few seconds, please wait...done.
```

Профиль HCIA-WLAN применён ко всем радиомодулям группы ap-group1.
Клиентский трафик передаётся напрямую в VLAN 101.

== Проверка подключения беспроводных клиентов

=== Первая станция



```text
STA>ipconfig

Link local IPv6 address : ::
IPv6 address            : :: / 128
IPv6 gateway            : ::
IPv4 address            : 192.168.101.253
Subnet mask             : 255.255.255.0
Gateway                 : 192.168.101.254
Physical address        : 54-89-98-3F-54-F3
DNS server              :
```

```text
STA>ping 192.168.101.254

Ping 192.168.101.254: 32 data bytes, Press Ctrl_C to break
From 192.168.101.254: bytes=32 seq=1 ttl=255 time=125 ms
From 192.168.101.254: bytes=32 seq=2 ttl=255 time=140 ms
From 192.168.101.254: bytes=32 seq=3 ttl=255 time=141 ms
From 192.168.101.254: bytes=32 seq=4 ttl=255 time=141 ms
From 192.168.101.254: bytes=32 seq=5 ttl=255 time=141 ms

--- 192.168.101.254 ping statistics ---
  5 packet(s) transmitted
  5 packet(s) received
  0.00% packet loss
  round-trip min/avg/max = 125/137/141 ms
```

```text
STA>ping 10.0.1.1

Ping 10.0.1.1: 32 data bytes, Press Ctrl_C to break
From 10.0.1.1: bytes=32 seq=1 ttl=255 time=141 ms
From 10.0.1.1: bytes=32 seq=2 ttl=255 time=141 ms
From 10.0.1.1: bytes=32 seq=3 ttl=255 time=125 ms
From 10.0.1.1: bytes=32 seq=4 ttl=255 time=125 ms
From 10.0.1.1: bytes=32 seq=5 ttl=255 time=125 ms

--- 10.0.1.1 ping statistics ---
  5 packet(s) transmitted
  5 packet(s) received
  0.00% packet loss
  round-trip min/avg/max = 125/131/141 ms
```

=== Сведения о станции на AC

Вывод с восстановленными переносами строк:

```text
[AC-wlan-ap-group-ap-group1]q
[AC-wlan-view]dis station all
Rf/WLAN: Radio ID/WLAN ID
Rx/Tx: link receive rate/link transmit rate(Mbps)
```

#text(size: 8pt)[
#table(
  columns: (auto, auto, auto, auto, auto), inset: 4pt,
  table.header([*STA MAC*], [*AP ID / имя*], [*Rf/WLAN / Band*], [*VLAN*], [*IP / SSID*]),
  [5489-983f-54f3], [0 / ap1], [0/1 / 2.4G], [101],
  [192.168.101.253 / HCIA-WLAN],
)
]

Остальные поля станции: Type — «-», Rx/Tx — «-/-», RSSI — «-».
Итого: Total: 1, 2.4G: 1, 5G: 0.

Станция подключена к ap1 в VLAN 101 и получила адрес 192.168.101.253.
Шлюз S1 и LoopBack0 S1 доступны без потерь.

=== Вторая станция

```text
STA>ipconfig

Link local IPv6 address : ::
IPv6 address            : :: / 128
IPv6 gateway            : ::
IPv4 address            : 192.168.101.252
Subnet mask             : 255.255.255.0
Gateway                 : 192.168.101.254
Physical address        : 54-89-98-06-29-8A
DNS server              :
```

```text
STA>ping 192.168.101.254

Ping 192.168.101.254: 32 data bytes, Press Ctrl_C to break
From 192.168.101.254: bytes=32 seq=1 ttl=255 time=125 ms
From 192.168.101.254: bytes=32 seq=2 ttl=255 time=125 ms
From 192.168.101.254: bytes=32 seq=3 ttl=255 time=140 ms
From 192.168.101.254: bytes=32 seq=4 ttl=255 time=141 ms
From 192.168.101.254: bytes=32 seq=5 ttl=255 time=156 ms

--- 192.168.101.254 ping statistics ---
  5 packet(s) transmitted
  5 packet(s) received
  0.00% packet loss
  round-trip min/avg/max = 125/137/156 ms
```

```text
STA>ping 192.168.101.253

Ping 192.168.101.253: 32 data bytes, Press Ctrl_C to break
From 192.168.101.253: bytes=32 seq=1 ttl=128 time=265 ms
From 192.168.101.253: bytes=32 seq=2 ttl=128 time=266 ms
From 192.168.101.253: bytes=32 seq=3 ttl=128 time=266 ms
From 192.168.101.253: bytes=32 seq=4 ttl=128 time=265 ms
From 192.168.101.253: bytes=32 seq=5 ttl=128 time=250 ms

--- 192.168.101.253 ping statistics ---
  5 packet(s) transmitted
  5 packet(s) received
  0.00% packet loss
  round-trip min/avg/max = 250/262/266 ms
```

Проверка доступности LoopBack0 S1 с STA2 завершилась успешно
(подтверждено при выполнении). Ниже сохранён доступный фрагмент вывода;
полная статистика не зафиксирована.

```text
STA>ping 10.0.1.1

Ping 10.0.1.1: 32 data bytes, Press Ctrl_C to break
From 10.0.1.1: bytes=32 seq=1 ttl=255 time=141 ms
From 10.0.1.1: bytes=32 seq=2 ttl=255 time=125 ms
```

=== Итоговый список клиентов на AC

```text
[AC-wlan-view]dis station all
Rf/WLAN: Radio ID/WLAN ID
Rx/Tx: link receive rate/link transmit rate(Mbps)
```

#text(size: 8pt)[
#table(
  columns: (auto, auto, auto, auto, auto), inset: 4pt,
  table.header([*STA MAC*], [*AP ID / имя*], [*Rf/WLAN / Band*], [*VLAN*], [*IP / SSID*]),
  [5489-9806-298a], [1 / ap2], [0/1 / 2.4G], [101],
  [192.168.101.252 / HCIA-WLAN],
  [5489-983f-54f3], [0 / ap1], [0/1 / 2.4G], [101],
  [192.168.101.253 / HCIA-WLAN],
)
]

Для обеих станций Type — «-», Rx/Tx — «-/-», RSSI — «-».
Итого: Total: 2, 2.4G: 2, 5G: 0.

STA1 и STA2 подключены к разным AP одной WLAN и получили адреса
в VLAN 101. Связь STA2 со шлюзом и STA1 подтверждена без потерь.



== Сохранение конфигурации

Конфигурации AC, S1, S3 и S4 сохранены командой `save` в пользовательском
режиме. Зафиксированный вывод S4:

```text
<S4>save
The current configuration will be written to the device.
Are you sure to continue?[Y/N]Y
Info: Please input the file name ( *.cfg, *.zip ) [vrpcfg.zip]:
Now saving the current configuration to the slot 0.
Save the configuration successfully.
```

= Вывод

Настроена WLAN HCIA-WLAN с защитой WPA/WPA2-PSK и AES.
Обе AP подключены к AC, две станции получили адреса по DHCP в VLAN 101.
Подтверждена доступность шлюза и связь между клиентами разных AP;
с обеих станций проверена связь с LoopBack0 S1.
