OPENING_BRACE = '{'
CLOSING_BRACE = '}'
NEW_LINE = '\n'
SPACE = ' '
QUOTE = '"'
COLON = ':'
COMMA = ','

DIGITS = '0123456789'


def parse(data, result_file):
    stack = list()
    phrase = str()

    i = 0
    while i < len(data):
        if data[i] == NEW_LINE:
            result_file.write(NEW_LINE)
            i += 1

        elif data[i] == OPENING_BRACE:
            if len(stack) == 0:
                result_file.write("<root>")
                stack.append("root")

            phrase = ""
            i += 1

        elif data[i] == CLOSING_BRACE:
            if len(stack) > 1:
                closing_element = stack.pop()
                print(closing_element.count(SPACE))
                not_space_index = 0

                closing_element = list(closing_element)
                for index in range(len(closing_element)):
                    if closing_element[index] != SPACE:
                        not_space_index = index
                        break

                closing_element.insert(not_space_index, '</')
                closing_element = ''.join(closing_element)

                result_file.write(f"{closing_element}>")

            else:
                result_file.write(f"</{stack.pop()}>")

            i += 1
                
            phrase = ""

        elif data[i] == COMMA:
            i += 1

        elif data[i] == QUOTE:
            if data[i + 1] == COLON:
                stack.append(phrase)

                space_count = phrase.count(SPACE)
                result_file.write(SPACE * space_count + f"<{phrase.strip()}>")
                phrase = ""
                
                i += 2
            
            elif data[i + 1] == COMMA or data[i + 1] == NEW_LINE:
                result_file.write(phrase.strip() + f"</{stack.pop().strip()}>")
                phrase = ""

                i += 1

            else:
                i += 1

        elif data[i] in DIGITS and ((data[i + 1] == COMMA and data[i + 2] == NEW_LINE) or data[i + 1] == NEW_LINE):
            phrase += data[i]
            result_file.write(phrase.strip() + f"</{stack.pop().strip()}>")
            phrase = ""

            i += 1

        else:
            phrase += data[i]
            i += 1
