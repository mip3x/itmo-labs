TAB = '\t'
NEW_LINE = '\n'
CLOSING_BRACE = '}'


def parse(result_file, matches):
    stack = list()
    tabs = 0

    stack.append("root")
    result_file.write("<root>\n")
    tabs += 1

    for match in matches:
        group = match.group().split(': ')

        if len(group) > 1:
            group = [group[0].replace('"', ''), group[1].replace('"', '')]
            result_file.write(f"{TAB * tabs}<{group[0]}>{group[1]}</{group[0]}>{NEW_LINE}")

        else:
            if group[0] == CLOSING_BRACE:
                group = stack.pop()
                tabs -= 1

                result_file.write(f"{TAB * tabs}</{group}>{NEW_LINE}")

            else:
                group = group[0].replace('"', '')
                stack.append(group)
                result_file.write(f"{TAB * tabs}<{group}>{NEW_LINE}")

                tabs += 1
