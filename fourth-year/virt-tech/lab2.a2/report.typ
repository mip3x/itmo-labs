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
#show raw.where(block: true): it => {
  set par(first-line-indent: 0pt, justify: false)
  set text(font: "Times New Roman", size: 14pt, hyphenate: false)
  block(width: 100%, breakable: true,
    it.text.split("\n").map(line => text(line)).join(linebreak()))
}
#show raw: set text(font: "Times New Roman", size: 14pt)
#let student = "Э э э"
#let group = "P3411"
#let teacher = "Белозубов Александр Владимирович"
#let teacher_position = "к.т.н., доцент"

#page(margin: (left: 30mm, right: 20mm, top: 10mm, bottom: 10mm), footer: none, numbering: none)[
  #set par(justify: false, first-line-indent: 0pt)
  #align(center)[
    Министерство науки и высшего образования Российской Федерации \
    Федеральное государственное автономное образовательное учреждение высшего образования \
    «Национальный исследовательский университет ИТМО»

    #v(21pt)
    Факультет программной инженерии и компьютерной техники
    #v(1fr)
    Лабораторная работа № 2 \
    Использование гипервизоров в Unix \
    по дисциплине «Технологии виртуализации» \
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
Цель работы — изучить средства виртуализации в Unix: GNOME Boxes, QEMU/KVM, libvirt, virt-manager и virt-install. Работа выполняется на одном физическом компьютере; для вложенных систем и проверки снимков выбираются легковесные дистрибутивы и приложения.

= Подготовка среды
== Настройки гостевой Ubuntu
Работа выполняется в гостевой Ubuntu, запущенной в VirtualBox на хосте Arch Linux. Гостевой системе выделены 4 ГиБ оперативной памяти и два виртуальных процессора. В настройках Processor включены PAE/NX и Nested VT-x/AMD-V (см. @fig-processor). Во вкладке Acceleration выбран интерфейс паравиртуализации KVM и включена Nested Paging (см. @fig-acceleration).

#figure(image("img/01-vbox-processor.png", width: 100%), caption: [Настройки процессора гостевой Ubuntu]) <fig-processor>
#figure(image("img/02-vbox-acceleration.png", width: 85%), caption: [Настройки ускорения гостевой Ubuntu]) <fig-acceleration>

== Дополнительный диск
Создан динамический диск VDI объёмом 30 ГиБ и подключён к контроллеру SATA. Файл диска расположен на хосте по пути /var/lib/virt-tech-lab2-a2/lab2-data.vdi (см. @fig-storage).

#figure(image("img/03-vbox-storage.png", width: 100%), caption: [Подключение дополнительного диска к контроллеру SATA]) <fig-storage>

В Ubuntu новый накопитель определился как /dev/sdb. Созданы таблица разделов GPT и раздел /dev/sdb1 с файловой системой ext4 и меткой LAB2_DATA. Раздел подключён к /home/user/VM для хранения дисков вложенных виртуальных машин. Системный диск /dev/sda не изменялся. Результат проверки представлен на @fig-disk.

#figure(image("img/05-ubuntu-disk.png", width: 100%), caption: [Дополнительный диск и точка монтирования в Ubuntu]) <fig-disk>

== Общая папка Soft
В VirtualBox настроена общая папка Soft с автоматическим подключением и разрешённой записью (см. @fig-soft-settings). На хосте ей соответствует каталог /home/mip3x/code/itmo-labs/fourth-year/virt-tech/lab2.a2/Soft; в Ubuntu папка подключена к /media/sf_Soft.

#figure(image("img/04-vbox-shared-folder.png", width: 100%), caption: [Настройки общей папки Soft]) <fig-soft-settings>

Из Ubuntu создан файл shared-test.txt со строкой «Hello from Ubuntu». Затем этот файл прочитан на хосте; содержимое совпало (см. @fig-soft-guest и @fig-soft-host).

#figure(image("img/06-soft-ubuntu.png", width: 100%), caption: [Запись и чтение файла в общей папке из Ubuntu]) <fig-soft-guest>
#figure(image("img/07-soft-host.png", width: 100%), caption: [Чтение общего файла на хосте]) <fig-soft-host>

