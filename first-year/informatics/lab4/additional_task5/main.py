import json
import pandas as pd

DATA_DIRECTORY = '../data/'


def parse(input_file_name, output_file_name):
    json_string = open(input_file_name, "r").read()
    json_object_pd = pd.read_json(json_string)
    json_object_pd.to_csv(output_file_name)


if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "add5_output.csv"

    parse(input_file_name, output_file_name)
