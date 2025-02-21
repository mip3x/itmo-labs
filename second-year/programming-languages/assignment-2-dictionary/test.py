import unittest
import subprocess

class TestDictionaryProgram(unittest.TestCase):
    def run_program(self, input_data):
        path = './dictionary'
        result = subprocess.run(
            [path],
            input=input_data,
            text=True,
            capture_output=True)

        return result.stdout, result.stderr

    def get_last_output_line(self, output):
        output_lines = output.strip().split('\n')
        return output_lines[-1] if output_lines else ""

    def test_first_node(self):
        input_data = "first node"
        expected_out = "first node value"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_meow_node(self):
        input_data = "meow node"
        expected_out = "meow value"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_tab_node(self):
        input_data = "tab      "
        expected_out = "tab value"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_russian_node(self):
        input_data = " русске??? ты?"
        expected_out = "текст на русском о нет...(о как??)"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_long_node(self):
        input_data = "very very long"
        expected_out = "e" * 300
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_spaces_node(self):
        input_data = "  "
        expected_out = "тут одни пробелы"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_empty_data_node(self):
        input_data = ""
        expected_out = "[ДАННЫЕ УДАЛЕНЫ]"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_new_line_node(self):
        input_data = "\n"
        expected_out = "new line value"
        expected_err = ""
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_key_not_found(self):
        input_data = "2347230948908"
        expected_out = "Trying to find word..."
        expected_err = "There is no such key in dictionary!"
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)

    def test_overflow(self):
        input_data = 'e' * 257
        expected_out = ">"
        expected_err = "The length of the string should be no more than 255 characters and it shouldn't be empty!"
        output, error = self.run_program(input_data)
        self.assertEqual(self.get_last_output_line(output), expected_out)
        self.assertEqual(self.get_last_output_line(error), expected_err)


if __name__ == '__main__':
    unittest.main()
