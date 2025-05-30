# Лабораторная работа №3. Опыты

Цель:

- познакомиться на практике с системами команд разных типов процессоров;
- познакомиться с устройством процессоров на уровне принципиальных схем.

Лабораторные работы проводят при помощи проекта [wrench](https://github.com/ryukzak/wrench):

- находится на стадии Beta, поэтому могут быть баги, ошибки моделирования, отсутствие необходимых инструкций, и т.п.);
- инструкции по установке, использованию, примеры исходных кодов и конфигураций приведены в репозитории.

Содержание лабораторной работы:

1. Реализация заданных вариантом алгоритмов на заданных архитектурах (см. документацию wrench):
    - [аккумуляторная архитектура `acc32`](./acc32),
    - [стековая архитектура `f32a`](./f32a),
    - [RISC архитектура `risc-iv-32`](./risc-iv-32).
1. Подготовка принципиальной схемы заданной вариантом для одного из используемых процессоров.
1. Защита реализованных алгоритмов (включая понимание архитектуры процессора, её достоинств и недостатков) и схемы.

Ваш вариант будет приведён в ведомости. Расшифровка варианта приведена в файле: [variants.md](https://github.com/ryukzak/wrench/blob/master/variants.md), где приведён код на языке Python и набор тестов. Вам необходимо написать эквивалентные алгоритмы на заданной архитектуре с учётом следующих требований:

1. Если ввод не соответствует области определения -- вернуть `-1`.
1. Если результат не может быть корректно рассчитан (результат не может быть представлен в рамках машинного слова) -- вернуть результат заполненный байтами со значениями `0xCC`.
1. Ввод должен подаваться через ячейку памяти `0x80`.
1. Вывод должен подаваться в ячейку памяти `0x84`.
1. Входное значение и результат по умолчанию — машинное слово в 32 бита, если не указано иное.
1. Исходный код должен быть отформатирован (вручную или при помощи `wrench-fmt`).
1. Журнал работы не должен быть обрезан (используйте конфигурацию с пониманием).
1. Требования, специфичные для ISA:
    - `F32a`: использовать процедуры;
    - `Risc-iv-32`: использовать вложенных процедур[^1], с целью демонстрации работы со стеком. Где применимо -- рекомендуется рекурсивное решение задачи.
1. При использовании процедур требуется выработать способ именования меток, помогающий видеть структуру кода.

[^1]: Одна процедура вызывает другую процедуру.

Формат занятия (или его части), посвящённого данной лабораторной работе:

1. Защита лабораторных работ.
1. Когда все выполненные лабораторные работы завершены — классическая консультация, где преподаватель ответит на ваши и в случае необходимости свои вопросы.

Процедура сдачи лабораторной работы:

1. Ваша реализация алгоритма и формата отчёта загружаются на сайт [wrench.edu.swampbuds.me](http://wrench.edu.swampbuds.me/submit-form), где подвергается автоматическому тестированию.
1. В случае успешного прохождения тестов -- сохраните ссылку на страницу отчёта.
    - Тестовые наборы и модели процессоров будут уточняться и дополняться в течение семестра. Будьте готовы, что ваша реализация может "сломаться".
    - Тестовые наборы и система симуляции могут содержать ошибки. В таком случае обращайтесь к преподавателю на лабораторных занятиях и консультациях, исправим.
1. Приходите на лабораторное занятие со ссылками на алгоритмы. Проходите защиту.
    - **ВАЖНО**: сохраняйте копии загруженных данных. В любой момент может потребоваться повторная загрузка.

Каждый алгоритм и схема сдаются отдельно. Если на занятии несколько учащихся -- по одной за раз. Повторная сдача одного результата в течение дня запрещена.

Оценивание реализации алгоритмов:

1. `7` баллов -- успешная реализация алгоритма и достойные ответы на вопросы:
    - объяснение алгоритма;
    - объяснение инструкций;
    - объяснение принципов формирования машинного кода из ассемблера;
    - объяснение специфики системы команд процессора;
    - и т.п.
1. `5` баллов -- успешная реализация алгоритма и частичные ответы на вопросы.
1. `0` баллов -- отсутствие реализации алгоритма и/или корректных ответов на вопросы.

Оценивание схемы:

1. `9` баллов -- корректное отображение схемы и достойные ответы на вопросы, включая возможную вариативность предложенной схемы и связанные с этим компромиссы.
1. `7` баллов -- корректное отображение схемы и достойные ответы на вопросы:
    - объяснение принципов работы схемы и процессора;
    - объяснение порядка выполнения той или иной инструкции данного процессора;
    - объяснение принципов работы с памятью и регистрами, вводом/выводом;
    - и т.п.
1. `5` баллов -- успешная реализация алгоритма и частичные ответы на вопросы.
1. `0` баллов -- отсутствие корректной схемы и/или корректных ответов на вопросы.

**ВАЖНО**: оценка может быть снижена если:

- задание сдаётся многократно (что говорит об отсутствии должной подготовки);
- исходный код неконсистентен (стиль кодирования / документирования / комментирования).

---

Типовые вопросы для защиты:

1. Что такое метка (label)?
1. Откуда рассчитываются значения меток (label)?
1. Как вывести в отчёте состояние регистра `Acc` в hex формате?
1. Назначение `alignment` (неиспользуемый буфер в секции данных)?
1. `acc32` Зачем определены дублирующиеся инструкции `load_addr` и `load`?
1. `acc32` Переполнение и перенос. Overflow и Carry флаги, иные способы обработки.
1. Что такое Memory Mapped IO?
1. Что будет, если процессор попытается прочитать инструкцию из адреса ввода-вывода?
1. Что означает инструкция `...`? Объясните что происходит в строках `...`.
1. Как сократить объём машинного кода? Какие ограничение/проблемы это создаст?
1. `risc-iv` Что означает конструкция `0(t1)`? Зачем она нужна?
1. `risc-iv` Почему для загрузки слова регистр нужно две инструкции?
1. `f32a` Почему в F32a удобно использовать процедуры?
1. `f32a` Extended arithmetic mode. Назначение и использование.
1. Особенности кодирования инструкций, плотность кода, проблемы доступа.
1. Как вы можете сократить объём машинного кода? Продемонстрируйте.
1. `risc-iv` Работа со стеком. Выделение памяти, передача данных в/из процедур.