== Образ гостевой системы
Первоначально проверен имеющийся образ Alpine Standard 3.24.2 x86_64 (см. @fig-iso). Для дальнейших опытов с официального сайта Alpine Linux скачан Alpine Standard 3.24.4 x86_64; файл сохранён в ~/lab2/iso/alpine-standard-3.24.4-x86_64.iso. Образ поддерживает запуск без установки в текстовом Live-режиме. Использование официального источника вместо mirror.yandex.ru является отличием от задания; Alpine на указанном зеркале не обнаружена.

#figure(image("img/08-alpine-downloaded.png", width: 100%), caption: [Проверка файла установочного образа Alpine Linux]) <fig-iso>

== Переход к QEMU/KVM
После нажатия Create в GNOME Boxes при запуске вложенной Alpine в Ubuntu под VirtualBox внешняя ВМ перестала отвечать. В журнале ядра Arch зафиксирован Oops: NULL pointer dereference в потоке EMT-0. Сообщение VirtualBox Guest seems to be unresponsive подтверждает прекращение ответов гостя. Arch сохранял возможность выполнения команд; полное падение хоста в данном повторном опыте не заявляется. Конкретная первопричина ошибки по имеющейся трассировке не установлена.

Для продолжения выключенные диски Ubuntu и данных конвертированы в независимые qcow2-копии. Исходные VDI сохранены. Проверка qemu-img check для обеих копий завершилась сообщением No errors were found on the image. Проверка относится к структуре образа и не заменяет проверку гостевой файловой системы.

```sh
qemu-img convert -p -f vdi -O qcow2 \
  ~/VM/WS_MMA_ubuntu/WS_MMA_ubuntu.vdi \
  /var/lib/virt-tech-lab2-a2/ubuntu.qcow2
qemu-img convert -p -f vdi -O qcow2 \
  /var/lib/virt-tech-lab2-a2/lab2-data.vdi \
  /var/lib/virt-tech-lab2-a2/data.qcow2
```

Ubuntu запущена на Arch через QEMU с KVM, параметрами -cpu host, -m 4096 и -smp 2. Вложенные опыты далее выполнялись внутри этой Ubuntu. На внешнем пользовательском NAT настроен проброс localhost:1800 к SSH-порту 22 Ubuntu. Для доступа к терминалу использовано `ssh -p 1800 user@127.0.0.1`.

В гостевой Ubuntu повторно проверены kvm-ok и диски. Дополнительный раздел /dev/sdb1 подключён к /home/user/VM. Для постоянного монтирования в /etc/fstab добавлена запись:

```sh
UUID=4e53f49e-6e2b-4bd4-8091-a395f101efdd /home/user/VM ext4 defaults,nofail 0 2
```

На @fig-qemu-host представлен результат данного этапа.

#figure(image("img/10-qemu-ubuntu-kvm-disk.png", width: 100%), caption: [Проверка KVM и дополнительного диска в Ubuntu под QEMU]) <fig-qemu-host>

Общая папка Soft проверена на этапе VirtualBox; её работа после миграции под QEMU не заявляется. Для последующих опытов ISO уже находился на системном диске Ubuntu.

= Работа с GNOME Boxes
== Запуск Live-системы и свойства

```sh
sudo apt install gnome-boxes cpu-checker
kvm-ok
ls -l /dev/kvm
```

В Ubuntu устройство /dev/kvm доступно, kvm-ok сообщил KVM acceleration can be used. В Boxes выбран ISO Alpine, создана ВМ с памятью около 541,5 МиБ. Ровно 512 МиБ ползунком не выставлялись. Диск динамический; его предельная ёмкость не означает немедленного расходования такого объёма на хосте.

На @fig-box-create представлен результат данного этапа.

#figure(image("img/09-boxes-create.png", width: 100%), caption: [Параметры создаваемой ВМ в Boxes]) <fig-box-create>

Alpine загружена с ISO без установки, выполнен вход root в Live-среду. Проверены выпуск системы, ядро и память.

На @fig-box-live представлен результат данного этапа.

#figure(image("img/11-boxes-alpine-live.png", width: 100%), caption: [Alpine Linux в Live-режиме внутри Boxes]) <fig-box-live>

