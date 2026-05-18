---
title: "DisplayPort"
subtitle: "Аппаратный интерфейс передачи аудио и видео"
author: "* *"
date: "Семинар №2, 2026"
lang: ru-RU
theme: Madrid
colortheme: seahorse
aspectratio: 169
fontsize: 11pt
mainfont: DejaVu Sans
sansfont: DejaVu Sans
header-includes:
  - \usepackage{tikz}
  - \usetikzlibrary{positioning,arrows.meta,shapes.geometric}
---

# Что такое DisplayPort

- Цифровой интерфейс VESA для подключения источника изображения к дисплею
- Передает видео, аудио и служебные данные по пакетной последовательной линии
- Основной сценарий: ПК, ноутбук или док-станция -> монитор, проектор, VR/AR-устройство
- Конкурирует с HDMI, но лучше ориентирован на компьютерные дисплеи, MST и USB-C Alt Mode

# История и версии

| Версия     |       Год | Ключевые изменения                                  |
| ---------- | --------: | --------------------------------------------------- |
| 1.0 / 1.1  | 2006-2007 | замена VGA/DVI, Main Link до HBR                    |
| 1.2        |      2009 | HBR2, Multi-Stream Transport, 4K60                  |
| 1.3        |      2014 | HBR3, 5K60 и 8K30 без сжатия                        |
| 1.4 / 1.4a | 2016-2018 | DSC 1.2, FEC, HDR-метаданные                        |
| 2.0        |      2019 | UHBR 10/13.5/20, до 80 Гбит/с raw                   |
| 2.1 / 2.1a | 2022-2024 | согласование с USB4, DP40/DP80, длиннее UHBR-кабели |

# Скорость передачи данных

| Режим  | Скорость на линию | 4 линии, raw | Полезная полоса |
| ------ | ----------------: | -----------: | --------------: |
| RBR    |       1.62 Гбит/с |  6.48 Гбит/с |     5.18 Гбит/с |
| HBR    |        2.7 Гбит/с |  10.8 Гбит/с |     8.64 Гбит/с |
| HBR2   |        5.4 Гбит/с |  21.6 Гбит/с |    17.28 Гбит/с |
| HBR3   |        8.1 Гбит/с |  32.4 Гбит/с |    25.92 Гбит/с |
| UHBR10 |         10 Гбит/с |    40 Гбит/с |    38.69 Гбит/с |
| UHBR20 |         20 Гбит/с |    80 Гбит/с |    77.37 Гбит/с |

Для HBR используется кодирование 8b/10b, для UHBR - 128b/132b.

# Уровни модели и среда передачи

- Физический уровень: электрическая передача по дифференциальным парам в кабеле
- Канальный уровень: пакетная передача, training link, служебный AUX-канал
- Верхние уровни: транспорт аудио/видео потоков, EDID/DPCD, управление дисплеем
- Основная среда: медный кабель с витой парой и экранированием
- Дополнительно: активные медные и оптические кабели для большей длины

# Линии сигнального интерфейса

\centering
\begin{tikzpicture}[node distance=1.1cm,>=Latex]
\node[draw, rounded corners, minimum width=2.9cm, minimum height=1cm] (src) {Source};
\node[draw, rounded corners, minimum width=2.9cm, minimum height=1cm, right=6.5cm of src] (sink) {Sink};
\foreach \y/\name in {0.9/Lane 0,0.3/Lane 1,-0.3/Lane 2,-0.9/Lane 3} {
\draw[->, thick] ([yshift=\y cm]src.east) -- node[above, font=\scriptsize] {\name} ([yshift=\y cm]sink.west);
}
\draw[<->, thick, blue] ([yshift=-1.6cm]src.east) -- node[below, font=\scriptsize] {AUX CH} ([yshift=-1.6cm]sink.west);
\draw[->, thick, gray] ([yshift=-2.25cm]sink.west) -- node[below, font=\scriptsize] {HPD} ([yshift=-2.25cm]src.east);
\end{tikzpicture}

\vspace{0.5em}

Минимум для передачи видео: 1 дифференциальная Main Link линия. Типичный кабель: 4 Main Link линии, AUX CH и HPD.

# Синхронизация и направление обмена

- Интерфейс синхронный на физическом уровне: приемник восстанавливает тактирование из потока данных
- Main Link однонаправленный: видео и аудио идут от источника к приемнику
- AUX CH двунаправленный полудуплексный: конфигурация, DPCD, чтение EDID, управление
- HPD однонаправленный от дисплея к источнику: подключение, отключение, запрос прерывания
- В обычном соединении есть роли: Source и Sink; в MST добавляется Branch-устройство

