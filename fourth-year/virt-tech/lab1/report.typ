#set text(font: "Times New Roman", size: 14pt, lang: "ru", fill: black,
  style: "normal", top-edge: 0.8em, bottom-edge: -0.2em)
#set par(justify: true, first-line-indent: (amount: 1.25cm, all: true), leading: 0.5em, spacing: 0pt)
#set page(paper: "a4", margin: (left: 30mm, right: 10mm, top: 20mm, bottom: 20mm),
  numbering: "1", number-align: center,
  footer: context align(center, text(size: 11pt, counter(page).display("1"))))
#set heading(numbering: "1.1")
#show heading: it => {
  set par(first-line-indent: 0pt)
  set text(weight: "bold", size: if it.level == 1 { 16pt } else if it.level == 2 { 15pt } else { 14pt })
  if it.level == 1 { pagebreak(weak: true) }
  align(center, upper(it))
}
#set figure(numbering: "1")
#show figure.where(kind: image): set figure(supplement: [Рисунок])
#show figure.caption: it => {
  set par(first-line-indent: 0pt, justify: false)
  align(center, it)
}
#set figure.caption(position: bottom, separator: [ — ])
#show outline.entry: it => upper(it)
#show outline: set par(first-line-indent: 0pt)
#set raw(theme: none)
#show raw.where(block: true): set par(first-line-indent: 0pt, justify: false)
#show raw: set text(font: "Times New Roman", size: 14pt)
#let student = "* * *"
#let group = "P34**"
#let teacher = "Белозубов Александр Владимирович"
#let teacher_position = "доцент, к.т.н."

#page(margin: (left: 30mm, right: 20mm, top: 10mm, bottom: 10mm), footer: none, numbering: none)[
  #set par(justify: false, first-line-indent: 0pt)
  #align(center)[
    Министерство науки и высшего образования Российской Федерации \
    Федеральное государственное автономное образовательное учреждение высшего образования \
    «Национальный исследовательский университет ИТМО»

    #v(1em)
    Факультет программной инженерии и компьютерной техники
    #v(1fr)
    Лабораторная работа № 1 \
    #v(1fr)
    #align(right)[
      Выполнил: #student \
      Группа: #group

      Проверил: #teacher \
      #teacher_position
    ]
    #v(1fr)
    Санкт-Петербург \
    2026
  ]
]

#outline(title: [ОГЛАВЛЕНИЕ], indent: auto)

#heading(numbering: none)[Введение]
Цель работы — освоить создание и настройку виртуальных машин в VirtualBox, изучить режимы сетевого взаимодействия, снимки состояния и средства обмена данными между основной и гостевыми операционными системами.

Среда выполнения: основная операционная система Arch Linux; гипервизор Oracle VirtualBox; гостевые системы Ubuntu и Windows 10. Для Ubuntu выбран имеющийся образ ubuntu-20.04.6-desktop-amd64.iso, для Windows — имеющийся образ Windows 10 22H2 x64.

Задачи: установить гостевые системы и дополнения; проверить внутреннюю сеть, виртуальный адаптер хоста, NAT и сети NAT; создать и восстановить снимки; настроить общую папку, буфер обмена и перетаскивание; выполнить управление через VBoxManage.

#heading(numbering: none)[Термины и сокращения]
- ОС — операционная система.
- ВМ — виртуальная машина.
- Хост — компьютер с основной ОС, на котором работает VirtualBox.
- Гостевая ОС — операционная система внутри ВМ.
- Гипервизор — программное средство создания и выполнения ВМ.
- NAT — трансляция сетевых адресов.
- DHCP — протокол автоматического назначения сетевых параметров.
- ISO — образ оптического диска.
- CPU — центральный процессор; RAM — оперативная память.
- IPv4 — версия 4 протокола межсетевого взаимодействия; адрес идентифицирует сетевой интерфейс.
- DNS — система доменных имён.
- ICMP — протокол управляющих сообщений; ping использует запросы и ответы Echo.
- SSH — протокол защищённого удалённого доступа.
- UUID — уникальный идентификатор виртуальной машины или носителя.
- BIOS — базовая система ввода-вывода, используемая при загрузке ВМ.
- Guest Additions — драйверы и службы интеграции гостевой ОС с VirtualBox.
- Host-only — виртуальная сеть между хостом и подключёнными гостевыми системами.
- Снимок состояния — сохранённое состояние ВМ, к которому можно вернуться.
- Drag and Drop — перетаскивание объектов между окнами хоста и гостя.

