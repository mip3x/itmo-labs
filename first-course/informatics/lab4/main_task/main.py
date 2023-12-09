import sys
sys.path.append('..')

from main_task.parser import parse as write_to_xml

DATA_DIRECTORY = "../data/"


def parse(input_file_name, output_file_name):
    input_file_data = open(input_file_name, "r").read()

    with open(output_file_name, 'w') as output_file:
        write_to_xml(input_file_data, output_file)


if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "main_output.xml"

    parse(input_file_name, output_file_name)
