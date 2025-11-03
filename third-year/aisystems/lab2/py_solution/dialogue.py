import re
from famkb import *


class DialogueEngine:
    def __init__(self, kb: FamilyKB):
        self.kb = kb
        self.current_pid = None

    @staticmethod
    def _normalize_text(user_text: str) -> str:
        return user_text.strip().lower()

    def _extract_year(self, text: str):
        m = re.search(r"\b(1[89][0-9]{2}|20[0-9]{2})\b", text)
        if m:
            return int(m.group(1))
        return None

    @staticmethod
    def _use_possessive(text_norm: str) -> bool:
        if re.search(r"\bмой\b|\bмоя\b|\bмои\b|\bмо[её]\b|\bу меня\b|\bмне\b", text_norm):
            return True
        return False

    def _choose_target_person(self, text_norm: str):
        if self._use_possessive(text_norm):
            if self.current_pid is not None:
                return self.current_pid

        pid = self.kb.find_person_in_text(text_norm)
        if pid is not None:
            return pid

        if self.current_pid is not None:
            return self.current_pid

        return None

    def _format_people_list(self, plist):
        if not plist:
            return "никого"
        return ", ".join(
            p.short_label() for p in sorted(plist, key=lambda x: x.full_name or x.pid)
        )

    def _header_plural(self, word_you: str, word_name: str, text_norm: str, pid: str):
        if self._use_possessive(text_norm):
            return f"{word_you}: "
        else:
            name_gen = self.kb.get_genitive_full_name(pid)
            return f"{word_name} {name_gen}: "

    def _header_family(self, text_norm: str, pid: str):
        if self._use_possessive(text_norm):
            return "Ваша семья:\n"
        else:
            name_gen = self.kb.get_genitive_full_name(pid)
            return f"Семья {name_gen}:\n"

    def _header_singular(self, word_you: str, word_name: str, text_norm: str, pid: str):
        if self._use_possessive(text_norm):
            return f"{word_you}: "
        else:
            name_gen = self.kb.get_genitive_full_name(pid)
            return f"{word_name} {name_gen}: "

    def _answer_children(self, pid, text_norm):
        kids = self.kb.get_children(pid)
        head = self._header_plural("Ваши дети", "Дети", text_norm, pid)
        if kids:
            return head + self._format_people_list(kids) + "."
        else:
            return head + "нет данных."

    def _answer_parents(self, pid, text_norm):
        parents = self.kb.get_parents(pid)
        head = self._header_plural("Ваши родители", "Родители", text_norm, pid)
        if parents:
            return head + self._format_people_list(parents) + "."
        else:
            return head + "нет данных."

    def _answer_father(self, pid, text_norm):
        f = self.kb.get_father(pid)
        head = self._header_singular("Ваш отец", "Отец", text_norm, pid)
        if f:
            return head + f.short_label() + "."
        else:
            return head + "нет данных."

    def _answer_mother(self, pid, text_norm):
        m = self.kb.get_mother(pid)
        head = self._header_singular("Ваша мать", "Мать", text_norm, pid)
        if m:
            return head + m.short_label() + "."
        else:
            return head + "нет данных."

    def _answer_siblings(self, pid, text_norm):
        sibs = self.kb.get_siblings(pid)
        head = self._header_plural("Ваши братья и сёстры",
                                   "Братья и сёстры",
                                   text_norm, pid)
        if sibs:
            return head + self._format_people_list(sibs) + "."
        else:
            return head + "нет данных."

    def _answer_grandparents(self, pid, text_norm):
        gps = self.kb.get_grandparents(pid)
        head = self._header_plural("Ваши бабушки и дедушки",
                                   "Бабушки и дедушки",
                                   text_norm, pid)
        if gps:
            return head + self._format_people_list(gps) + "."
        else:
            return head + "нет данных."

    def _answer_spouse(self, pid, text_norm):
        spouses = self.kb.get_spouses_alltime(pid)
        head = self._header_plural("Ваши супруг(и)", "Супруг(и)", text_norm, pid)
        if spouses:
            parts = []
            for sp, start, end in spouses:
                if start is not None and end is not None:
                    parts.append(f"{sp.short_label()} (брак {start}–{end})")
                elif start is not None:
                    parts.append(f"{sp.short_label()} (с {start})")
                else:
                    parts.append(f"{sp.short_label()}")
            return head + ", ".join(parts) + "."
        else:
            return head + "нет данных."

    def _answer_age(self, pid, year):
        age = self.kb.compute_age_in_year(pid, year)
        person = self.kb.people[pid]
        if age is None:
            return f"Возраст {person.short_label()} в {year} году: нет данных."
        else:
            return f"Возраст {person.short_label()} в {year} году: {age} лет."

    def _answer_family(self, pid, text_norm):
        parents = self.kb.get_parents(pid)
        kids = self.kb.get_children(pid)
        sibs = self.kb.get_siblings(pid)
        spouses_only = [sp for (sp, _, _) in self.kb.get_spouses_alltime(pid)]

        head = self._header_family(text_norm, pid)

        lines = []
        if parents:
            lines.append("  Родители: " + self._format_people_list(parents))
        if spouses_only:
            lines.append("  Супруг(и): " + self._format_people_list(spouses_only))
        if kids:
            lines.append("  Дети: " + self._format_people_list(kids))
        if sibs:
            lines.append("  Братья и сёстры: " + self._format_people_list(sibs))

        if lines:
            return head + "\n".join(lines)
        else:
            return head + "  нет данных."

    def _detect_intent(self, text_norm: str):
        m_me = re.match(r"^\s*(я|меня зовут)\b(.+)$", text_norm)
        if m_me:
            possible_name = m_me.group(2).strip()
            possible_name = possible_name.replace("-", " ")
            possible_name = possible_name.strip(" .!?")
            pid_me = self.kb.find_person_in_text(possible_name.lower())
            if pid_me is not None:
                return ("set_self", {"pid": pid_me})

        if "сколько" in text_norm and "лет" in text_norm:
            year = self._extract_year(text_norm)
            if year is not None:
                return ("age", {"year": year})

        if re.search(r"\bсемья\b|\bсемью\b|\bсемьи\b", text_norm):
            return ("family", {})

        if re.search(r"\bдет[иья]\b|\bсын|сынов|дочк|дочер|дочь\b", text_norm):
            return ("children", {})

        if re.search(r"родител", text_norm):
            return ("parents", {})

        if re.search(r"\bотец\b|\bпапа\b", text_norm):
            return ("father", {})

        if re.search(r"\bмать\b|\bмама\b", text_norm):
            return ("mother", {})

        if re.search(r"брат|сестр", text_norm):
            return ("siblings", {})

        if re.search(r"дедушк|бабушк", text_norm):
            return ("grandparents", {})

        if re.search(r"\bжена\b|\bмуж\b|супруг|супруга", text_norm):
            return ("spouse", {})

        return ("unknown", {})

    def handle_query(self, user_text: str):
        text_norm = self._normalize_text(user_text)

        if text_norm in ("выход", "exit", "quit", "q"):
            return "__EXIT__"

        intent, extra = self._detect_intent(text_norm)

        if intent == "set_self":
            self.current_pid = extra["pid"]
            me = self.kb.people[self.current_pid]
            return (
                f"Вы: {me.short_label()}.\n"
                "Теперь можно спрашивать, например:\n"
                "  кто мои дети?\n"
                "  кто моя семья?\n"
                "  кто мой отец?\n"
                "  сколько лет мне в 2010?\n"
            )

        target_pid = self._choose_target_person(text_norm)
        if target_pid is None:
            return (
                "Не могу определить, о ком речь. "
                "Скажи сначала кто ты: 'я Алексей Иванов'."
            )

        if intent == "children":
            return self._answer_children(target_pid, text_norm)

        if intent == "parents":
            return self._answer_parents(target_pid, text_norm)

        if intent == "father":
            return self._answer_father(target_pid, text_norm)

        if intent == "mother":
            return self._answer_mother(target_pid, text_norm)

        if intent == "siblings":
            return self._answer_siblings(target_pid, text_norm)

        if intent == "grandparents":
            return self._answer_grandparents(target_pid, text_norm)

        if intent == "spouse":
            return self._answer_spouse(target_pid, text_norm)

        if intent == "age":
            year = extra.get("year")
            if year is None:
                return (
                    "Не вижу год в вопросе. "
                    "Пример: 'сколько лет Денису Иванову в 2010?'"
                )
            return self._answer_age(target_pid, year)

        if intent == "family":
            return self._answer_family(target_pid, text_norm)

        msg_help = (
            "Не понял вопрос.\n"
            "Я понимаю запросы вроде:\n"
            "  кто мои дети?\n"
            "  кто дети Натальи Ивановой?\n"
            "  кто мои родители?\n"
            "  кто мой отец / кто моя мать?\n"
            "  кто моя семья?\n"
            "  кто моя жена / кто муж Виктора Смирнова?\n"
            "  сколько лет Денису Иванову в 2010?\n"
            "Сначала можно сказать: 'я Денис Иванов'."
        )
        return msg_help