= Установка гостевых операционных систем
== Постановка задачи
Задание по пунктам 1–8 руководства. Установить VirtualBox и Extension Pack, создать ВМ WS_ФИО_win и WS_ФИО_ubuntu, установить Windows 10, Ubuntu и дополнения гостевой ОС.

Требуемая начальная конфигурация каждой ВМ: один виртуальный процессор, 2048 МБ оперативной памяти, один диск 20 ГБ, аудио отключено, один сетевой адаптер в режиме «Внутренняя сеть».

В руководстве пути D:/VM и D:/Public приведены для Windows. На хосте Arch Linux использованы каталоги /home/mip3x/VM и /home/mip3x/public соответственно.

== Ход выполнения
На хосте установлены VirtualBox 7.2.20, пакет модулей virtualbox-host-dkms и заголовки ядра linux-headers. Для последующей установки дополнений в гостевые системы установлен образ:

```sh
sudo pacman -S virtualbox-guest-iso
```

Пакет Oracle VirtualBox Extension Pack установлен командой:

```sh
sudo VBoxManage extpack install /tmp/Oracle_VirtualBox_Extension_Pack-7.2.20.vbox-extpack
```

После установки в Extension Pack Manager отображается активное расширение (см. @fig-extension-pack).

#figure(
  image("img/01-extension-pack.png", width: 100%),
  caption: [Установленный и активный Oracle VirtualBox Extension Pack],
) <fig-extension-pack>

== Установка Ubuntu
Перед установкой к ВМ подключён образ Ubuntu 20.04.6, выделено 2048 МБ памяти и 20 ГБ диска; сеть настроена как Internal Network с именем intnet (см. @fig-ubuntu-initial). На этом скриншоте аудио включено — это отличие от требуемой конфигурации. В установщике выбран вариант Erase disk and install Ubuntu для виртуального диска (см. @fig-ubuntu-install).

#figure(image("img/02-ubuntu-config.png", width: 100%), caption: [Параметры Ubuntu перед установкой]) <fig-ubuntu-initial>
#figure(image("img/03-ubuntu-install-disk.png", width: 100%), caption: [Выбор автоматической разметки виртуального диска Ubuntu]) <fig-ubuntu-install>

Установлена Ubuntu с графическим рабочим столом. После перезагрузки выполнен вход под пользователем user; имя гостевой системы — WS-MMA-ubuntu. Рабочий стол и терминал установленной системы представлены на @fig-ubuntu-desktop.

#figure(
  image("img/04-ubuntu-desktop.png", width: 100%),
  caption: [Рабочий стол Ubuntu после установки],
) <fig-ubuntu-desktop>

== Установка Windows 10
На начальном скриншоте ВМ имеет имя S_MMA_win, впоследствии исправленное на WS_MMA_win. Выделено 2048 МБ памяти, подключён диск 20 ГБ, аудио отключено, выбран сетевой режим Internal Network с именем intnet (см. @fig-windows-initial).

#figure(image("img/05-windows-config.png", width: 100%), caption: [Начальная конфигурация виртуальной машины Windows]) <fig-windows-initial>

Для установки выбран незанятый виртуальный диск объёмом 20 ГБ (см. @fig-windows-disk). Разделы создаются установщиком автоматически.

#figure(
  image("img/06-windows-install-disk.png", width: 100%),
  caption: [Выбор виртуального диска для установки Windows 10],
) <fig-windows-disk>

Установка Windows завершена, выполнен вход в систему. Рабочий стол представлен на @fig-windows-desktop.

#figure(
  image("img/07-windows-desktop.png", width: 100%),
  caption: [Рабочий стол Windows 10 после установки],
) <fig-windows-desktop>

== Дополнения гостевой ОС Windows
Имя компьютера Windows изменено на WS-MMA-win. Через меню Devices → Insert Guest Additions CD image подключён образ дополнений. С виртуального компакт-диска запущен VBoxWindowsAdditions.exe и установлен Oracle VirtualBox Guest Additions 7.2.20. Завершение установки показано на @fig-windows-additions. После установки выполнена перезагрузка гостевой ОС.

