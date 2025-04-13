import itertools

def read_line(s, buf_size):
    """Read line from input with buffer size limits."""
    assert "\n" in s, "input should have a newline character"
    line = "".join(itertools.takewhile(lambda x: x != "\n", s))

    print(len(line) + 1)
    print(s)
    print(s[len(line) + 1 :])

    if len(line) > buf_size - 1:
        return None, s[buf_size:]

    return line, s[len(line) + 1 :]


def cstr(s, buf_size):
    """Make content for buffer with pascal string (default value for cell: `_`)."""
    assert len(s) + 1 <= buf_size
    buf = s + "\0" + ("_" * (buf_size - len(s) - 1))
    return "".join(itertools.takewhile(lambda c: c != "\0", s)), buf


def pstr(s, buf_size):
    """Make content for buffer with pascal string (default value for cell: `_`)."""
    assert len(s) + 1 <= buf_size
    buf = chr(len(s)) + s + ("_" * (buf_size - len(s) - 1))
    return s, buf


def cbuf(s, buf_size):
    return cstr(s, buf_size)[1]


def pbuf(s, buf_size):
    return pstr(s, buf_size)[1]


def hello_user_pstr(input):
    """Greet the user with Pascal string: ask the name and greet by `Hello, <name>!` message.

    - Result string with greet message should be represented as a correct Pascal string.
    - Buffer size for the message -- `0x20`, starts from `0x00`.
    - End of input -- new line.
    - Initial buffer values -- `_`.

    Python example args:
        input (str): The input string containing the user's name.

    Returns:
        tuple: A tuple containing the greeting message and the remaining input.
    """
    line, rest = read_line(input, 0x20 - len("Hello, " + "!") - 1)

    q = "What is your name?\n"
    if not line:
        return [q, 100], rest

    greet = "Hello, " + line + "!"
    return q + greet, rest


assert hello_user_pstr('Alice\n') == ('What is your name?\nHello, Alice!', '')
# and mem[0..31]: 0d 48 65 6c 6c 6f 2c 20 41 6c 69 63 65 21 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f
assert hello_user_pstr('Bob\n') == ('What is your name?\nHello, Bob!', '')
# and mem[0..31]: 0b 48 65 6c 6c 6f 2c 20 42 6f 62 21 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f

result = hello_user_pstr(input() + '\n')
print(result)
