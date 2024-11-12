OPENING_BRACE = '{'
CLOSING_BRACE = '}'
NEW_LINE = '\n'
TAB = '\t'
QUOTE = '"'
COLON = ':'
COMMA = ','

DIGITS = '0123456789'


def parse(data, result_file):
    stack = list()
    phrase = str()
    tabs = 0

    i = 0
    while i < len(data):
        if data[i] == NEW_LINE:
            result_file.write(NEW_LINE)

        elif data[i] == OPENING_BRACE:
            if len(stack) == 0:
                result_file.write("<root>")
                stack.append("root")

            tabs += 1
            phrase = ""

        elif data[i] == CLOSING_BRACE:
            tabs -= 1

            result_file.write(f"{TAB * tabs}</{stack.pop().strip()}>")
            phrase = ""

        elif data[i] == QUOTE:
            if data[i + 1] == COLON:
                phrase = phrase.replace(':', '', 1).replace(',', '')
                stack.append(phrase)
                result_file.write(f"{TAB * tabs}<{phrase.strip()}>")
                phrase = ""
            
            elif data[i + 1] == COMMA or data[i + 1] == NEW_LINE:
                phrase = phrase.replace(':', '', 1).replace(',', '')
                result_file.write(phrase.strip() + f"</{stack.pop().strip()}>")
                phrase = ""

        elif data[i] in DIGITS and ((data[i + 1] == COMMA and data[i + 2] == NEW_LINE) or data[i + 1] == NEW_LINE):
            phrase = phrase.replace(':', '', 1).replace(',', '')
            phrase += data[i]
            result_file.write(phrase.strip() + f"</{stack.pop().strip()}>")
            phrase = ""

        else:
            phrase += data[i]

        i += 1
