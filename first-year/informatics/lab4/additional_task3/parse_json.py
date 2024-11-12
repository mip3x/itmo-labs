from pyparsing import Optional, Word, Suppress, nums, Group, delimitedList, Dict, Forward, QuotedString, Literal

LBRACE, RBRACE, LBRACK, RBRACK, COLON, COMMA = map(Suppress, '{}[]:,')

TRUE = Literal("true").setParseAction(lambda: True)
FALSE = Literal("false").setParseAction(lambda: False)
NULL = Literal("null").setParseAction(lambda: None)

integer = Word('-' + nums).setParseAction(lambda x: int(x[0]))
real = Word('-.' + nums).setParseAction(lambda x: float(x[0]))

string = QuotedString('"')
value = Forward()
array = Forward() # value is delimiter ([ value ])
member = Forward() # colon is delimiter (string : value)
json_object = Dict(LBRACE + Optional(delimitedList(member)) + RBRACE)

array << Group(LBRACK + Optional(delimitedList(value)) + RBRACK)
member << Group(string + COLON + value)
value << (real | integer | string | json_object | array | TRUE | FALSE | NULL)


def parse_json(input_file):
    data = input_file.read()
    parsed_data = value.parseString(data).asDict()

    return parsed_data