#figure(
  image("img/08-windows-guest-additions.png", width: 100%),
  caption: [Завершение установки Guest Additions в Windows],
) <fig-windows-additions>

== Дополнения гостевой ОС Ubuntu
Для загрузки зависимостей сетевой адаптер Ubuntu временно переведён в режим NAT. Подготовлены средства сборки и заголовки текущего ядра:

```sh
sudo apt update
sudo apt install -y build-essential dkms linux-headers-$(uname -r)
```

После подключения образа Guest Additions и установки дополнений выполнена перезагрузка. Результат проверен командами:

```sh
VBoxControl --version
lsmod | grep vbox
```

VBoxControl сообщает версию 7.2.20r175154, а список модулей содержит vboxguest (см. @fig-ubuntu-additions). Это подтверждает наличие утилиты дополнений и загрузку гостевого модуля ядра. Работа общей папки проверена в отдельном разделе отчёта.

#figure(
  image("img/09-ubuntu-guest-additions.png", width: 100%),
  caption: [Проверка Guest Additions в Ubuntu после перезагрузки],
) <fig-ubuntu-additions>

= Настройка сетевых интерфейсов
Задание по пунктам 9–32 руководства. Во внутренней сети назначить Windows адрес 192.168.99.1, Ubuntu — 192.168.99.2. Проверить взаимную доступность и доступ к внешним ресурсам.

Затем проверить автоматическое получение адресов и режим виртуального адаптера хоста, включая дополнительный адаптер 192.168.99.1 и диапазон DHCP 192.168.99.10–192.168.99.77. Проверить NAT, общую сеть NatNetwork 10.45.33.0/24 и раздельные сети с NatNetwork1 10.22.77.0/24.

== Внутренняя сеть со статическими адресами
Для проверки внутренней сети заданы адреса Windows 192.168.99.1 и Ubuntu 192.168.99.2 с маской 255.255.255.0. Шлюз по умолчанию и DNS-серверы не указаны. Настройки Windows представлены на @fig-win-static, Ubuntu — на @fig-ubuntu-static.

#figure(
  image("img/10-windows-static-ip.png", width: 100%),
  caption: [Статические параметры IPv4 в Windows],
) <fig-win-static>

#figure(
  image("img/11-ubuntu-static-ip.png", width: 100%),
  caption: [Статические параметры IPv4 в Ubuntu],
) <fig-ubuntu-static>

Проверка из Windows выполнена командами ipconfig, ping -n 4 192.168.99.2 и ping -n 4 1.1.1.1 (см. @fig-win-internal-ping). Ubuntu ответила на четыре запроса из четырёх, потерь нет. Для внешнего адреса получена ошибка «General failure».

#figure(
  image("img/12-windows-internal-ping.png", width: 100%),
  caption: [Проверка внутренней сети и внешнего адреса из Windows],
) <fig-win-internal-ping>

В Ubuntu выполнены команды ip -4 addr, ping -c 4 -W 2 192.168.99.1 и ping -c 4 -W 2 1.1.1.1 (см. @fig-ubuntu-internal-ping). Интерфейс enp0s3 имеет адрес 192.168.99.2/24. Windows не ответила ни на один из четырёх запросов. При обращении к внешнему адресу получена ошибка «Network is unreachable». По результату проверки в браузере сайт https://www.itmo.ru не открывается.

#figure(
  image("img/13-ubuntu-internal-ping.png", width: 100%),
  caption: [Проверка внутренней сети и внешнего адреса из Ubuntu],
) <fig-ubuntu-internal-ping>

Успешный ping из Windows в Ubuntu подтверждает обмен пакетами между ВМ. Отсутствие ответа на запросы, инициированные Ubuntu, предположительно связано с фильтрацией входящих ICMP Echo Request брандмауэром Windows; на этапе проверки внутренней сети настройки брандмауэра оставлены без изменений. Доступ к внешним ресурсам отсутствует, поскольку внутренняя сеть изолирована, а шлюз для выхода за пределы подсети не настроен.

