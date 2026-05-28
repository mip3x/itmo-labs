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

  #text(weight: "bold", size: 16pt)[Отчёт по лабораторной работе №4]
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

#text(size: 16pt, weight: "bold")[Лабораторная работа №4]

= Анализ трафика утилиты ping

На данном этапе анализировался трафик утилиты `ping` в программе `Wireshark`. Использовался домен `se.ifmo.ru`, IP-адрес сервера `77.234.196.4`, локальный IP-адрес компьютера `192.168.0.17`.

Для отображения ICMP-пакетов и IPv4-фрагментов использовался фильтр:

```text
ip.flags.mf == 1 || ip.frag_offset > 0 || icmp
```

Использованные команды:

```text
ping -c 4 -s 100 se.ifmo.ru
ping -c 4 -s 500 se.ifmo.ru
ping -c 4 -s 1000 se.ifmo.ru
ping -c 4 -s 1500 se.ifmo.ru
ping -c 4 -s 2000 se.ifmo.ru
ping -c 4 -s 3000 se.ifmo.ru
ping -c 4 -s 5000 se.ifmo.ru
ping -c 4 -s 10000 se.ifmo.ru
ping -c 4 -s 15000 se.ifmo.ru
ping -c 4 -s 20000 se.ifmo.ru
ping -c 4 -t 10 se.ifmo.ru
```

== Общий вид захваченного трафика

#figure(
  image("ping_full_screen.png", width: 100%),
  caption: [Список ICMP-пакетов и IPv4-фрагментов в Wireshark]
)

На скриншоте видны ICMP Echo Request/Reply и IPv4-фрагменты. Обычные ping-пакеты отображаются как `ICMP`, а фрагментированные — как `IPv4` с описанием `Fragmented IP protocol (proto=ICMP 1)`.

Фрагменты одного исходного пакета имеют одинаковое поле `Identification`. Первый фрагмент имеет `Fragment Offset = 0`, промежуточные фрагменты имеют флаг `More fragments`, а у последнего фрагмента этот флаг сброшен.

IP-адрес источника исходящих запросов — `192.168.0.17`, адрес назначения — `77.234.196.4`.

== Структура ICMP Echo Request

#figure(
  image("ping_icmp_structure.png", width: 100%),
  caption: [Структура ICMP Echo Request в Wireshark]
)

На скриншоте раскрыта структура ICMP Echo Request:

- `Frame` — служебная информация Wireshark о захваченном кадре: номер, длина, время захвата и интерфейс;
- `Ethernet II` — заголовок канального уровня с MAC-адресами источника и назначения;
- `Internet Protocol Version 4` — заголовок сетевого уровня с IP-адресами, TTL, идентификатором и флагами фрагментации;
- `Internet Control Message Protocol` — ICMP-сообщение, используемое утилитой `ping`.

В Ethernet-заголовке указаны MAC-адреса локального компьютера и ближайшего шлюза. В IPv4-заголовке указаны IP-адреса источника и назначения, `TTL`, `Identification`, флаги фрагментации и поле `Protocol: ICMP (1)`.

Поле `Protocol: ICMP (1)` показывает, что ICMP является полезной нагрузкой IP-пакета. В ICMP-заголовке `Type: Echo request (8)` означает ping-запрос. Поля `Identifier` и `Sequence Number` используются для сопоставления запроса с ответом.

В поле `ICMP Data` находятся данные ping-пакета. В Wireshark они отображаются в шестнадцатеричном виде и как ASCII-символы.

== Ответы на вопросы

1. Имеет ли место фрагментация исходного пакета, какое поле на это указывает?

Для пакета размером `100` байт фрагментации нет: `Don't fragment = Set`, `More fragments = Not set`, `Fragment Offset = 0`.

#figure(
  image("ping_100_bytes_df1mf0.png", width: 100%),
  caption: [Ping-пакет 100 байт без фрагментации]
)

Для пакетов размером `1500` байт и более фрагментация есть. На неё указывают поля IPv4: `More fragments`, `Fragment Offset` и одинаковое значение `Identification`.

#figure(
  image("ping_1500_bytes_df0mf1.png", width: 100%),
  caption: [Первый фрагмент ping-пакета 1500 байт]
)

2. Какая информация указывает, является ли фрагмент пакета последним или промежуточным?

Промежуточный фрагмент имеет `More fragments = Set`. Последний фрагмент имеет `More fragments = Not set` и `Fragment Offset > 0`.

#figure(
  image("ping_1500_bytes_df0mf0.png", width: 100%),
  caption: [Последний фрагмент ping-пакета 1500 байт]
)

3. Чему равно количество фрагментов при передаче ping-пакетов?

Количество фрагментов зависит от размера ping-пакета и MTU канала. В Wireshark оно определяется как число IPv4-пакетов с одинаковым `Identification`.

Общий способ подсчёта: размер данных, которые нужно передать внутри IP-пакета, делится на максимальный размер данных одного фрагмента. Если результат получается нецелым, он округляется вверх.

Для Ethernet MTU обычно равен `1500` байт. При IPv4-заголовке `20` байт в одном полном фрагменте можно передать `1480` байт данных. Значит, количество фрагментов равно размеру IP-данных, разделённому на `1480` и округлённому вверх до целого числа.