Исследованы вкладки General и System: имя ВМ, сведения о системе, параметры памяти и диска и показатели текущей нагрузки. Boxes предоставляет средства запуска, остановки, настройки ресурсов и наблюдения за работой ВМ; доступность изменений зависит от её состояния.

На @fig-box-general представлен результат данного этапа.

#figure(image("img/12-boxes-general.png", width: 100%), caption: [Общие свойства ВМ в Boxes]) <fig-box-general>

На @fig-box-system представлен результат данного этапа.

#figure(image("img/13-boxes-system.png", width: 100%), caption: [Системные параметры и нагрузка ВМ в Boxes]) <fig-box-system>

== Подключение к Helios

В Boxes внутри Ubuntu создано удалённое SSH-подключение по адресу `ssh://s409066@helios.cs.ifmo.ru:2222`. Вход выполнен по паролю, без проброса SSH-agent. После входа командами hostname, whoami и uname -sr проверены сервер, учётная запись и ОС. Пароль в отчёте не приводится.

На @fig-helios-address представлен результат данного этапа.

#figure(image("img/14-boxes-helios-address.png", width: 100%), caption: [Адрес подключения к Helios]) <fig-helios-address>

На @fig-helios-session представлен результат данного этапа.

#figure(image("img/15-boxes-helios-session.png", width: 100%), caption: [Успешный сеанс Helios в Boxes]) <fig-helios-session>

== Локальный мост и удалённый рабочий стол

Вместо компьютера соседа использована Windows на том же Arch. Для неё создан отдельный записываемый qcow2-слой поверх исходного VDI; базовый диск требуется сохранять. Windows запущена через QEMU/KVM с 2048 МиБ RAM и двумя CPU. Включены Remote Desktop и соответствующая служба.

На @fig-rdp-enable представлен результат данного этапа.

#figure(image("img/16-windows-rdp-enabled.png", width: 100%), caption: [Включение удалённого рабочего стола Windows]) <fig-rdp-enable>

На Arch создан локальный мост br-lab2 и два TAP-порта. Они соединяют вторые адаптеры Ubuntu и Windows, не меняя физическое подключение хоста. Первые адаптеры используют пользовательский NAT QEMU для выхода в интернет.

```sh
sudo ip link add br-lab2 type bridge
sudo ip link set br-lab2 up
sudo ip tuntap add dev tap-lab2-u mode tap user "$USER"
sudo ip tuntap add dev tap-lab2-w mode tap user "$USER"
sudo ip link set tap-lab2-u master br-lab2
sudo ip link set tap-lab2-w master br-lab2
sudo ip link set tap-lab2-u up
sudo ip link set tap-lab2-w up
```

Второй адаптер Ubuntu ens5 получил 172.29.88.2/24, Windows Ethernet 3 — 172.29.88.3/24. Шлюз и DNS для этой локальной сети не задавались. При первоначальном тайм-ауте обнаружен автоматический адрес 169.254… вместо настроенного; после назначения Windows адреса 172.29.88.3 проверка nc -vz -w 3 172.29.88.3 3389 завершилась succeeded. Служба TermService работала и слушала TCP-порт 3389.

На @fig-win-ip представлен результат данного этапа.

#figure(image("img/17-windows-lan-ip.png", width: 100%), caption: [Настройки адреса Windows в локальной сети]) <fig-win-ip>

В Boxes на Ubuntu создано подключение rdp://172.29.88.3. После аутентификации открыта Windows, выполнены hostname и whoami. Клиентом RDP выступала Boxes внутри Ubuntu, сервером — Windows; соединение шло напрямую через мост. Проброс localhost:13389 на Arch для данного подключения не использовался.

На @fig-rdp-address представлен результат данного этапа.

#figure(image("img/18-boxes-rdp-address.png", width: 100%), caption: [Адрес RDP-подключения в Boxes]) <fig-rdp-address>

На @fig-rdp-session представлен результат данного этапа.

#figure(image("img/19-boxes-rdp-session.png", width: 100%), caption: [Рабочий стол Windows через RDP в Boxes]) <fig-rdp-session>

= Работа с QEMU
== Создание диска и установка Alpine

