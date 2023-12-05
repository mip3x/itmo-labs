import re
from parser import parse

DATA_DIRECTORY = "./data/"

if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "output.xml"

    input_file_data = open(input_file_name, "r").read()

    regex = r"(\".*\")(: \d+)?|\}"
    matches = re.finditer(regex, input_file_data)

    with open(output_file_name, 'w') as output_file:
        parse(output_file, matches)
