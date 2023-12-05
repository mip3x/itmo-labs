from parser import parse

DATA_DIRECTORY = "./data/"

if __name__ == "__main__":
    input_file_name = DATA_DIRECTORY + "input.json"
    output_file_name = DATA_DIRECTORY + "output.xml"

    input_file_data = open(input_file_name, "r").read()
    # for i in input_file_data[:100]:
    #     if i == ' ':
    #         print('SPACE')
    #     elif i == '\n':
    #         print('NEWLINE')
    #     elif i == '\t':
    #         print('TAB')
    #     else:
    #         print(i)

    with open(output_file_name, 'w') as output_file:
        parse(input_file_data, output_file)