Все команды данного раздела выполнены внутри Ubuntu. Установлены qemu-kvm и qemu-utils; версия QEMU — 4.2.1. Отдельная ВМ создавалась вручную, без регистрации в libvirt.

```sh
qemu-img create -f qcow2 ~/VM/alpine-qemu.qcow2 10G
qemu-img info ~/VM/alpine-qemu.qcow2
```

На @fig-qemu-disk представлен результат данного этапа.

#figure(image("img/20-qemu-disk.png", width: 100%), caption: [Создание qcow2-диска 10 ГиБ]) <fig-qemu-disk>

QEMU запущен с -enable-kvm, -cpu host, памятью 512 МиБ, одним CPU, созданным диском и ISO через -cdrom. После загрузки Live-среды выполнен setup-alpine, выбран основной диск sda и режим sys. Подтверждена очистка только виртуального диска 10 ГиБ. По завершении Alpine штатно выключена.

На @fig-qemu-install представлен результат данного этапа.

#figure(image("img/21-alpine-installed.png", width: 100%), caption: [Завершение установки Alpine в QEMU]) <fig-qemu-install>

== Запуск установленной ОС

```sh
qemu-system-x86_64 \
  -enable-kvm -cpu host -m 2048 -smp 1 \
  -drive file="$HOME/VM/alpine-qemu.qcow2",format=qcow2 \
  -boot order=c \
  -netdev user,id=net0,hostfwd=tcp:127.0.0.1:2222-:22 \
  -device e1000,netdev=net0,mac=16:10:20:22:20:09 \
  -display gtk -full-screen
```

Система загружена с диска без ISO. Проверены выпуск Alpine, объём памяти и адрес интерфейса. Полноэкранный режим задан -full-screen. MAC из задания 17:10:20:22:20:09 является групповым, поэтому применён индивидуальный 16:10:20:22:20:09. Проброс 2222 относится к пользовательской сети внутри Ubuntu; отдельный SSH-вход в эту вручную запущенную ВМ не заявляется.

На @fig-qemu-full представлен результат данного этапа.

#figure(image("img/22-qemu-install-fullscreen.png", width: 100%), caption: [Полноэкранный запуск установленной Alpine в QEMU]) <fig-qemu-full>

= Работа с virt-manager и libvirt
== Подготовка и создание ВМ

```sh
sudo apt install virt-manager libvirt-daemon-system \
  libvirt-clients virtinst libosinfo-bin bridge-utils
sudo usermod -aG libvirt,kvm "$USER"
sudo systemctl enable --now libvirtd
id
systemctl is-active libvirtd
kvm-ok
```

После повторного входа в учётную запись подтверждены группы libvirt и kvm, состояние libvirtd active и доступность аппаратного ускорения.

На @fig-libvirt-check представлен результат данного этапа.

#figure(image("img/23-libvirt-check.png", width: 100%), caption: [Проверка libvirt, групп пользователя и KVM]) <fig-libvirt-check>

В системном подключении QEMU/KVM создана Malyshev_P3411: 512 МиБ RAM, один CPU, основной qcow2-диск 4 ГиБ /home/user/VM/alpine-vmm.qcow2, сеть default с NAT. Для установки выбран ISO Alpine 3.24.4.

На @fig-vmm-create представлен результат данного этапа.

#figure(image("img/24-vmm-create.png", width: 100%), caption: [Создание ВМ в virt-manager]) <fig-vmm-create>

```sh
qemu-img create -f qcow2 /home/user/VM/vmm-extra.qcow2 10M
```

Через Add Hardware подключён дополнительный VirtIO-диск 10 МиБ. В госте основной диск определился как sda, дополнительный — vda. Alpine установлена через setup-alpine и setup-disk на sda в режиме sys; дополнительный диск не форматировался. После выключения ISO извлечён, выполнена загрузка с установленного диска.

На @fig-vmm-extra представлен результат данного этапа.

#figure(image("img/25-vmm-extra-disk.png", width: 100%), caption: [Дополнительный виртуальный диск 10 МиБ]) <fig-vmm-extra>

На @fig-vmm-boot представлен результат данного этапа.

