import sys
sys.path.append('..')

from additional_task3.parse_xml import parse_xml
from additional_task3.parse_json import parse_json

DATA_DIRECTORY = "../data/"


def parse(input_file_name, output_file_name):
    with open(input_file_name, 'r') as input_file:
        input_file_data = parse_json(input_file)

    with open(output_file_name, 'w') as output_file:
        parse_xml(input_file_data, output_file)


if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "add3_output.xml"

    parse(input_file_name, output_file_name)
