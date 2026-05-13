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

#let subject_title = "Тестирование программного обеспечения"
#let work_title = "Отчёт по лабораторной работе №2"
#let work_name = "Интеграционное тестирование системы функций"
#let work_variant = "Вариант №4356715"

#let student_name = "* * *"
#let group = "Группа P3311"

#let teacher_name = "Наумова Надежда Александровна"

#let year = "2026"

#align(center)[
  #v(10mm)
  #text(weight: "bold")[#organization]
  #v(3mm)
  #faculty
  #v(25mm)

  #text(weight: "bold", size: 16pt)[#work_title]
  #v(6mm)
  #text(weight: "bold")[#subject_title]
  #v(3mm)
  #work_name
  #v(3mm)
  #work_variant
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

= Задание

Провести интеграционное тестирование программной системы, реализующей кусочно-заданную систему функций. Для выполнения работы требуется:
- построить систему функций из тригонометрической и логарифмической ветвей
- выполнить интеграцию сверху вниз с использованием заглушек
- использовать JUnit 5, параметризованные тесты и Mockito
- применять табличные значения заглушек
- подготовить CSV-выгрузки и графики для функций
- учесть характерные точки: экстремумы, асимптоты, окрестности точек разрыва и периодичность

== Вариант

Исследуемая система функций задаётся следующим образом.

#figure(
  image("func.png", width: 100%),
  caption: [Система функций]
)

Зависимости между модулями в системе приведены на схеме ниже.

#figure(
  image("sys_func_dep.png", width: 75%),
  caption: [Схема зависимостей модулей]
)

= Цель работы

Освоить интеграционное тестирование программной системы на примере кусочно-заданной системы функций, проверить корректность взаимодействия между модулями и пошагово заменить заглушки на реальные реализации согласно схеме зависимостей.

= Ход работы

*Ссылка на Pull Request:*
#link("https://github.com/mip3x/itmo-labs/pull/50")

== Архитектура решения

Система содержит две основные ветви:
- при $x <= 0$ используется тригонометрическая ветвь
- при $x > 0$ используется логарифмическая ветвь

Базовыми модулями являются функции $sin(x)$ и $ln(x)$. Остальные компоненты строятся на их основе:
- $cos(x)$ и $csc(x)$ зависят от $sin(x)$
- $sec(x)$ зависит от $cos(x)$
- $tan(x)$ и $cot(x)$ зависят от $sin(x)$ и $cos(x)$
- $log_2(x)$, $log_3(x)$ и $log_10(x)$ зависят от $ln(x)$

Верхним модулем является `SystemFunction`, который объединяет обе ветви и выбирает нужную формулу в зависимости от значения аргумента.

Структура основных классов проекта приведена на диаграмме ниже.

#figure(
  image("class-diagram.svg", width: 100%),
  caption: [Диаграмма классов проекта]
)

== Выбранная стратегия интеграции

Для работы выбрана стратегия *top-down* — интеграция сверху вниз. На первом этапе тестируется `SystemFunction` с полностью подменёнными зависимостями. Затем заглушки постепенно заменяются реальными реализациями согласно графу зависимостей.

Порядок интеграции:
1. все зависимости являются заглушками
2. реальные $tan(x)$, $cot(x)$ и $sec(x)$, а $sin(x)$, $cos(x)$ и $csc(x)$ остаются заглушками
3. реальные $tan(x)$, $cot(x)$, $sec(x)$, $cos(x)$ и $csc(x)$, а $sin(x)$ остаётся заглушкой
4. тригонометрическая ветвь становится полностью реальной
5. реальный $ln(x)$, логарифмы пока заменяются заглушками
6. реальные $ln(x)$ и $log_2(x)$, остальные логарифмы остаются заглушками
7. реальные $ln(x)$, $log_2(x)$ и $log_3(x)$
8. добавляется реальный $log_10(x)$, после чего логарифмическая ветвь становится полностью реальной
9. финальный тест всей системы с реальными модулями

== Использование заглушек

В качестве заглушек использовались Mockito-mock объекты, реализующие интерфейс функции. Значения для заглушек задавались таблично: при вызове `calculate(x, eps)` возвращалось заранее подготовленное значение для аргумента $x$.

Для упрощения подготовки моков был введён вспомогательный объект `StubHelper`, который:
- создаёт `mock<Function>()`
- перехватывает вызов `calculate(any(), any())`
- извлекает фактический аргумент $x$
- возвращает значение из таблицы, загруженной из CSV-ресурса
- выбрасывает исключение, если значение для точки отсутствует

Табличные значения вынесены в файлы `src/test/resources/test-data/system/trig_values.csv` и `src/test/resources/test-data/system/log_values.csv`. Из этих же таблиц берутся:
- значения для Mockito-заглушек
- ожидаемые значения `expected` в интеграционных тестах
- допустимые погрешности сравнения
- значения, пригодные для последующего построения графиков