#figure(image("img/27-vmm-disk-boot.png", width: 100%), caption: [Загрузка установленной Alpine в virt-manager]) <fig-vmm-boot>

В свойствах virt-manager доступны настройки CPU, памяти, загрузки, дисков, сетевых адаптеров и графики; графики позволяют наблюдать нагрузку. Для сетевых устройств можно выбирать виртуальную сеть, мост и модель адаптера. В опыте использована NAT-сеть default и модель e1000.

== Снимки и восстановление

На выключенной ВМ создан исходный снимок clean-before-nano. Имя снимка сохранено, хотя в качестве приложения далее выбран Neovim. Это исходное состояние до установки редактора.

На @fig-snapshot-clean представлен результат данного этапа.

#figure(image("img/28-vmm-snapshot-clean.png", width: 100%), caption: [Создание исходного снимка]) <fig-snapshot-clean>

После запуска раскомментирован репозиторий community той же версии Alpine и установлен Neovim вместо тяжёлого браузера из задания.

```sh
sed -i '/\/community/s/^#//' /etc/apk/repositories
apk update
apk add neovim
nvim --version | head -n 3
```

На @fig-neovim представлен результат данного этапа.

#figure(image("img/29-vmm-neovim-installed.png", width: 100%), caption: [Установленный Neovim]) <fig-neovim>

Alpine штатно выключена, создан второй снимок neovim-installed. Снимки фиксировались на выключенной ВМ для согласованности дисков.

На @fig-snapshots представлен результат данного этапа.

#figure(image("img/30-vmm-snapshots.png", width: 100%), caption: [Два состояния виртуальной машины]) <fig-snapshots>

Через Revert to snapshot восстановлен clean-before-nano. После запуска проверены apk info -e neovim и command -v nvim; отсутствие редактора подтверждает возврат к исходному состоянию.

На @fig-revert представлен результат данного этапа.

#figure(image("img/31-reverted-snapshot.png", width: 100%), caption: [Выбор восстановленного снимка]) <fig-revert>

На @fig-no-neovim представлен результат данного этапа.

#figure(image("img/32-vmm-snapshot-restored.png", width: 100%), caption: [Отсутствие Neovim после отката]) <fig-no-neovim>

== Управление через virsh и полный клон

```sh
virsh -c qemu:///system list --all
virsh -c qemu:///system dominfo Malyshev_P3411
virsh -c qemu:///system domblklist Malyshev_P3411
virsh -c qemu:///system domiflist Malyshev_P3411
```

Вывод подтвердил 512 МиБ памяти, один CPU, основной диск hda и дополнительный vda. Сетевой адаптер e1000 подключён к default, MAC 52:54:00:e0:ad:94. UUID ВМ: 15ef91ac-7e59-4fd7-a44a-33b2f165f3eb.

На @fig-virsh-info представлен результат данного этапа.

#figure(image("img/33-virsh-info.png", width: 100%), caption: [Сведения о домене, дисках и сети через virsh]) <fig-virsh-info>

```sh
virsh -c qemu:///system reboot Malyshev_P3411
virsh -c qemu:///system autostart Malyshev_P3411
virsh -c qemu:///system shutdown Malyshev_P3411
virsh -c qemu:///system list --all
```

Проверены перезагрузка, включение автозапуска и выключение. Команда virsh stop из методички отсутствует; shutdown запрашивает штатное завершение гостя. destroy означает немедленную остановку, а не штатный halt; для данного опыта она не требовалась.

На @fig-virsh-control представлен результат данного этапа.

#figure(image("img/34-virsh-control.png", width: 100%), caption: [Управление состоянием ВМ через virsh]) <fig-virsh-control>

```sh
virt-clone --connect qemu:///system \
  --original Malyshev_P3411 --name Malyshev_P3411_clone \
  --file /home/user/VM/alpine-vmm-clone.qcow2 \
  --file /home/user/VM/vmm-extra-clone.qcow2
```

Создан полный клон с отдельными копиями обоих дисков; регистрация и пути проверены через list --all и domblklist. Клон оставлен выключенным.

На @fig-clone представлен результат данного этапа.

#figure(image("img/35-virsh-clone.png", width: 100%), caption: [Полный клон и его диски]) <fig-clone>

