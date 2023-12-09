import time
import sys

sys.path.append('..')

from main_task.main import parse as task0_parse
from additional_task1.main import parse as task1_parse
from additional_task2.main import parse as task2_parse
from additional_task3.main import parse as task3_parse

INPUT_FILE_NAME = "../data/input.json"
OUTPUT_FILE_NAME = "../data/add4_output.xml"
TASKS_FUNCS = [task0_parse, task1_parse, task2_parse, task3_parse]


def benchmark(function, input_file_name, output_file): 
    start_time = time.time()


    for _ in range(100):
        function(input_file_name, output_file)

    print(f"--- {time.time() - start_time} seconds ---")


if __name__ == "__main__":
    for task_func in TASKS_FUNCS:
        benchmark(task_func, INPUT_FILE_NAME, OUTPUT_FILE_NAME)