== Виртуальный адаптер хоста и DHCP
В обеих гостевых ОС включено автоматическое получение адресов. В Network Manager настроена сеть Host-only: адрес адаптера хоста 192.168.56.1/24, DHCP-сервера — 192.168.56.100, диапазон выдачи — 192.168.56.101–192.168.56.254. Настройки приведены на @fig-host-adapter и @fig-host-dhcp.

#figure(image("img/15-host-only-adapter.png", width: 100%), caption: [Параметры виртуального адаптера хоста]) <fig-host-adapter>
#figure(image("img/16-host-only-dhcp.png", width: 100%), caption: [Параметры DHCP-сервера сети Host-only]) <fig-host-dhcp>

После подключения ВМ к этому адаптеру Windows получила адрес 192.168.56.101/24, Ubuntu — 192.168.56.102/24. Оба адреса входят в заданный диапазон DHCP. Результаты команд ipconfig и ip -4 addr представлены на @fig-win-host-ip и @fig-ubuntu-host-ip. В Windows шлюз по умолчанию отсутствует.

#figure(image("img/17-windows-host-only-ip.png", width: 100%), caption: [Адрес Windows в сети Host-only]) <fig-win-host-ip>
#figure(image("img/18-ubuntu-host-only-ip.png", width: 100%), caption: [Динамический адрес Ubuntu в сети Host-only]) <fig-ubuntu-host-ip>

== Дополнительная сеть Host-only
На хосте Arch Linux для разрешения подсети задания выполнены команды:

```sh
sudo mkdir -p /etc/vbox
echo '* 192.168.56.0/21 192.168.99.0/24' | sudo tee -a /etc/vbox/networks.conf
```

Создан дополнительный виртуальный адаптер хоста с адресом 192.168.99.1 и маской 255.255.255.0 (см. @fig-host99-adapter). Включён DHCP-сервер с адресом 192.168.99.2 и диапазоном выдачи 192.168.99.10–192.168.99.77 (см. @fig-host99-dhcp).

#figure(image("img/19-host-only-99-adapter.png", width: 100%), caption: [Адрес дополнительного адаптера Host-only]) <fig-host99-adapter>
#figure(image("img/20-host-only-99-dhcp.png", width: 100%), caption: [Диапазон DHCP дополнительной сети Host-only]) <fig-host99-dhcp>

=== Проверка связи и влияние брандмауэра Windows
В дополнительной сети Windows получила по DHCP адрес 192.168.99.11/24, Ubuntu — 192.168.99.10/24. Оба адреса входят в заданный диапазон. Из Windows успешно выполнены ping до Ubuntu и до адаптера хоста 192.168.99.1: в каждом случае получены четыре ответа из четырёх, потерь нет. Обращение к 1.1.1.1 завершилось ошибкой «General failure» (см. @fig-win-host99-tests).

#figure(image("img/21-windows-host99-tests.png", width: 100%), caption: [Проверка связи из Windows в дополнительной сети Host-only]) <fig-win-host99-tests>

При первоначальной проверке из Ubuntu Windows не отвечала на ping. Для проверки причины в Windows PowerShell с правами администратора временно отключён брандмауэр:

```powershell
Set-NetFirewallProfile -Profile Domain,Private,Public -Enabled False
```

После отключения повторён запрос из Ubuntu:

```sh
ping -c 4 -W 2 192.168.99.11
```

Получены четыре ответа из четырёх, потери составили 0 %, среднее время ответа — 0,410 мс. Проверка адреса хоста 192.168.99.1 также успешна: четыре ответа, потерь нет. При обращении к 1.1.1.1 получена ошибка «Network is unreachable» (см. @fig-ubuntu-host99-tests).

#figure(image("img/22-ubuntu-host99-tests.png", width: 100%), caption: [Проверка связи из Ubuntu после отключения брандмауэра Windows]) <fig-ubuntu-host99-tests>

Появление ответов после отключения брандмауэра подтверждает, что причиной недоступности Windows по ping была фильтрация входящих ICMP Echo Request, а не отсутствие сетевого соединения между ВМ. Брандмауэр пропускал ответы на запросы, инициированные самой Windows, поэтому ранее ping из Windows в Ubuntu работал. Режим Host-only обеспечивает связь ВМ между собой и с хостом, но сам по себе не предоставляет выход в Интернет. Отключение брандмауэра не создаёт отсутствующий маршрут во внешнюю сеть.

