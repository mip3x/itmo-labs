import sys
sys.path.append('..')

import re
from additional_task2.parser import parse as write_to_xml

DATA_DIRECTORY = "../data/"


def parse(input_file_name, output_file_name):
    input_file_data = open(input_file_name, "r").read()

    regex = r"(\".*\")(: \d+)?|\}"
    matches = re.finditer(regex, input_file_data)

    with open(output_file_name, 'w') as output_file:
        write_to_xml(output_file, matches)


if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "add2_output.xml"

    parse(input_file_name, output_file_name)
