import re


def displayAnswer(string: str, result: list, correctAnswer: list, match: bool):
    print(f"Строка: {string}\nОтвет: \n{displayList(result)}\n")
    print(f"Правильный ответ: \n{displayList(correctAnswer)}\nСовпадение: {'да' if match else 'нет'}\n")


def displayList(inputList: list) -> str:
    outputString: str = '\n'.join(inputList)
    outputString += '\n'

    return outputString


def getData(pattern: str, string: str, testNumber: int) -> (list, list, bool):
    result: list = test(pattern, string)

    correctAnswer: list
    match: bool
    correctAnswer, match = checking(result, testNumber)

    return result, correctAnswer, match


def taskThirdTesting(pattern: str):
    testNumber: int = 1
    print('### ТРЕТЬЕ ЗАДАНИЕ ###\n')

    print('## Первый тест ##')
    string: str = ("Классное слово – обороноспособность,\n"
                   "которое должно идти после слов: трава\n"
                   "и молоко и арита\n")

    result: list
    correctAnswer: list
    match: bool
    result, correctAnswer, match = getData(pattern, string, testNumber)

    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Второй тест ##')
    string: str = "пупп пууууп пооооопооооо пээпээ"

    result, correctAnswer, match = getData(pattern, string, testNumber)

    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Третий тест ##')
    string: str = "ппп пппп ннннн"

    result, correctAnswer, match = getData(pattern, string, testNumber)

    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Четвертый тест ##')
    string: str = "а и ооо пооопп"

    result, correctAnswer, match = getData(pattern, string, testNumber)

    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Пятый тест ##')
    string: str = "ааа ааа аааааа ооо"

    result, correctAnswer, match = getData(pattern, string, testNumber)

    displayAnswer(string, result, correctAnswer, match)


def test(pattern: str, string: str) -> list:
    matches = re.finditer(pattern, string)

    match_list: list = list()
    for match in matches:
        match_list.append(match.group())

    match_list = sorted(set(match_list), key=lambda x: (len(x), x))
    return match_list


def checking(result: list, testNumber: int) -> (list, bool):
    correctAnswers: dict = {
        1: ['и', 'идти', 'слов', 'слово', 'трава', 'должно', 'молоко', 'обороноспособность'],
        2: ['пупп', 'пууууп', 'пээпээ', 'пооооопооооо'],
        3: [],
        4: ['а', 'и', 'ооо', 'пооопп'],
        5: ['ааа', 'ооо', 'аааааа']
    }

    return correctAnswers[testNumber], result == correctAnswers[testNumber]