После диагностики брандмауэр Windows оставлен выключенным для последующих сетевых экспериментов.

== Режим NAT
Оба сетевых адаптера ВМ переключены в режим NAT; в гостевых системах сохранено автоматическое получение сетевых параметров. Windows и Ubuntu получили одинаковые IPv4-адреса 10.0.2.15/24. В Windows отображается шлюз 10.0.2.2. Результаты команд ipconfig и ip -4 addr, а также проверки внешнего адреса приведены на @fig-win-nat и @fig-ubuntu-nat.

#figure(image("img/24-windows-nat.png", width: 100%), caption: [Адрес Windows и проверка внешнего адреса в режиме NAT]) <fig-win-nat>
#figure(image("img/26-ubuntu-nat.png", width: 100%), caption: [Адрес Ubuntu и проверка внешнего адреса в режиме NAT]) <fig-ubuntu-nat>

При выполнении ping до 1.1.1.1 в каждой системе получено четыре ответа из четырёх, потерь нет. Среднее время ответа составило 24 мс в Windows и 39,533 мс в Ubuntu. Сайт Университета ИТМО успешно открыт в Microsoft Edge и Firefox (см. @fig-win-nat-web и @fig-ubuntu-nat-web), что подтверждает доступ к внешнему веб-ресурсу из обеих гостевых ОС.

#figure(image("img/25-windows-itmo-ru.png", width: 100%), caption: [Сайт ИТМО в Windows при использовании NAT]) <fig-win-nat-web>
#figure(image("img/27-ubuntu-itmo-ru.png", width: 100%), caption: [Сайт ИТМО в Ubuntu при использовании NAT]) <fig-ubuntu-nat-web>

Согласно руководству Oracle по сетевым режимам (источник 3), совпадение адресов не вызывает конфликта: обычный режим NAT создаёт отдельную изолированную сеть для каждого адаптера ВМ. Общая локальная сеть между ВМ при этом не образуется. Ping адреса 10.0.2.15 из любой из этих систем обращался бы к ней самой и не являлся бы проверкой связи с другой ВМ. Прямая связь между ВМ по этим адресам в данной конфигурации не обеспечивается; это объяснение устройства режима, а не результат отдельного межмашинного ping.

== Общая сеть NAT Network
Создана сеть NatNetwork с префиксом 10.45.33.0/24 и включённым DHCP (см. @fig-natnetwork-config). Обе ВМ подключены к этой сети.

#figure(image("img/28-nat-network-config.png", width: 100%), caption: [Настройки общей сети NatNetwork]) <fig-natnetwork-config>

Windows получила адрес 10.45.33.4/24 и шлюз 10.45.33.1, Ubuntu — 10.45.33.3/24. Из Windows выполнены команды ipconfig, ping -n 4 10.45.33.3 и ping -n 4 1.1.1.1; из Ubuntu — ip -4 addr, ping -c 4 10.45.33.4 и ping -c 4 1.1.1.1. Результаты представлены на @fig-win-natnetwork и @fig-ubuntu-natnetwork.

#figure(image("img/29-windows-nat-network.png", width: 100%), caption: [Проверка общей сети NAT Network из Windows]) <fig-win-natnetwork>
#figure(image("img/30-ubuntu-nat-network.png", width: 100%), caption: [Проверка общей сети NAT Network из Ubuntu]) <fig-ubuntu-natnetwork>

Во всех четырёх проверках получены четыре ответа из четырёх, потери составили 0 %. Среднее время ответа Windows на запросы Ubuntu — 0,508 мс. Сайт ИТМО после обновления страницы открывается в обеих гостевых ОС. Таким образом, общая NAT Network обеспечивает связь между ВМ и доступ к внешним ресурсам. Проверки выполнены при выключенном брандмауэре Windows.

== Раздельные сети NAT Network
Создана вторая сеть NatNetwork1 с префиксом 10.22.77.0/24 и включённым DHCP (см. @fig-natnetwork1-config). К ней подключена Ubuntu; Windows оставлена в NatNetwork 10.45.33.0/24.

