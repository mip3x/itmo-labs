# import xmltodict
import dicttoxml
from xml.dom.minidom import parseString


def parse_xml(json_data, result_file):
    print(json_data)

    # json_data = {'root': json_data}
    xml = dicttoxml.dicttoxml(json_data, attr_type=False)
    parsedXml = parseString(xml)
    # xmltodict.unparse(json_data, output=result_file, pretty=True)
    result_file.write(parsedXml.toprettyxml())