= Использование virt-install
== Профили операционных систем

В Ubuntu команда virt-install предоставляется пакетом virtinst. Через osinfo-query получены и отсортированы профили Windows и Fedora.

```sh
osinfo-query os | grep -i windows | sort
osinfo-query os | grep -i fedora | sort
```

На @fig-osinfo-win представлен результат данного этапа.

#figure(image("img/36-osinfo-windows.png", width: 100%), caption: [Профили Windows в базе libosinfo]) <fig-osinfo-win>

На @fig-osinfo-fedora представлен результат данного этапа.

#figure(image("img/37-osinfo-fedora.png", width: 100%), caption: [Профили Fedora в базе libosinfo]) <fig-osinfo-fedora>

Отсортированные списки сохранены в файлы на дополнительном диске:

```sh
osinfo-query os | grep -i windows | sort > ~/VM/osinfo-windows.txt
osinfo-query os | grep -i fedora | sort > ~/VM/osinfo-fedora.txt
ls -lh ~/VM/osinfo-*.txt
```

Наличие файлов и чтение списка Fedora показаны на @fig-osinfo-files. Размеры файлов составили около 3,3 КиБ для Windows и 4,7 КиБ для Fedora.

#figure(image("img/43-osinfo-files.png", width: 100%), caption: [Сохранённые списки профилей операционных систем]) <fig-osinfo-files>

В установленной базе доступны профили Alpine до alpinelinux3.8. Этот профиль выбран для оборудования новой ВМ; он не меняет версию установленной из ISO Alpine 3.24.4. На дополнительном разделе перед созданием ВМ оставалось около 27 ГБ.

== Создание и установка отдельной системы

```sh
virt-install \
  --connect qemu:///system \
  --name Malyshev_P3411_virtinstall \
  --virt-type kvm --memory 2048 --vcpus 2 \
  --cpu host-passthrough --os-variant alpinelinux3.8 \
  --disk path=/home/user/VM/alpine-virtinstall.qcow2,size=8,format=qcow2,bus=virtio \
  --cdrom /home/user/lab2/iso/alpine-standard-3.24.4-x86_64.iso \
  --network network=default,model=virtio \
  --graphics spice
```

Параметр --connect выбирает системное подключение libvirt внутри Ubuntu; --name задаёт имя; --virt-type kvm включает аппаратную виртуализацию; --memory и --vcpus выделяют 2048 МиБ и два CPU. --cpu host-passthrough передаёт возможности CPU L1. --os-variant выбирает профиль устройств. --disk создаёт диск qcow2 8 ГиБ с VirtIO, --cdrom подключает ISO, --network выбирает NAT-сеть default и VirtIO-адаптер. --graphics spice задаёт графическую консоль. В отличие от примера методички использованы Alpine и SPICE, консоль открыта для ручной установки.

На @fig-virtinstall-create представлен результат данного этапа.

#figure(image("img/38-virtinstall-create.png", width: 100%), caption: [Создание ВМ командой virt-install]) <fig-virtinstall-create>

В новой ВМ выполнена отдельная установка setup-alpine на vda размером 8 ГиБ в режиме sys. Имя системы — alpine-virtinstall. После успешной установки ВМ выключена, ISO отключён и выполнена загрузка с диска.

На @fig-virtinstall-install представлен результат данного этапа.

#figure(image("img/39-virtinstall-installed.png", width: 100%), caption: [Завершение установки новой Alpine]) <fig-virtinstall-install>

После загрузки подтверждены hostname alpine-virtinstall, доступная память около 1972 МиБ и адрес eth0 192.168.122.76/24.

На @fig-virtinstall-boot представлен результат данного этапа.

#figure(image("img/40-virtinstall-boot.png", width: 100%), caption: [Проверка установленной ВМ и её адреса]) <fig-virtinstall-boot>

== SSH и фоновый запуск

В Alpine создан обычный пользователь student; служба sshd запущена и добавлена в default. Вход выполнен из Ubuntu по паролю. Пароли в отчёте не приводятся.

```sh
adduser student
rc-service sshd start
rc-update add sshd default
```