#figure(image("img/31-nat-network1-config.png", width: 100%), caption: [Настройки второй сети NatNetwork1]) <fig-natnetwork1-config>

Ubuntu получила адрес 10.22.77.3/24. Проверка ping -c 4 10.45.33.4 не получила ответов от Windows: отправлено четыре пакета, получено ноль, потери 100 %. Проверка прервана сочетанием Ctrl+C после отправки четырёх запросов. При выполнении ping -c 4 1.1.1.1 получены все четыре ответа, потерь нет, среднее время ответа — 33,684 мс (см. @fig-ubuntu-separate-nat).

#figure(image("img/32-ubuntu-separate-nat.png", width: 100%), caption: [Проверка связи из Ubuntu в отдельной NAT Network]) <fig-ubuntu-separate-nat>

Разделение ВМ по разным сетям NAT Network устранило прямую доступность Windows из Ubuntu при сохранении доступа Ubuntu к внешнему адресу. Отдельные сети NAT Network не объединяются автоматически в общую маршрутизируемую сеть. При проверке обратного направления из Windows командой ping -n 4 10.22.77.3 также не получено ответов: четыре запроса завершились сообщением «Request timed out», потери 100 % (см. @fig-win-separate-nat). Таким образом, отсутствие связи между ВМ подтверждено в обе стороны. После обновления страницы сайт ИТМО открывается в обеих ОС, что подтверждает сохранение доступа к Интернету.

#figure(image("img/33-windows-separate.png", width: 100%), caption: [Отсутствие ответов Ubuntu из Windows при использовании разных NAT Network]) <fig-win-separate-nat>

= Создание и восстановление снимков
Задание по пунктам 33–45 руководства: сохранить исходное состояние Windows, установить приложение и создать второй снимок, проверить восстановление, затем создать отдельную ветвь с МойОфис и конфигурацией 4096 МБ оперативной памяти и два процессора. В качестве приложения вместо приведённого в руководстве примера Яндекс Браузера выбран архиватор 7-Zip.

== Создание исходного снимка и снимка с 7-Zip
Создан исходный снимок New_OS_Windows. В гостевой Windows установлен архиватор 7-Zip, скачанный с официального сайта. Успешный запуск 7-Zip File Manager показан на @fig-windows-7zip.

#figure(image("img/34-windows-7zip.png", width: 100%), caption: [Установленный архиватор 7-Zip в Windows]) <fig-windows-7zip>

После установки приложения создан снимок OS_Windows_7zip. Windows выключена штатно. В разделе Snapshots отображается цепочка New_OS_Windows → OS_Windows_7zip → Current State (changed), см. @fig-windows-snapshots.

#figure(image("img/35-windows-snapshots.png", width: 100%), caption: [Исходный снимок Windows и снимок с установленным 7-Zip]) <fig-windows-snapshots>

== Восстановление исходного состояния
В разделе Snapshots выбран снимок New_OS_Windows и выполнена операция Restore без создания дополнительного снимка текущего состояния. После запуска Windows открыто окно Programs and Features командой appwiz.cpl. В списке установленных программ 7-Zip отсутствует, а Oracle VirtualBox Guest Additions 7.2.20 сохранён (см. @fig-windows-restored). Это подтверждает возврат к состоянию до установки архиватора.

#figure(image("img/36-windows-restored-no-7zip.png", width: 100%), caption: [Список программ после восстановления исходного снимка Windows]) <fig-windows-restored>

== Создание альтернативной ветви с PuTTY
После восстановления New_OS_Windows установлен и запущен SSH-клиент PuTTY (см. @fig-windows-putty). По выбранному варианту выполнения вместо указанного в пункте 42 МойОфис использован PuTTY. Это отступление от перечня программ руководства; проверка сохранения и восстановления состояния выполняется с другим приложением.

#figure(image("img/37-windows-putty.png", width: 100%), caption: [Запуск PuTTY в восстановленной Windows]) <fig-windows-putty>

Windows выключена, объём оперативной памяти увеличен до 4096 МБ, число виртуальных процессоров — до двух. В выключенном состоянии создан снимок OS_Windows_putty. Эти значения подтверждены конфигурацией снимка в файле WS_MMA_win.vbox. Дерево снимков содержит две ветви от New_OS_Windows: OS_Windows_7zip и OS_Windows_putty (см. @fig-windows-putty-snapshot).

