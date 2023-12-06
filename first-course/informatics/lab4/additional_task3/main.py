from parse_xml import parse_xml
from parse_json import parse_json

DATA_DIRECTORY = "./data/"

if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "output.xml"

    with open(input_file_name, 'r') as input_file:
        input_file_data = parse_json(input_file)

    with open(output_file_name, 'w') as output_file:
        parse_xml(input_file_data, output_file)