== Интеграционные тесты

Для тестирования использованы:
- JUnit 5
- параметризованные тесты
- Mockito для реализации заглушек
- CSV-источники с заранее подготовленными ожидаемыми значениями `expected`

На первом уровне интеграции были подготовлены параметризованные тесты на обе ветви системы:
- отрицательная ветвь проверялась на наборе точек $x <= 0$
- положительная ветвь проверялась на наборе точек $x > 0$

После этого были реализованы частичные интеграционные тесты, в которых поэтапно заменялись заглушки реальными модулями. Например:
- тест `SystemFunctionIntegrationStubTest` проверяет уровень, где все заглушки
- тест `SystemFunctionTanCotSecIntegrationTest` проверяет уровень, где реальные $tan(x)$, $cot(x)$ и $sec(x)$
- тест `SystemFunctionTanCotSecCosCscIntegrationTest` проверяет уровень, где дополнительно становятся реальными $cos(x)$ и $csc(x)$
- тест `SystemFunctionFullIntegrationTest` завершает интеграцию тригонометрической ветви
- тест `SystemFunctionLnIntegrationTest` проверяет уровень, где реальным является только $ln(x)$
- тест `SystemFunctionLnLog2IntegrationTest` добавляет реальный $log_2(x)$
- тест `SystemFunctionLnLog2Log3IntegrationTest` добавляет реальный $log_3(x)$
- тест `SystemFunctionLogFullIntegrationTest` завершает интеграцию логарифмической ветви

На каждом этапе:
- `actual` вычислялся через `SystemFunction`
- `expected` подставлялся из заранее подготовленных CSV-таблиц
- значения заглушек подставлялись из тех же табличных источников

== Характерные точки

Для тригонометрической ветви:
- точки $x = -2 pi - 0.01$ и $x = -2 pi + 0.01$ как левая и правая окрестности асимптоты $x = -2 pi$
- точки $x = -6.1$ и $x = -5.8$ как представители участков до и после локального минимума $x = -5.95$
- точка $x = -5.95$ как локальный минимум на интервале $(-2 pi; -3 pi / 2)$
- точки $x = -3 pi / 2 - 0.01$ и $x = -3 pi / 2 + 0.01$ как окрестности асимптоты $x = -3 pi / 2$
- точки $x = -pi - 0.01$ и $x = -pi + 0.01$ как окрестности асимптоты $x = -pi$
- точки $x = -3.54104$ и $x = -3.44104$ как представители участков слева и справа от точки $x = -3.49104$
- точка $x = -3.49104$ как дополнительная характерная точка на переходе через ноль внутри интервала $(-3 pi / 2; -pi)$
- точки $x = -2.95$ и $x = -2.6$ как представители участков до и после локального максимума $x = -2.79$
- точка $x = -2.79$ как локальный максимум на интервале $(-pi; -pi / 2)$
- точки $x = -pi / 2 - 0.01$ и $x = -pi / 2 + 0.01$ как окрестности асимптоты $x = -pi / 2$
- точка $x = -pi / 4$ как внутренняя точка интервала $(-pi / 2; 0)$
- точка $x = -0.01$ как левая окрестность асимптоты $x = 0$
- точка $x = -9 pi / 4$ как проверка периодичности, то есть сдвиг точки $-pi / 4$ на период $-2 pi$

Для логарифмической ветви:
- асимптота в точке $x = 0$, так как при $x -> 0+$ логарифмы и натуральный логарифм не определены и модуль уходит в бесконечность
- точка $x = 0.1$ как точка, расположенная близко к асимптоте и показывающая сильное отрицательное значение системы
- точка $x approx 0.153$ как окрестность нуля функции, где положительная ветвь меняет знак
- точка $x approx 0.253$ как окрестность локального максимума положительной ветви на интервале $(0; 1)$
- точка $x = 1.0$ как точка минимума, в которой все логарифмы обращаются в ноль и значение системы равно нулю
- точка $x = 3.0$ как точка интервала $(1; +infinity)$
- точка $x = 10.0$ как дополнительная контрольная точка тестирования, показывающая дальнейший рост функции на больших допустимых значениях

#figure(
  image("generated/plots/system_trig_partition.svg", width: 100%),
  caption: [Асимптоты, интервалы эквивалентности и выбранные точки для тригонометрической ветви]
)

На интервале $[-2 pi; 0]$ отрицательная ветвь разбивается асимптотами на четыре основные области эквивалентности:
- $(-2 pi; -3 pi / 2)$
- $(-3 pi / 2; -pi)$
- $(-pi; -pi / 2)$
- $(-pi / 2; 0)$

Разрывы возникают в точках, где $sin(x) = 0$ или $cos(x) = 0$, то есть в узлах вида $x = k pi / 2$.

