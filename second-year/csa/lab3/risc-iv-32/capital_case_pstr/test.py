import itertools

def read_line(s, buf_size):
    """Read line from input with buffer size limits."""
    assert "\n" in s, "input should have a newline character"
    line = "".join(itertools.takewhile(lambda x: x != "\n", s))

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

def capital_case_pstr(s):
    """Convert the first character of each word in a Pascal string to capital case.

    Capital Case Is Something Like This.

    - Result string should be represented as a correct Pascal string.
    - Buffer size for the message -- `0x20`, starts from `0x00`.
    - End of input -- new line.
    - Initial buffer values -- `_`.

    Python example args:
        s (str): The input string till new line.

    Returns:
        tuple: A tuple containing the capitalized output string and input rest.
    """
    line, rest = read_line(s, 0x20)
    if line is None:
        return [overflow_error_value], rest
    return line.title(), rest


assert capital_case_pstr('hello world\n') == ('Hello World', '')
# and mem[0..31]: 0b 48 65 6c 6c 6f 20 57 6f 72 6c 64 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f
assert capital_case_pstr('python programming\n') == ('Python Programming', '')
# and mem[0..31]: 12 50 79 74 68 6f 6e 20 50 72 6f 67 72 61 6d 6d 69 6e 67 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f 5f
assert capital_case_pstr('HELLO WORLD\n') == ('Hello World', '')
