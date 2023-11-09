import re


def task2Testing():
    pattern: str = r'(?<!:)(([01]\d|2[0-3]):[0-5]\d(?::[0-5]\d)?)(?!:)'

    print('### ВТОРОЕ ЗАДАНИЕ ###\n')

    print('## Первый тест ##')
    string: str = '''Уважаемые студенты! В эту субботу в 
    15:00 планируется доп. занятие на 2 
    часа. То есть в 17:00:01 оно уже точно 
    кончится'''

    print(f"Строка: {string}\nОтвет: {replace(pattern, string)}\n")

    print('## Второй тест ##')
    string: str = '''Сегодня в 29:5849:938475 состоится...'''

    print(f"Строка: {string}\nОтвет: {replace(pattern, string)}\n")

    print('## Третий тест ##')
    string: str = '''Сегодня в 23:7 состоится...'''

    print(f"Строка: {string}\nОтвет: {replace(pattern, string)}\n")

    print('## Четвертый тест ##')
    string: str = '''Сегодня в 07:00 будет занятие в аудитории 235'''

    print(f"Строка: {string}\nОтвет: {replace(pattern, string)}\n")

    print('## Пятый тест ##')
    string: str = '''Сегодня в 00:00 будет занятие в аудитории 235, а в 00:01:70 всем спать. В 0:07:10 ничего не планируется'''

    print(f"Строка: {string}\nОтвет: {replace(pattern, string)}")


def replace(pattern: str, string: str) -> str:
    replacement: str = '(TBD)'
    result: str = re.sub(pattern, replacement, string)

    return result