Для покрытия этой ветви были выбраны:
- точки с обеих сторон от всех внутренних асимптот и от точки $x = -2 pi$
- точки слева и справа от дополнительной характерной точки $x = -3.49104$
- точки до и после локального минимума на первом интервале
- локальный минимум $x = -5.95$ на первом интервале
- точки до и после локального максимума на третьем интервале
- локальный максимум $x = -2.79$ на третьем интервале
- внутренняя точка $x = -pi / 4$ на последнем интервале
- окрестность асимптоты $x = 0$ слева

#figure(
  image("generated/plots/system_trig_periodicity.svg", width: 100%),
  caption: [Проверка периодичности тригонометрической ветви]
)

Дополнительная точка $x = -9 pi / 4$ используется не для нового интервала эквивалентности, а для проверки периодичности. Она эквивалентна точке $x = -pi / 4$, поскольку отличается от неё на период $2 pi$.

#figure(
  image("generated/plots/system_log_partition.svg", width: 100%),
  caption: [Асимптота, интервалы эквивалентности и выбранные точки для логарифмической ветви]
)

Для положительной ветви выделяются следующие области эквивалентности:
- $(0; 0.153)$, где значение положительной ветви отрицательно
- точка $x approx 0.153$ как окрестность нуля функции
- $(0.153; 0.253)$, где ветвь быстро возрастает после смены знака
- точка $x approx 0.253$ как окрестность локального максимума
- $(0.253; 1)$, где ветвь убывает к нулю, оставаясь неотрицательной
- точка $x = 1$ как отдельная граничная точка и минимум ветви
- $(1; +infinity)$, где ветвь снова возрастает

В этой ветви вертикальная асимптота возникает при $x = 0$, так как логарифмические функции не определены на неположительных значениях. При $x -> 0+$ натуральный логарифм и логарифмы по основаниям 2, 3 и 10 стремятся к $-infinity$, поэтому и вся положительная ветвь системы уходит вниз.

Тестовые точки выбираются так:
- $x = 0.1$ показывает поведение ветви в непосредственной близости к асимптоте $x = 0$
- $x approx 0.153$ отмечает окрестность нуля функции и смену знака
- $x approx 0.253$ отмечает окрестность локального максимума
- $x = 1.0$ проверяет граничное значение и минимум, где все логарифмы равны нулю
- $x = 3.0$ представляет типичное значение из интервала $(1; +infinity)$
- $x = 10.0$ дополнительно используется в тестах как контрольная точка роста функции на больших допустимых значениях, хотя на локальном графике разбиения она не показана

Выбранный набор покрывает:
- типичные значения внутри области определения
- окрестности разрывов и асимптот
- проверку периодичности тригонометрической ветви
- характерное поведение логарифмической ветви: смену знака, локальный максимум, минимум в точке $x = 1$ и дальнейший рост

== CSV-выгрузки и графики

Выгрузки сделаны для:
- базовых функций $sin(x)$ и $ln(x)$
- производных тригонометрических функций
- логарифмов с различными основаниями
- полной системы функций

Модули для выгрузки:
- `CsvExporter`, выполняющий сохранение таблицы вида `X, Result`
- `FunctionRegistry`, предоставляющий доступ ко всем модулям системы по имени
- точка входа `Main`, позволяющая запускать экспорт из командной строки
- Python-скрипт `report/scripts/generate_graphs.py`, строящий SVG-графики по подготовленным CSV

Пример запуска:

#raw("./gradlew run --args=\"system build/system.csv -10 10 0.1 1e-6 ;\"", block: true, lang: "bash")

CSV-выгрузки используются для последующего построения графиков и выбора табличных значений для заглушек. Значения для заглушек и ожидаемые результаты интеграционных тестов были вынесены в CSV-ресурсы и используются как единый источник тестовых данных.

=== Базовые функции

#figure(
  image("generated/plots/sin.svg", width: 100%),
  caption: [График функции $sin(x)$]
)

#figure(
  image("generated/plots/ln.svg", width: 100%),
  caption: [График функции $ln(x)$]
)

=== Тригонометрические функции

#figure(
  image("generated/plots/cos.svg", width: 100%),
  caption: [График функции $cos(x)$]
)

#figure(
  image("generated/plots/tan.svg", width: 100%),
  caption: [График функции $tan(x)$]
)

#figure(
  image("generated/plots/cot.svg", width: 100%),
  caption: [График функции $cot(x)$]
)

#figure(
  image("generated/plots/sec.svg", width: 100%),
  caption: [График функции $sec(x)$]
)

#figure(
  image("generated/plots/csc.svg", width: 100%),
  caption: [График функции $csc(x)$]
)

=== Логарифмические функции

#figure(
  image("generated/plots/log2.svg", width: 100%),
  caption: [График функции $log_2(x)$]
)