В терминале Ubuntu выполнено подключение:

```sh
ssh student@192.168.122.76
hostname
whoami
uname -r
```

На @fig-virtinstall-ssh представлен результат данного этапа.

#figure(image("img/41-virtinstall-ssh.png", width: 100%), caption: [SSH из Ubuntu в новую Alpine]) <fig-virtinstall-ssh>

После штатного выключения Alpine графическое окно закрыто. В Ubuntu выполнен запуск через libvirt без открытия консоли:

```sh
virsh -c qemu:///system start Malyshev_P3411_virtinstall
virsh -c qemu:///system autostart Malyshev_P3411_virtinstall
virsh -c qemu:///system list --all
ssh student@192.168.122.76
```

Успешное повторное SSH-подключение подтвердило фоновую работу ВМ. Autostart настраивает её запуск при старте службы libvirt. Адрес выдан DHCP и в дальнейшем может измениться. Для SSH необходимы работающая сеть между L1 и L2, служба sshd и учётная запись с разрешённой аутентификацией. Проброс порта здесь не нужен: Ubuntu обращается непосредственно к адресу вложенной ВМ.

На @fig-background-ssh представлен результат данного этапа.

#figure(image("img/42-virtinstall-background-ssh.png", width: 100%), caption: [Фоновый запуск и повторное SSH-подключение]) <fig-background-ssh>

= Дополнительное задание: Q35 и автоматически сформированный XML
== Создание виртуальной машины
В Ubuntu через системное подключение libvirt создана отдельная ВМ Malyshev_P3411_q35. В отличие от предыдущего опыта явно указан тип машины q35. Для исследования конфигурации установка ОС на диск не требовалась.

```sh
virt-install \
  --connect qemu:///system \
  --name Malyshev_P3411_q35 \
  --virt-type kvm --machine q35 \
  --memory 512 --vcpus 1 \
  --os-variant alpinelinux3.8 \
  --disk path=/home/user/VM/alpine-q35.qcow2,size=4,format=qcow2,bus=virtio \
  --cdrom /home/user/lab2/iso/alpine-standard-3.24.4-x86_64.iso \
  --network network=default,model=virtio \
  --graphics spice --noautoconsole
```

Скрипт создания приведён на @fig-q35-create. Выделены 512 МиБ памяти, один CPU и диск qcow2 4 ГиБ. Параметр --noautoconsole предотвращает автоматическое открытие окна консоли, но не запуск ВМ.

#figure(image("img/44-q35-create.png", width: 100%), caption: [Скрипт создания ВМ с машиной Q35 и устройствами VirtIO]) <fig-q35-create>

== Сравнение параметров с XML
Описание работающего домена сохранено командой:

```sh
virsh -c qemu:///system dumpxml Malyshev_P3411_q35 \
  > ~/VM/Malyshev_P3411_q35.xml
```

Из XML отобраны строки с типом машины, дисками, сетью, контроллерами, адресами и графикой. Полученный результат представлен на @fig-q35-xml1 и @fig-q35-xml2.

#figure(image("img/45-q35-xml-1.png", width: 100%), caption: [XML домена: тип машины, диски и контроллеры]) <fig-q35-xml1>
#figure(image("img/46-q35-xml-2.png", width: 100%), caption: [XML домена: сеть, консоль и дополнительные устройства]) <fig-q35-xml2>

Сопоставление явно переданных параметров с фактическим XML:

#table(
  columns: (1fr, 1.4fr), inset: 6pt,
  table.header([Параметр команды], [Результат в XML]),
  [`--virt-type kvm`], [`<domain type='kvm' id='3'>`],
  [`--machine q35`], [`<type arch='x86_64' machine='pc-q35-4.2'>hvm</type>`],
  [`--disk ...,bus=virtio`], [`<target dev='vda' bus='virtio'/>`],
  [`--network network=default,model=virtio`], [Интерфейс типа network, источник default, модель virtio],
  [`--graphics spice`], [Графика SPICE с автоматически назначенным портом 5900],
)

Таким образом, псевдоним q35 развёрнут в конкретную версию pc-q35-4.2, соответствующую используемому QEMU. Шина VirtIO диска и модель VirtIO сетевой карты сохранены. Имя устройства vda выбрано автоматически: в команде задавалась шина, а не это имя.

