import re


def displayAnswer(string: str, result: int, correctAnswer: int, match: bool):
    print(f"Строка: {string}\nОтвет программы: {result}")
    print(f"Правильный ответ: {correctAnswer}\nСовпадение: {'да' if match else 'нет'}\n")


def taskFirstTesting(pattern: str):
    testNumber: int = 1
    print('### ПЕРВОЕ ЗАДАНИЕ ###\n')

    print('## Первый тест ##')
    string: str = '=-{(sldf=-{(=-{sdf(=sdf-{(===-{('

    result: int = test(pattern, string)

    correctAnswer: int
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Второй тест ##')
    string: str = 'Здесь нет никаких смайликов'

    result: int = test(pattern, string)

    correctAnswer: int
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Третий тест ##')
    string: str = '=-{('

    result: int = test(pattern, string)

    correctAnswer: int
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Четвертый тест ##')
    string: str = '=-{(=-{(=-{(=-{(=-{(=-{(=-{('

    result: int = test(pattern, string)

    correctAnswer: int
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)

    testNumber += 1

    print('## Пятый тест ##')
    string: str = '=-{( =  -{ (  =-    {(f;lsjdf    ={(   {(=- {( =-{('

    result: int = test(pattern, string)
    print(f"Строка: {string}\nОтвет: {result}")

    correctAnswer: int
    match: bool
    correctAnswer, match = checking(result, testNumber)
    displayAnswer(string, result, correctAnswer, match)


def test(pattern: str, string: str) -> int:
    result: int = len(re.findall(pattern, string))

    return result


def checking(result: int, testNumber: int) -> (int, bool):
    correctAnswers: dict = {
        1: 3,
        2: 0,
        3: 1,
        4: 7,
        5: 2
    }

    return correctAnswers[testNumber], result == correctAnswers[testNumber]
