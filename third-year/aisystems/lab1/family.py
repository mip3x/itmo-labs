from pyswip import Prolog
import sys

# ─────────────────────────────
# 0. Безопасный stdin (чтобы не ловить UnicodeDecodeError в некоторых локалях)
# ─────────────────────────────
#
# Некоторые терминалы/контейнеры запускают Python без корректной локали,
# и тогда input() пытается читать не в utf-8.
# Попробуем насильно выставить stdin/out в utf-8 (Python 3.7+).
try:
    sys.stdin.reconfigure(encoding="utf-8")
    sys.stdout.reconfigure(encoding="utf-8")
except Exception:
    # если это старый Python или не поддерживается — просто тихо пропустим
    pass

# ─────────────────────────────
# 1. Prolog и маппинги имён
# ─────────────────────────────

prolog = Prolog()
prolog.consult("family.pl")

def load_people_maps():
    pid_to_human = {}
    human_to_pid = {}
    for row in prolog.query("name(ID, Human)."):
        pid = row["ID"]
        human = str(row["Human"])
        pid_to_human[pid] = human
        human_to_pid[human.lower()] = pid
    return pid_to_human, human_to_pid

PID_TO_HUMAN, HUMAN_TO_PID = load_people_maps()

def resolve_person_id(human_name_str):
    return HUMAN_TO_PID.get(human_name_str.strip().lower())


# ─────────────────────────────
# 2. Доступ к базе знаний Prolog
# ─────────────────────────────

def get_parents(pid):
    res = []
    for row in prolog.query(f"parent(P,{pid})."):
        res.append(row["P"])
    return res

def get_children(pid):
    res = []
    for row in prolog.query(f"parent({pid},C)."):
        res.append(row["C"])
    return res

def get_grandparents(pid):
    res = []
    for row in prolog.query(f"grandparent(G,{pid})."):
        res.append(row["G"])
    return res

def get_death_year(pid):
    q = list(prolog.query(f"death({pid},Y).", maxresult=1))
    if q:
        return q[0]["Y"]
    return None

def get_marriages(pid):
    # все браки (год, супруг), с двух сторон married/3
    marriages = []
    for row in prolog.query(f"married({pid},Spouse,Year)."):
        marriages.append((int(row["Year"]), row["Spouse"]))
    for row in prolog.query(f"married(Spouse,{pid},Year)."):
        marriages.append((int(row["Year"]), row["Spouse"]))

    uniq = []
    seen = set()
    for (y,s) in marriages:
        if (y,s) not in seen:
            seen.add((y,s))
            uniq.append((y,s))
    uniq.sort(key=lambda t: t[0])
    return uniq

def get_marriage_count(pid):
    return len(get_marriages(pid))

def get_age_in_year(pid, year):
    q = list(prolog.query(f"age_in({pid},{year},Age).", maxresult=1))
    if q:
        return q[0]["Age"]
    return None


# ─────────────────────────────
# 3. Лексер (разбиение строки на токены)
# ─────────────────────────────

PUNCT = set(".?!,;:")

def tokenize(user_text):
    clean_chars = []
    for ch in user_text:
        if ch in PUNCT:
            clean_chars.append(" ")
        else:
            clean_chars.append(ch)
    clean = "".join(clean_chars)

    parts = clean.split()
    tokens = []
    for p in parts:
        tokens.append({
            "raw": p,
            "low": p.lower()
        })
    return tokens


# ─────────────────────────────
# 4. AST-узлы
# ─────────────────────────────

class ASTNode: pass

