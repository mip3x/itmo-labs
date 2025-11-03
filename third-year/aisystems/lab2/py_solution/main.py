import sys
from famkb import FamilyKB
from dialogue import DialogueEngine
from pathlib import Path


FAMILY_FILE = Path(__file__).parent.parent / "kb.pl"


def main():
    try:
        sys.stdin.reconfigure(encoding='utf-8')
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

    kb = FamilyKB(FAMILY_FILE)
    kb.load_from_file()

    engine = DialogueEngine(kb)

    print("База: kb.pl\n"
          "Примеры:\n"
          "  я Алексей Иванов\n"
          "  кто мои дети?\n"
          "  кто моя семья?\n"
          "  кто родители у елены ивановой?\n"
          "  кто мой отец?\n"
          "  кто моя жена?\n"
          "  сколько лет Денису Иванову в 2010?\n"
          "Чтобы выйти, напиши: выход\n")

    while True:
        try:
            user_text = input("> ")
        except (EOFError, KeyboardInterrupt):
            print("\nКонец")
            break
        except UnicodeDecodeError:
            print("Не удалось прочитать ввод как UTF-8. Попробуй ещё раз.")
            continue

        answer = engine.handle_query(user_text)
        if answer == "__EXIT__":
            print("Конец")
            break

        print(answer)
        print()


if __name__ == "__main__":
    main()