#figure(image("img/38-windows-putty-snapshot.png", width: 100%), caption: [Две ветви снимков Windows с разными приложениями]) <fig-windows-putty-snapshot>

== Восстановление ветви с 7-Zip
Из ветви с PuTTY выполнено восстановление OS_Windows_7zip. После запуска Windows объём памяти вновь составляет 2048 МБ; сетевой адаптер восстановлен в режиме NAT (см. @fig-restored-config). В Programs and Features присутствует 7-Zip 26.04 (x64), PuTTY отсутствует, архиватор запускается (см. @fig-restored-7zip). Таким образом, восстановлены настройки ВМ и набор программ выбранного снимка, а изменения альтернативной ветви не перенесены в текущее состояние.

#figure(image("img/39-windows-restored-config.png", width: 100%), caption: [Конфигурация Windows после восстановления снимка с 7-Zip]) <fig-restored-config>
#figure(image("img/40-windows-restored-7zip.png", width: 100%), caption: [7-Zip присутствует, PuTTY отсутствует после восстановления снимка]) <fig-restored-7zip>

= Общая папка и буфер обмена
== Подключение общей папки
Общие папки реализуются средствами Guest Additions (источник 4). В обеих ВМ настроена общая папка public, соответствующая каталогу /home/mip3x/public на хосте Arch Linux. Включено автоматическое монтирование, доступ Full (см. @fig-win-shared-settings и @fig-ubuntu-shared-settings). Фактическое имя каталога записано строчными буквами.

#figure(image("img/41-windows-shared-folder.png", width: 100%), caption: [Настройка общей папки Windows]) <fig-win-shared-settings>
#figure(image("img/42-ubuntu-shared-folder.png", width: 100%), caption: [Настройка общей папки Ubuntu]) <fig-ubuntu-shared-settings>

В Windows файл lab1-test.txt открыт через ресурс `\\VBOXSVR\public`, в Ubuntu — из /media/sf_public. В обеих системах отображается одинаковое содержимое «Hello from host» (см. @fig-win-shared-file и @fig-ubuntu-shared-file). На скриншоте Ubuntu файл открыт через административный доступ; доступ обычного пользователя отдельно не подтверждён.

#figure(image("img/42-windows-shared-file.png", width: 100%), caption: [Чтение общего файла в Windows]) <fig-win-shared-file>
#figure(image("img/43-ubuntu-shared-file.png", width: 100%), caption: [Чтение общего файла в Ubuntu]) <fig-ubuntu-shared-file>

При загрузке Ubuntu обнаружена несогласованность файловой системы /dev/sda5. В оболочке initramfs выполнена команда fsck -f /dev/sda5 с подтверждением исправлений, затем exit. После исправления загрузка и чтение общей папки восстановлены.

== Буфер обмена и перетаскивание
В Ubuntu двунаправленный буфер обмена и Drag and Drop не включались. В итоговом выводе VBoxManage showvminfo оба параметра имеют значение disabled. Передача данных в этой гостевой ОС проверена через общую папку. Результатов проверки буфера обмена и Drag and Drop для Windows не зафиксировано; выполнение пункта 49 в отчёте не заявляется.

= Управление из командной строки
== Список виртуальных машин и запуск
Команды выполнены на хосте Arch Linux. Полный предоставленный протокол сохранён в сопроводительном файле vbox-cli-output.txt. Выполнена команда:

```sh
VBoxManage list vms
```

Получен список двух зарегистрированных ВМ:

```text
"WS_MMA_ubuntu" {0559d715-b5bf-4bec-818f-96d242fffdcc}
"WS_MMA_win" {39484d78-5fb4-4823-9e3b-e31c753a60ad}
```

Подготовлены скрипты start-ubuntu.sh и start-windows.sh. Содержимое первого:

```sh
#!/bin/sh
set -eu
exec VBoxManage startvm "WS_MMA_ubuntu" --type gui
```

Содержимое второго:

```sh
#!/bin/sh
set -eu
exec VBoxManage startvm "WS_MMA_win" --type gui
```

