import re


def displayAnswer(string: str, result: str, correctAnswer: str, match: bool):
    print(f"Строка: {string}\nОтвет программы: {result}")
    print(f"Правильный ответ: {correctAnswer}\nСовпадение: {'да' if match else 'нет'}\n")


def taskSecondTesting(pattern: str):
    testNumber: int = 1

    print('### ВТОРОЕ ЗАДАНИЕ ###\n')

    print('## Первый тест ##')
    string: str = '''Уважаемые студенты! В эту субботу в 
    15:00 планируется доп. занятие на 2 
    часа. То есть в 17:00:01 оно уже точно 
    кончится'''

    result: str = replace(pattern, string)

    correctAnswer: str
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Второй тест ##')
    string: str = '''Сегодня в 29:5849:938475 состоится...'''

    result: str = replace(pattern, string)

    correctAnswer: str
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Третий тест ##')
    string: str = '''Сегодня в 23:7 состоится...'''

    result: str = replace(pattern, string)

    correctAnswer: str
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Четвертый тест ##')
    string: str = '''Сегодня в 07:00 будет занятие в аудитории 235'''

    result: str = replace(pattern, string)

    correctAnswer: str
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Пятый тест ##')
    string: str = '''Сегодня в 00:00 будет занятие в аудитории 235, а в 00:01:70 всем спать. В 0:07:10 ничего не планируется'''

    result: str = replace(pattern, string)

    correctAnswer: str
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)


def replace(pattern: str, string: str) -> str:
    replacement: str = '(TBD)'
    result: str = re.sub(pattern, replacement, string)

    return result


def checking(result: str, testNumber: int) -> (str, bool):
    correctAnswers: dict = {
        1: '''Уважаемые студенты! В эту субботу в 
    (TBD) планируется доп. занятие на 2 
    часа. То есть в (TBD) оно уже точно 
    кончится''',
        2: 'Сегодня в 29:5849:938475 состоится...',
        3: 'Сегодня в 23:7 состоится...',
        4: 'Сегодня в (TBD) будет занятие в аудитории 235',
        5: 'Сегодня в (TBD) будет занятие в аудитории 235, а в 00:01:70 всем спать. В 0:07:10 ничего не планируется'
    }

    return correctAnswers[testNumber], result == correctAnswers[testNumber]