# Топологии

\centering
\begin{tikzpicture}[node distance=0.9cm and 1.3cm, every node/.style={font=\small}]
\node[draw, rounded corners] (pc) {ПК};
\node[draw, rounded corners, right=of pc] (m1) {Монитор};
\draw[->, thick] (pc) -- (m1);

\node[draw, rounded corners, below=1.3cm of pc] (dock) {Ноутбук};
\node[draw, rounded corners, right=of dock] (hub) {MST hub};
\node[draw, rounded corners, right=of hub, yshift=0.45cm] (a) {Display A};
\node[draw, rounded corners, right=of hub, yshift=-0.45cm] (b) {Display B};
\draw[->, thick] (dock) -- (hub);
\draw[->, thick] (hub) -- (a);
\draw[->, thick] (hub) -- (b);

\node[draw, rounded corners, below=1.3cm of dock] (laptop) {USB-C};
\node[draw, rounded corners, right=of laptop] (usb4) {Dock};
\node[draw, rounded corners, right=of usb4] (monitor) {DP monitor};
\draw[->, thick] (laptop) -- node[above] {Alt Mode} (usb4);
\draw[->, thick] (usb4) -- (monitor);
\end{tikzpicture}

# Надежность передачи

- Link training подбирает скорость, число линий, амплитуду и предыскажение
- Канал AUX используется для согласования возможностей Source/Sink и параметров линии
- В DisplayPort 1.4 добавлены DSC и Forward Error Correction для сжатого видеопотока
- В UHBR режимах используется более эффективное кодирование 128b/132b
- Сертификация кабелей DP40/DP80 снижает риск ошибок на высоких скоростях

# Разъемы

\centering
\begin{columns}[T,onlytextwidth]
\begin{column}{0.33\textwidth}
\centering
\includegraphics[width=\linewidth,height=0.38\textheight,keepaspectratio]{assets/connectors/displayport-full.jpg}
\vspace{0.2em}
\parbox{\linewidth}{\centering\scriptsize Full-size DisplayPort\\20 контактов, часто с защелкой}
\end{column}
\begin{column}{0.33\textwidth}
\centering
\includegraphics[width=\linewidth,height=0.38\textheight,keepaspectratio]{assets/connectors/mini-displayport.jpg}
\vspace{0.2em}
\parbox{\linewidth}{\centering\scriptsize Mini DisplayPort\\компактный вариант для ноутбуков}
\end{column}
\begin{column}{0.33\textwidth}
\centering
\includegraphics[width=\linewidth,height=0.38\textheight,keepaspectratio]{assets/connectors/usb-c-alt-mode.jpg}
\vspace{0.2em}
\parbox{\linewidth}{\centering\scriptsize USB-C DisplayPort Alt Mode\\общий разъем USB-C}
\end{column}
\end{columns}

\vspace{0.6em}

# Максимальная дальность

- DisplayPort задает требования к качеству передачи, а не одну универсальную длину
- Практически: пассивные кабели обычно 1-3 м для высоких скоростей и до нескольких метров для меньших режимов
- Сертифицированные DP40/DP80 кабели ориентированы на UHBR-режимы
- Для длинных трасс используют активные медные или оптические кабели; длина может достигать десятков метров
- DisplayPort 2.1a/2.1b развивает именно тему более длинных UHBR-кабелей

# Особенности

- MST: несколько независимых видеопотоков через один порт
- Adaptive-Sync: переменная частота обновления для игр и профессиональной графики
- DSC: визуально без потерь для высоких разрешений и частот
- DP Alt Mode: передача DisplayPort через USB-C рядом с USB и USB Power Delivery
- Обратная совместимость: устройства обычно согласуют максимальный общий режим

# Сферы использования

- Настольные ПК и игровые мониторы с высокой частотой обновления
- Рабочие станции, CAD, видеомонтаж, медицинская визуализация
- Ноутбуки, док-станции и мониторы с USB-C
- Многомониторные рабочие места и MST-хабы
- Встроенные дисплеи через eDP: ноутбуки, панели, автомобильные и промышленные системы

# Итоги

- DisplayPort - пакетный цифровой интерфейс для высокоскоростной передачи видео и аудио
- Основной канал однонаправленный, служебный AUX-канал двунаправленный
- Масштабируется от одной до четырех линий и от RBR до UHBR20
- Сильные стороны: высокая пропускная способность, MST, Adaptive-Sync, DSC и USB-C Alt Mode
- Ключевое ограничение: требования к качеству кабеля резко растут с повышением скорости