Скрипты запущены последовательно:

```sh
sh ./start-ubuntu.sh
sh ./start-windows.sh
VBoxManage list runningvms
```

Для каждой ВМ получено сообщение об успешном запуске:

```text
VM "WS_MMA_ubuntu" has been successfully started.
VM "WS_MMA_win" has been successfully started.
```

Команда list runningvms вывела обе машины с теми же UUID, что подтверждает их одновременную работу.

== Просмотр параметров виртуальной машины
Выполнена команда:

```sh
VBoxManage showvminfo "WS_MMA_ubuntu"
```

В предоставленном выводе зафиксированы состояние running, память 2048 МБ, один процессор, BIOS, графический контроллер VMSVGA, аппаратная виртуализация и Nested Paging. Единственный активный сетевой адаптер подключён к NatNetwork1. Общая папка public сопоставлена с /home/mip3x/public, доступна для записи и автоматически монтируется.

Поле Guest OS содержит Ubuntu 20.10 (Groovy Gorilla) (64-bit): это выбранный профиль VirtualBox, а не результат определения установленного выпуска Ubuntu. На момент команды Audio имело значение enabled, Clipboard Mode и Drag and drop Mode — disabled. Таким образом, в итоговой конфигурации Ubuntu аудио осталось включено вопреки пункту 4.1.3, а пункт 49 не выполнен для этой ВМ. Настройки оставлены без изменений. Значение Additions run level: 0 непосредственно после запуска само по себе не подтверждает отсутствие ранее установленных дополнений; их работа была проверена чтением общей папки.

#heading(numbering: none)[Заключение]
+ Созданы и запущены две ВМ с Ubuntu и Windows 10. В обеих системах установлены Guest Additions 7.2.20; в Ubuntu подтверждена загрузка модуля vboxguest.
+ Во внутренней сети Windows получила четыре ответа от Ubuntu без потерь, но обратные запросы остались без ответа. В сети Host-only после отключения брандмауэра Windows получены четыре ответа из четырёх в обратном направлении. Также подтверждена доступность хоста из обеих ВМ. Выход во внешнюю сеть при этих настройках отсутствовал.
+ Проверено получение адресов DHCP в двух сетях Host-only: 192.168.56.101 и 192.168.56.102, затем 192.168.99.11 и 192.168.99.10. Все адреса входят в настроенные диапазоны выдачи.
+ В обычном NAT обе ВМ получили адрес 10.0.2.15 в независимых сетях. В обеих ОС получены четыре ответа от 1.1.1.1 без потерь и открыт сайт ИТМО. В общей NatNetwork подтверждена также двусторонняя связь между ВМ без потерь. После переноса Ubuntu в NatNetwork1 межмашинные проверки завершились со 100 % потерь в обе стороны, при этом доступ к сайту сохранился.
+ Созданы три снимка Windows с двумя альтернативными ветвями. Восстановление исходного снимка удалило установленный 7-Zip из текущего состояния. Возврат к ветви 7-Zip восстановил архиватор и память 2048 МБ; PuTTY из другой ветви отсутствовал. Для ветви PuTTY сохранены 4096 МБ памяти и два процессора.
+ Настроена общая папка хоста public с автоматическим монтированием. Один и тот же файл с текстом «Hello from host» прочитан в Windows и Ubuntu.
+ Подготовлены два скрипта запуска ВМ. Обе машины успешно запущены через VBoxManage; их работа подтверждена списком runningvms, параметры Ubuntu получены командой showvminfo.
+ Зафиксированы отличия от полного задания: вместо МойОфис установлен PuTTY, в итоговой конфигурации Ubuntu аудио включено, буфер обмена и Drag and Drop отключены. Эти действия не представлены как выполненные требования.

#heading(numbering: none)[Литература]
+ Oracle. Virtual Networking // Oracle VirtualBox User Guide, Release 7.2. URL: https://docs.oracle.com/en/virtualization/virtualbox/7.2/user/networkingdetails.html (дата обращения: 15.10.2026).
+ Oracle. Guest Additions // Oracle VirtualBox User Guide, Release 7.2. URL: https://docs.oracle.com/en/virtualization/virtualbox/7.2/user/guestadditions.html (дата обращения: 15.10.2026).