class WhoAreMyParents(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class WhoAreMyGrandparents(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class WhoAreMyChildren(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class WhenMarried(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class WhenDied(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class HowManyMarriages(ASTNode):
    def __init__(self, subject_pid):
        self.subject_pid = subject_pid

class AgeInYear(ASTNode):
    def __init__(self, subject_pid, year):
        self.subject_pid = subject_pid
        self.year = year


# ─────────────────────────────
# 5. Парсер (tokens -> AST)
# ─────────────────────────────

def collect_name(tokens, pos):
    """
    Собирает потенциальное ФИО начиная с tokens[pos],
    пока не встретит служебное слово.
    """
    stopwords = {
        "кто","мои","родители","дети","деды","дедушки","бабушки",
        "в","каком","году","сколько","браков","было","у",
        "умер","умерла","женился","лет","было"
    }

    collected = []
    while pos < len(tokens):
        low = tokens[pos]["low"]
        if low in stopwords:
            break
        collected.append(tokens[pos]["raw"])
        pos += 1

    full = " ".join(collected).strip()
    return full, pos

def parse_pattern_A(tokens):
    # я <имя> кто мои {родители|дети|деды/...}
    pos = 0
    if len(tokens) < 4:
        return None

    if tokens[pos]["low"] != "я":
        return None
    pos += 1

    human_name, pos = collect_name(tokens, pos)
    if not human_name:
        return None

    if pos >= len(tokens) or tokens[pos]["low"] != "кто":
        return None
    pos += 1

    if pos >= len(tokens) or tokens[pos]["low"] != "мои":
        return None
    pos += 1

    if pos >= len(tokens):
        return None
    tail = tokens[pos]["low"]

    pid = resolve_person_id(human_name)
    if pid is None:
        return ("unknown_person", human_name)

    if tail in ["родители","родитель"]:
        return WhoAreMyParents(pid)
    if tail in ["дети","детей","ребенок","ребёнок"]:
        return WhoAreMyChildren(pid)
    if tail in ["деды","дедушки","бабушки"]:
        return WhoAreMyGrandparents(pid)

    return None

def parse_pattern_B(tokens):
    # в каком году <имя> женился / умер
    if len(tokens) < 5:
        return None

    pos = 0
    if tokens[pos]["low"] != "в":
        return None
    pos += 1

    if pos >= len(tokens) or tokens[pos]["low"] != "каком":
        return None
    pos += 1

    if pos >= len(tokens) or tokens[pos]["low"] != "году":
        return None
    pos += 1

    human_name, pos = collect_name(tokens, pos)
    if not human_name:
        return None

    if pos >= len(tokens):
        return None
    tail = tokens[pos]["low"]

    pid = resolve_person_id(human_name)
    if pid is None:
        return ("unknown_person", human_name)

    if tail.startswith("женился"):
        return WhenMarried(pid)
    if tail.startswith("умер"):
        return WhenDied(pid)

    return None

def parse_pattern_C(tokens):
    # сколько браков было у <имя>
    if len(tokens) < 5:
        return None

    pos = 0
    if tokens[pos]["low"] != "сколько":
        return None
    pos += 1

    if tokens[pos]["low"] != "браков":
        return None
    pos += 1

    if tokens[pos]["low"] != "было":
        return None
    pos += 1

    if tokens[pos]["low"] != "у":
        return None
    pos += 1

    human_name, pos = collect_name(tokens, pos)
    if not human_name:
        return None

    pid = resolve_person_id(human_name)
    if pid is None:
        return ("unknown_person", human_name)

    return HowManyMarriages(pid)

def parse_pattern_D(tokens):
    # сколько лет было <имя> в <год> году
    # пример: "сколько лет было Денис Иванов в 2010 году"
    if len(tokens) < 7:
        return None

    pos = 0
    if tokens[pos]["low"] != "сколько":
        return None
    pos += 1

    if tokens[pos]["low"] != "лет":
        return None
    pos += 1

    if tokens[pos]["low"] != "было":
        return None
    pos += 1

    human_name, pos = collect_name(tokens, pos)
    if not human_name:
        return None

    pid = resolve_person_id(human_name)
    if pid is None:
        return ("unknown_person", human_name)

    if pos >= len(tokens) or tokens[pos]["low"] != "в":
        return None
    pos += 1

    if pos >= len(tokens):
        return None
    year_token = tokens[pos]["low"]
    try:
        year_val = int(year_token)
    except ValueError:
        return None
    pos += 1
    # слово "году" можно проигнорировать

    return AgeInYear(pid, year_val)

def parse_tokens_to_ast(tokens):
    for parser in [parse_pattern_A, parse_pattern_B, parse_pattern_C, parse_pattern_D]:
        node = parser(tokens)
        if node is not None:
            return node
    return None


# ─────────────────────────────
# 6. Выполнение AST → ответ
# ─────────────────────────────

def pretty_person(pid):
    return f"{PID_TO_HUMAN.get(pid, pid)} ({pid})"

def pretty_people_list(pids):
    if not pids:
        return "никто (нет данных в базе)"
    return ", ".join(pretty_person(p) for p in pids)

def execute_ast(astnode):
    # неизвестное имя
    if isinstance(astnode, tuple) and astnode[0] == "unknown_person":
        human_name = astnode[1]
        return f"Персона '{human_name}' не найдена в базе знаний."

    # родители
    if isinstance(astnode, WhoAreMyParents):
        plist = get_parents(astnode.subject_pid)
        return f"Родители {PID_TO_HUMAN[astnode.subject_pid]}: {pretty_people_list(plist)}."

    # деды/бабушки
    if isinstance(astnode, WhoAreMyGrandparents):
        glist = get_grandparents(astnode.subject_pid)
        return f"Дедушки и бабушки {PID_TO_HUMAN[astnode.subject_pid]}: {pretty_people_list(glist)}."

    # дети
    if isinstance(astnode, WhoAreMyChildren):
        clist = get_children(astnode.subject_pid)
        return f"Дети {PID_TO_HUMAN[astnode.subject_pid]}: {pretty_people_list(clist)}."

    # в каком году X женился
    if isinstance(astnode, WhenMarried):
        marriages = get_marriages(astnode.subject_pid)
        if not marriages:
            return f"В базе нет записей о браках {PID_TO_HUMAN[astnode.subject_pid]}."
        lines = []
        for (year, spouse_pid) in marriages:
            lines.append(f"{year} — {pretty_person(spouse_pid)}")
        return (
            f"{PID_TO_HUMAN[astnode.subject_pid]} вступал(а) в брак в следующие годы:\n- "
            + "\n- ".join(lines)
        )

    # в каком году X умер
    if isinstance(astnode, WhenDied):
        dy = get_death_year(astnode.subject_pid)
        if dy is None:
            return (
                f"По базе нет факта смерти для {PID_TO_HUMAN[astnode.subject_pid]} "
                f"(возможно жив/неизвестно)."
            )
        return f"{PID_TO_HUMAN[astnode.subject_pid]} умер(ла) в {dy} году."

    # сколько браков у X
    if isinstance(astnode, HowManyMarriages):
        cnt = get_marriage_count(astnode.subject_pid)
        return f"У {PID_TO_HUMAN[astnode.subject_pid]} всего браков: {cnt}."

    # сколько лет было X в Y году
    if isinstance(astnode, AgeInYear):
        age = get_age_in_year(astnode.subject_pid, astnode.year)
        if age is None:
            return (
                f"В {astnode.year} году {PID_TO_HUMAN[astnode.subject_pid]} "
                f"ещё не родился или возраст не определён."
            )
        return (
            f"В {astnode.year} году {PID_TO_HUMAN[astnode.subject_pid]} "
            f"было {age} лет."
        )

    return "Запрос распознан, но я не знаю, как его выполнить."


# ─────────────────────────────
# 7. REPL (бесконечный цикл вопросов)
# ─────────────────────────────

def repl():
    print("Система принятия решений по семейной базе знаний (Prolog + AST).")
    print("")
    print("Примеры запросов:")
    print("  Я Денис Иванов. Кто мои родители?")
    print("  Я Софья Петрова кто мои деды?")
    print("  Я Наталья Иванова кто мои дети")
    print("  В каком году Павел Иванов умер?")
    print("  В каком году Денис Иванов женился?")
    print("  Сколько браков было у Денис Иванов?")
    print("  Сколько лет было Денис Иванов в 2010 году?")
    print("")
    print("Напиши 'выход' чтобы завершить.")
    print("")

    while True:
        try:
            user_line = input("Введи вопрос: ").strip()
        except UnicodeDecodeError:
            # если снова словим кривую локаль — не падаем
            print(">>> Ошибка чтения ввода. Убедись, что раскладка UTF-8 (русский текст).")
            continue
        except EOFError:
            # ctrl+d
            print("\nПока !")
            break

        if user_line.lower() in ["выход", "exit", "quit"]:
            print("Пока !")
            break

        tokens = tokenize(user_line)
        astnode = parse_tokens_to_ast(tokens)

        if astnode is None:
            print(">>> Я не понял вопрос. Попробуй по образцу выше.")
            continue

        answer = execute_ast(astnode)
        print(">>>", answer)
        # и цикл продолжается

def main():
    repl()

if __name__ == "__main__":
    main()

