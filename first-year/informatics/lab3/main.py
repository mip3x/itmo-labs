import task1, task2, task3

FIRST_TASK_PATTERN: str = r'=-{\('
SECOND_TASK_PATTERN: str = r'(?<!:)(([01]\d|2[0-3]):[0-5]\d(?::[0-5]\d)?)(?!:)'

VOWELS: str = 'аеиоуыэюя'
CONSONANTS: str = 'йцкнгшщзхъфвпрлджчсмтьб'
THIRD_TASK_PATTERN = rf'(\b[{CONSONANTS}]*([{VOWELS}])[{CONSONANTS}]*(\2[{CONSONANTS}]*)*\b)'

task_number = input("Введите номер задания (1-3): ")
if task_number == '1':
    task1.taskFirstTesting(FIRST_TASK_PATTERN)
elif task_number == '2':
    task2.taskSecondTesting(SECOND_TASK_PATTERN)
elif task_number == '3':
    task3.taskThirdTesting(THIRD_TASK_PATTERN)
else:
    print('Неверный ввод')
