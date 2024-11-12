from json2xml.utils import readfromjson
from json2xml import json2xml

DATA_DIRECTORY = '../data/'


def parse(input_file_name, output_file_name):
    json_data = readfromjson(input_file_name)
    xml_data = json2xml.Json2xml(data=json_data, attr_type=False).to_xml()

    with open(output_file_name, 'w') as output_file:
        output_file.write(xml_data)


if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "add1_output.xml"

    parse(input_file_name, output_file_name)
