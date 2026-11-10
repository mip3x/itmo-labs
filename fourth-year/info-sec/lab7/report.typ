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

#let organization = "Федеральное государственное автономное образовательное учреждение высшего образования «Национальный исследовательский университет ИТМО»"
#let faculty = "Факультет программной инженерии и компьютерной техники (ФПИиКТ)"

#let work_title = "Дисциплина «Информационная безопасность»"

#let student_name = "* * *"

#let group = "Группа P34**"


#let year = "2026"

#align(center)[
  #v(10mm)
  #text(weight: "bold")[#organization]
  #v(3mm)
  #faculty
  #v(25mm)

  #text(weight: "bold")[#work_title]
  #v(4mm)
  Работа 7: Безопасность браузера и анализ сетевого трафика
  #v(2mm)
]

#v(30mm)

#align(right)[
  Выполнил(а): \
  #student_name \
  #group \

]

#v(25mm)

#align(center)[
  #year
]

#pagebreak()

#outline(title: [Оглавление], indent: auto)
#pagebreak()
#set page(numbering: "1")

= Среда выполнения

Браузер LibreWolf

= Ход работы

== Настройка безопасности и конфиденциальности

=== Расширенная защита от отслеживания

В настройках LibreWolf установлено, что Enhanced Tracking Protection включена по умолчанию в строгом режиме (Strict). Дополнительное включение не потребовалось; режим оставлен без изменений.

#figure(image("img/01-tracking-protection.png", width: 100%), caption: [Настройки защиты от отслеживания в LibreWolf])

Strict объединяет несколько механизмов ограничения межсайтового отслеживания:

- блокировка трекеров и сбора цифрового отпечатка
- разделение cookies и других хранилищ по посещаемому сайту (dFPI / State Partitioning)
- SmartBlock: подмена некоторых заблокированных скриптов совместимыми заглушками, чтобы уменьшить нарушения работы страниц
- дополнительная очистка cookies, более строгие правила передачи Referer и удаление известных отслеживающих параметров URL

#figure(image("img/02-faq-enhanced-tracking-protectiong.png", width: 100%), caption: [Описание Enhanced Tracking Protection в официальном FAQ LibreWolf])

=== Сохранение паролей

В разделе Passwords проверено состояние параметра "Ask to save passwords": предложение сохранять пароли отключено.

#figure(image("img/03-passwords.png", width: 100%), caption: [Отключённое предложение сохранять пароли в LibreWolf])

=== Очистка cookies и кэша

В окне очистки выбран период Everything и пункты Cookies and site data и Temporary cached files and pages. До очистки объём cookies и данных сайтов составлял 1,0 МБ; после выполнения очистки - 0 байт. Пункт временных файлов показывал 0 байт до и после операции.

#figure(image("img/04-before-clear.png", width: 85%), caption: [Выбранные категории и объём данных перед очисткой])

#figure(image("img/05-after-clear.png", width: 85%), caption: [Нулевой объём данных после очистки])

Для объяснения нулевого значения временных файлов исследована страница about:cache. На момент проверки она показывала 116 записей общим объёмом 2443 КиБ и расположение "none, only stored in memory" в обоих разделах: сетевой кэш присутствует в оперативной памяти.

#figure(image("img/06-cache-memory.png", width: 100%), caption: [Сетевой кэш в оперативной памяти на странице about:cache])

== Анализ сетевых запросов

=== GET-запрос страницы

Во вкладке Network выбран основной HTML-документ по адресу https://en.wikipedia.org/wiki/ITMO_University. Метод GET используется для получения представления ресурса. Зафиксированы статус 200 (успешный ответ), версия HTTP/2 и использование HTTPS.

#figure(image("img/07-get-headers.png", width: 100%), caption: [GET-запрос статьи Wikipedia и заголовки ответа])

Некоторые из заголовков:

- content-encoding: gzip - сжатие содержимого ответа
- content-language: en - язык содержимого
- cache-control - правила кэширования; в частности, max-age=0 и must-revalidate требуют проверки актуальности перед повторным использованием устаревшего ответа
- content-security-policy - ограничения на источники загрузки и выполнение содержимого страницы. Наличие заголовка само по себе не доказывает отсутствие XSS

=== Заголовки GET-запроса

В разделе Request Headers исследованы данные, отправленные браузером:

- Accept - поддерживаемые типы содержимого
- Accept-Encoding - поддерживаемые способы сжатия: gzip, deflate, br, zstd
- Accept-Language: en-US,en;q=0.5 - языковые предпочтения
- Host: en.wikipedia.org - адресуемый сервер
- Referer: https://www.wikipedia.org/ - источник перехода
- Cookie - сохранённые cookies, подходящие для запроса
- DNT: 1 - просьба не отслеживать пользователя, которая сама по себе не обеспечивает блокировку
- Sec-Fetch-Dest: document и Sec-Fetch-Mode: navigate - запрос документа при навигации
- Sec-Fetch-Site: same-site - переход в пределах одного сайта в понимании браузера
- User-Agent - заявленные сведения о клиенте

#figure(image("img/08-get-request-headers.png", width: 100%), caption: [Заголовки GET-запроса браузера])

== Передача данных аутентификации

На форме входа Wikipedia отправлены тестовые учётные данные. Во вкладке Request инструменты разработчика видны Form data: wpName со значением LAB7example и wpPassword со значением lab7example. Также присутствуют служебные поля title: Special:UserLogin, authAction: login и wpLoginToken.

#figure(image("img/09-post-form.png", width: 100%), caption: [Данные формы входа с вымышленными логином и паролем])

При использовании HTTPS HTTP-заголовки и тело запроса защищены TLS на пути между браузером и сервером, завершающим TLS-соединение.

В современных веб-приложениях логин и пароль обычно передаются в теле POST-запроса по HTTPS, например в виде данных формы или JSON. Метод POST сам по себе не обеспечивает шифрование; его обеспечивает HTTPS/TLS. Логин и пароль передаются единожды - в момент аутентификации, затем используются сессии или токены.

На отдельном снимке Headers виден метод POST, адрес получателя https://auth.wikimedia.org/enwiki/w/index.php, версия HTTP/2 и статус ответа. Логин и пароль переданы в теле запроса, URL содержит служебные параметры формы и возврата к статье.

#figure(image("img/10-post-headers.png", width: 100%), caption: [POST-запрос к серверу аутентификации Wikimedia по HTTPS])

При отправке формы браузер формирует HTTP-запрос с полями wpName и wpPassword. Содержимое запроса шифруется средствами TLS при передаче; принимающая сторона завершает TLS-соединение и получает исходные данные для обработки. Ответ сервера также передаётся по защищённому соединению.
