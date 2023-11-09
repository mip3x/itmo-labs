import task1, task2

PATTERN: str = r'=-{\('

task_number = input("Введите номер задания (1-3): ")
if task_number == '1':
    task1.task1Testing(PATTERN)
elif task_number == '2':
    task2.task2Testing()
elif task_number == '3':
    print('Третье задание')
else:
    print('Неверный ввод')