== Автоматически добавленные элементы
Команда virt-install содержит высокоуровневое описание. Итоговая конфигурация формируется совместно virt-install и libvirt: клиент добавляет значения по умолчанию с учётом профиля ОС, libvirt дополняет и нормализует описание, а при запуске появляются текущие значения ресурсов. 

В полученном XML присутствуют следующие не заданные явно сведения:

+ UUID домена `3e9d3240-6763-4095-b623-6aa13d274da1` и текущий идентификатор `id='3'`. UUID идентифицирует ВМ постоянно, числовой ID относится к текущему запуску.
+ MAC сетевой карты `52:54:00:18:fd:90`. Для запущенного интерфейса показаны имя хостового TAP-устройства `vnet0`, мост `virbr0` сети default и идентификатор сетевого порта. 
+ PCIe-топология: корневой контроллер pcie-root и пять pcie-root-port. Устройствам назначены PCI-адреса. Например, VirtIO-диск подключён по `0000:03:00.0`, сетевая карта — по `0000:01:00.0`.
+ SATA-контроллер и SATA CDROM с именем sda. Наличие CDROM следует из --cdrom, но его шина, имя и адрес выбраны автоматически.
+ USB-контроллеры ICH9: ich9-ehci1 и ich9-uhci1/2/3; контроллер virtio-serial, последовательная консоль, мышь и клавиатура PS/2.
+ Канал `com.redhat.spice.0` и видеоустройство QXL. Тип графики SPICE задан явно, но сопутствующая модель видео и канал в командной строке не перечислялись. Наличие канала не доказывает установку агента внутри гостя.
+ Для SPICE выбраны `autoport='yes'`, текущий порт 5900 и адрес прослушивания 127.0.0.1. Автоматический порт может измениться при следующем запуске; этот localhost относится к Ubuntu, в которой работает libvirt.
+ В описании CPU показана модель EPYC-Milan; непосредственно в команде допзадания модель CPU не указывалась. Это представление виртуального CPU.

Вывод: libvirt хранит подробное описание виртуального оборудования, существенно более полное, чем исходная команда. В XML подтверждены KVM, машина Q35 и устройства VirtIO; идентификаторы, адреса, контроллеры и сопутствующие устройства сформированы автоматически. Поскольку исследовался работающий домен, XML также содержит динамические параметры текущего запуска. Для отдельного сравнения постоянной конфигурации можно использовать dumpxml с --inactive.

Конфигурационный файл находится по пути: /etc/libvirt/qemu/Malyshev_P3411_q35.xml

#heading(numbering: none)[Заключение]
+ Подготовлены гостевая Ubuntu, дополнительный SATA-диск 30 ГиБ и общая папка Soft; обмен файлом проверен на этапе VirtualBox.
+ После ошибки ядра при запуске вложенной ВМ работа продолжена с независимыми копиями дисков под QEMU/KVM. Вложенный KVM и запуск Alpine проверены практически.
+ В Boxes выполнен Live-запуск Alpine, исследованы свойства, проверены SSH-вход на Helios по паролю и RDP к локальной Windows через мост.
+ В QEMU установлена Alpine на отдельный диск 10 ГиБ и проверен полноэкранный запуск с 2048 МиБ RAM.
+ В virt-manager создана ВМ с основным диском 4 ГиБ и дополнительным 10 МиБ. Установка Neovim и его отсутствие после восстановления снимка подтвердили откат состояния.
+ Проверены virsh, полный клон с обоими дисками и создание отдельной Alpine через virt-install на диске 8 ГиБ с двумя CPU и 2048 МиБ RAM.
+ Проверены SSH от обычного пользователя и фоновый запуск через libvirt. Отличия от примеров задания: Alpine вместо Fedora, Neovim вместо браузера, официальный источник ISO вместо зеркала Яндекса, корректировка группового MAC и использование SPICE.


+ Создана отдельная ВМ Q35; сравнение dumpxml с virt-install подтвердило заданные KVM и VirtIO и выявило автоматически сформированные идентификаторы, топологию устройств и параметры текущего запуска.