Для примера на скриншоте пакет размером `1500` байт разделён на `2` фрагмента: первый с `Fragment Offset = 0`, второй с `Fragment Offset = 1480`.

4. Построить график зависимости количества фрагментов от размера ping-пакета.

#table(
  columns: 2,
  align: center,
  [Размер ping-пакета, байт], [Количество фрагментов],
  [100], [1],
  [500], [1],
  [1000], [1],
  [1500], [2],
  [2000], [2],
  [3000], [3],
  [5000], [4],
  [10000], [7],
  [15000], [11],
  [20000], [14],
)

#figure(
  image("ping_fragments_graph.png", width: 90%),
  caption: [График зависимости количества фрагментов от размера ping-пакета]
)

5. Как изменить поле TTL с помощью утилиты ping?

В Linux поле TTL изменяется ключом `-t`. В работе использовалась команда:

```text
ping -c 4 -t 10 se.ifmo.ru
```

После этого в IPv4-заголовке исходящего ICMP-пакета поле `Time to Live` равно `10`.

#figure(
  image("ping_ttl_10.png", width: 100%),
  caption: [ICMP-пакет с TTL = 10]
)

6. Что содержится в поле данных ping-пакета?

Как я понял, это вся полезная нагрузка `IP`-пакета: то есть `ICMP`-заголовок (8 байт) и полезная нагрузка `ICMP`-заголовка.

В поле `ICMP Data` содержится timestamp и последовательность тестовых байтов (`ICMP Data` как раз будет равна размеру, переданному как аргумент команды `ping`). Эти данные возвращаются обратно в `Echo Reply`, чтобы отправитель мог сопоставить ответ с запросом и измерить время передачи.

#figure(
  image("ping_icmp_data.png", width: 100%),
  caption: [Поле ICMP Data в ping-пакете]
)

= Анализ трафика утилиты traceroute

По умолчанию в Linux `traceroute` использует UDP-датаграммы на маловероятные порты. В руководстве это описано так:

```text
Probe packets are udp datagrams with so-called "unlikely" destination ports.
The "unlikely" port of the first probe is 33434, then for each next probe it
is incremented by one.
```

В данной работе использовался ключ `-I`, чтобы отправлять ICMP Echo Request и сравнить результат с трафиком `ping`.

Для анализа маршрута использовалась команда:

```text
traceroute -I -n se.ifmo.ru
```

Ключ `-I` включает отправку ICMP Echo Request, ключ `-n` отключает DNS-разрешение имён узлов.

#figure(
  image("traceroute_full_screen.png", width: 100%),
  caption: [Общий вид ICMP-трафика traceroute]
)

#figure(
  image("traceroute_request_ttl10.png", width: 100%),
  caption: [Исходящий ICMP-пакет traceroute с TTL = 10]
)

== Ответы на вопросы

1. Сколько байт содержится в заголовке IP? Сколько байт содержится в поле данных?

В ICMP-пакетах `traceroute` IPv4-заголовок занимает `20` байт, так как поле `Header Length` равно `5` словам по 4 байта. Общая длина IP-пакета равна `60` байт, поэтому поле данных IP-пакета занимает `40` байт.

#figure(
  image("traceroute_ping_length.png", width: 100%),
  caption: [Длина IPv4-заголовка и общая длина IP-пакета traceroute]
)

2. Как и почему изменяется поле TTL в следующих друг за другом ICMP-пакетах traceroute?

`traceroute` отправляет серии ICMP-пакетов с увеличивающимся TTL: сначала `1`, затем `2`, `3` и так далее. Каждый маршрутизатор уменьшает TTL на `1`. Когда TTL становится равным нулю, маршрутизатор отбрасывает пакет и возвращает ICMP-сообщение `Time-to-live exceeded`. Так определяется каждый следующий узел маршрута.

3. Чем отличаются ICMP-пакеты traceroute от ICMP-пакетов ping?

`ping` отправляет ICMP Echo Request с обычным TTL и ждёт Echo Reply от конечного узла. `traceroute` отправляет ICMP Echo Request с постепенно увеличивающимся TTL, чтобы получать ответы не только от конечного сервера, но и от промежуточных маршрутизаторов.

4. Чем отличаются полученные пакеты ICMP reply от ICMP error и зачем нужны оба типа ответов?

`ICMP reply` приходит от конечного узла и означает, что пакет дошёл до цели. `ICMP error`, например `Time-to-live exceeded`, приходит от промежуточного маршрутизатора и означает, что TTL закончился до достижения цели. Ошибки нужны для построения маршрута, а reply — для определения конца маршрута.

5. Что изменится в работе traceroute, если убрать ключ `-n`? Какой дополнительный трафик будет генерироваться?

Без ключа `-n` программа будет пытаться получить доменные имена для IP-адресов промежуточных узлов. Из-за этого появится дополнительный DNS-трафик, обычно обратные DNS-запросы `PTR`.

#figure(
  image("traceroute_full_screen_with_dns.png", width: 100%),
  caption: [Дополнительный DNS-трафик при запуске traceroute без ключа -n]
)

= Вывод

ICMP передаётся внутри IPv4-пакета как его полезная нагрузка. Фрагментация определяется по полям `More fragments`, `Fragment Offset` и `Identification`.