#figure(
  image("generated/plots/log3.svg", width: 100%),
  caption: [График функции $log_3(x)$]
)

#figure(
  image("generated/plots/log10.svg", width: 100%),
  caption: [График функции $log_10(x)$]
)

=== Полная система

#figure(
  image("generated/plots/system.svg", width: 100%),
  caption: [График полной системы функций]
)

=== Команды для CSV-выгрузок

#raw("./gradlew run --args=\"sin report/generated/csv/sin.csv -6.283185307179586 6.283185307179586 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"cos report/generated/csv/cos.csv -6.283185307179586 6.283185307179586 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"tan report/generated/csv/tan.csv -4.71238898038469 4.71238898038469 0.01 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"cot report/generated/csv/cot.csv -4.71238898038469 4.71238898038469 0.01 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"sec report/generated/csv/sec.csv -4.71238898038469 4.71238898038469 0.01 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"csc report/generated/csv/csc.csv -4.71238898038469 4.71238898038469 0.01 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"ln report/generated/csv/ln.csv 0.1 10.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"log2 report/generated/csv/log2.csv 0.1 10.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"log3 report/generated/csv/log3.csv 0.1 10.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"log10 report/generated/csv/log10.csv 0.1 10.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"system report/generated/csv/system.csv -6.283185307179586 10.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"system report/generated/csv/system_trig_partition.csv -6.303185307179586 0.0 0.01 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"system report/generated/csv/system_trig_periodicity.csv -7.5 0.0 0.02 1e-6\"", block: true, lang: "bash")

#raw("./gradlew run --args=\"system report/generated/csv/system_log_partition.csv 0.01 4.0 0.005 1e-6\"", block: true, lang: "bash")

=== Команды для графиков

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/sin.csv report/generated/plots/sin.svg \"sin(x)\" -1.2 1.2", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/cos.csv report/generated/plots/cos.svg \"cos(x)\" -1.2 1.2", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/tan.csv report/generated/plots/tan.svg \"tan(x)\" -10 10", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/cot.csv report/generated/plots/cot.svg \"cot(x)\" -10 10", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/sec.csv report/generated/plots/sec.svg \"sec(x)\" -5 5", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/csc.csv report/generated/plots/csc.svg \"csc(x)\" -5 5", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/ln.csv report/generated/plots/ln.svg \"ln(x)\" -3 3", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/log2.csv report/generated/plots/log2.svg \"log2(x)\" -4 4", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/log3.csv report/generated/plots/log3.svg \"log3(x)\" -3 3", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/log10.csv report/generated/plots/log10.svg \"log10(x)\" -2 2", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/system.csv report/generated/plots/system.svg \"system(x)\" -20 25", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/system_trig_partition.csv report/generated/plots/system_trig_partition.svg \"system(x), trig branch, one period\" -120 120 a:-6.283185307179586 a:-4.71238898038469 a:-3.141592653589793 a:-1.5707963267948966 a:0.0 p:-6.293185307179586 p:-6.2731853071795864 p:-6.1 p:-5.95 p:-5.8 p:-4.7223889803846895 p:-4.70238898038469 p:-3.151592653589793 p:-3.1315926535897933 p:-3.54104 p:-3.49104 p:-3.44104 p:-2.95 p:-2.79 p:-2.6 p:-1.5807963267948966 p:-1.5607963267948965 p:-0.7853981633974483 p:-0.01", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/system_trig_periodicity.csv report/generated/plots/system_trig_periodicity.svg \"system(x), periodicity check\" -10 10 p:-7.0685834705770345 p:-0.7853981633974483", block: true, lang: "bash")

#raw("python3 report/scripts/generate_graphs.py report/generated/csv/system_log_partition.csv report/generated/plots/system_log_partition.svg \"system(x), log branch partition\" -20 25 a:0.0 p:0.1 p:0.153 p:0.253 p:1.0 p:3.0", block: true, lang: "bash")

Скрипт `generate_graphs.py` принимает пять обязательных аргументов: путь к CSV, путь к SVG, заголовок графика, нижнюю границу по оси $Y$ и верхнюю границу по оси $Y$. После них можно передавать:
- `a:<x>` для отображения вертикальной асимптоты
- `p:<x>` для отображения выбранной тестовой точки

Графики для функций с асимптотами ($tan$, $cot$, $sec$, $csc$ и `system`) строятся с ограничением диапазона по оси $Y$, чтобы на одном изображении были различимы интервалы между разрывами.

= Вывод

В ходе лабораторной работы была реализована система функций и выбрана стратегия интеграционного тестирования сверху вниз. Для проверки корректности работы верхнего модуля использовались табличные заглушки на базе Mockito, параметризованные тесты JUnit 5 и единые CSV-источники тестовых данных. Далее заглушки поэтапно заменялись реальными функциями в соответствии со схемой зависимостей.
