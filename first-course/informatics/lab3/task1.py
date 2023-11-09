import re


def task1Testing(pattern: str):
    print('### ПЕРВОЕ ЗАДАНИЕ ###\n')

    print('## Первый тест ##')
    string: str = '=-{(sldf=-{(=-{sdf(=sdf-{(===-{('

    print(f"Строка: {string}\nОтвет: {test(pattern, string)}\n")

    print('## Второй тест ##')
    string: str = 'Здесь нет никаких смайликов'

    print(f"Строка: {string}\nОтвет: {test(pattern, string)}\n")

    print('## Третий тест ##')
    string: str = '=-{('

    print(f"Строка: {string}\nОтвет: {test(pattern, string)}\n")

    print('## Четвертый тест ##')
    string: str = '=-{(=-{(=-{(=-{(=-{(=-{(=-{('

    print(f"Строка: {string}\nОтвет: {test(pattern, string)}\n")

    print('## Пятый тест ##')
    string: str = '=-{( =  -{ (  =-    {(f;lsjdf    ={(   {(=- {( =-{('

    print(f"Строка: {string}\nОтвет: {test(pattern, string)}")


def test(pattern: str, string: str) -> int:
    result: int = len(re.findall(pattern, string))

    return result
