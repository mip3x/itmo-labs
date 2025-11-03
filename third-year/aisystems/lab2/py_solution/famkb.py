from collections import defaultdict
import re
import sys


class Person:
    def __init__(self, pid):
        self.pid = pid
        self.full_name = None
        self.gender = None
        self.birth_year = None
        self.death_year = None
        self.parents = set()
        self.children = set()
        self.marriages = []

    def short_label(self):
        if self.birth_year is not None:
            return f"{self.full_name} ({self.birth_year})"
        return self.full_name or self.pid


class FamilyKB:
    def __init__(self):
        self.people = {}
        self.married_raw = []
        self.divorced_raw = []

        self.name_forms_index = defaultdict(list)

    def load_from_file(self, path="family.pl"):
        re_name = re.compile(r"name\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*'([^']+)'\s*\)\s*\.")
        re_male = re.compile(r"male\s*\(\s*([a-zA-Z0-9_]+)\s*\)\s*\.")
        re_female = re.compile(r"female\s*\(\s*([a-zA-Z0-9_]+)\s*\)\s*\.")
        re_birth = re.compile(r"birth\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*([0-9]{4})\s*\)\s*\.")
        re_death = re.compile(r"death\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*([0-9]{4})\s*\)\s*\.")
        re_parent = re.compile(r"parent\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*([a-zA-Z0-9_]+)\s*\)\s*\.")
        re_married = re.compile(r"married\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*([a-zA-Z0-9_]+)\s*,\s*([0-9]{4})\s*\)\s*\.")
        re_divorced = re.compile(r"divorced\s*\(\s*([a-zA-Z0-9_]+)\s*,\s*([a-zA-Z0-9_]+)\s*,\s*([0-9]{4})\s*\)\s*\.")

        def ensure_person(pid):
            if pid not in self.people:
                self.people[pid] = Person(pid)
            return self.people[pid]

        try:
            with open(path, "r", encoding="utf-8") as f:
                for line in f:
                    line = line.strip()
                    if not line or line.startswith("%"):
                        continue

                    m = re_name.match(line)
                    if m:
                        pid, fio = m.groups()
                        p = ensure_person(pid)
                        p.full_name = fio.strip()
                        continue

                    m = re_male.match(line)
                    if m:
                        pid = m.group(1)
                        p = ensure_person(pid)
                        p.gender = "male"
                        continue

                    m = re_female.match(line)
                    if m:
                        pid = m.group(1)
                        p = ensure_person(pid)
                        p.gender = "female"
                        continue

                    m = re_birth.match(line)
                    if m:
                        pid, year = m.groups()
                        p = ensure_person(pid)
                        p.birth_year = int(year)
                        continue

                    m = re_death.match(line)
                    if m:
                        pid, year = m.groups()
                        p = ensure_person(pid)
                        p.death_year = int(year)
                        continue

                    m = re_parent.match(line)
                    if m:
                        par, child = m.groups()
                        p_par = ensure_person(par)
                        p_child = ensure_person(child)
                        p_par.children.add(child)
                        p_child.parents.add(par)
                        continue

                    m = re_married.match(line)
                    if m:
                        a, b, y = m.groups()
                        self.married_raw.append((a, b, int(y)))
                        continue

                    m = re_divorced.match(line)
                    if m:
                        a, b, y = m.groups()
                        self.divorced_raw.append((a, b, int(y)))
                        continue

            self._finalize_marriages()
            self._build_name_forms_index()

        except FileNotFoundError:
            print("ОШИБКА: Не найден файл family.pl", file=sys.stderr)
            sys.exit(1)

    def _finalize_marriages(self):
        union = {}
        for a, b, y in self.married_raw:
            k1 = tuple(sorted([a, b]))
            union[k1] = {"a": a, "b": b, "start": y, "end": None}

        for a, b, y in self.divorced_raw:
            k1 = tuple(sorted([a, b]))
            if k1 in union:
                union[k1]["end"] = y
            else:
                union[k1] = {"a": a, "b": b, "start": None, "end": y}

        for _, info in union.items():
            a = info["a"]
            b = info["b"]
            start = info["start"]
            end = info["end"]

            if a in self.people:
                self.people[a].marriages.append((b, start, end))
            if b in self.people:
                self.people[b].marriages.append((a, start, end))

    @staticmethod
    def _to_genitive_first(first: str) -> str:
        f = first.lower()

        if f == "павел":
            return "павла"
        if f.endswith("ей"):
            return f[:-2] + "ея"
        if f.endswith("ий"):
            return f[:-2] + "ия"
        if f.endswith("й"):
            return f[:-1] + "я"
        if f.endswith("ь"):
            return f + "я"
        if f.endswith("а"):
            return f[:-1] + "ы"
        if f.endswith("я"):
            return f[:-1] + "и"

        return f + "а"

    @staticmethod
    def _to_genitive_last(last: str) -> str:
        l = last.lower()

        if l.endswith(("ов", "ев", "ёв", "ин", "ын")):
            return l + "а"

        if l.endswith("ский") or l.endswith("цкий"):
            return l[:-2] + "ого"

        if l.endswith(("ова", "ева", "ёва", "ина", "ына")):
            return l[:-1] + "ой"

        if l.endswith("ская") or l.endswith("цкая"):
            return l[:-2] + "ой"

        return l

    @staticmethod
    def _tokenize_name(full_name: str):
        parts = full_name.strip().split()
        if len(parts) >= 2:
            first = parts[0]
            last = parts[1]
            rest = parts[2:]
        else:
            first = parts[0]
            last = ""
            rest = []
        return first, last, rest

    def get_genitive_full_name(self, pid: str) -> str:
        p = self.people[pid]
        if not p.full_name:
            return p.pid
        first, last, _ = self._tokenize_name(p.full_name)
        g_first = self._to_genitive_first(first)
        if last:
            g_last = self._to_genitive_last(last)
            return (g_first + " " + g_last).strip()
        return g_first

    def gen_all_name_forms(self, pid):
        person = self.people[pid]
        if not person.full_name:
            return set()

        first, last, rest = self._tokenize_name(person.full_name)
        first_l = first.lower()
        last_l = last.lower()

        forms = set()

        base_full = (first_l + (" " + last_l if last_l else "")).strip()
        forms.add(base_full)
        forms.add(first_l)
        if last_l:
            forms.add(last_l)

        gen_first = self._to_genitive_first(first)
        gen_last = self._to_genitive_last(last) if last else ""
        gen_full = (gen_first + (" " + gen_last if gen_last else "")).strip()
        forms.add(gen_first)
        if gen_last:
            forms.add(gen_last)
        forms.add(gen_full)

        return forms

    def _build_name_forms_index(self):
        for pid in self.people:
            forms = self.gen_all_name_forms(pid)
            for form in forms:
                self.name_forms_index[form].append(pid)

    def find_person_in_text(self, text_lower: str):
        best_pid = None
        best_len = -1

        for form, pid_list in self.name_forms_index.items():
            pattern = r"\b" + re.escape(form) + r"\b"
            if re.search(pattern, text_lower):
                if len(form) > best_len:
                    best_len = len(form)
                    best_pid = pid_list[0]

        return best_pid

    def get_children(self, pid):
        return [self.people[cid] for cid in self.people[pid].children]

    def get_parents(self, pid):
        return [self.people[par] for par in self.people[pid].parents]

    def get_father(self, pid):
        for par in self.people[pid].parents:
            if self.people[par].gender == "male":
                return self.people[par]
        return None

    def get_mother(self, pid):
        for par in self.people[pid].parents:
            if self.people[par].gender == "female":
                return self.people[par]
        return None

    def get_siblings(self, pid):
        sibs = set()
        me_parents = self.people[pid].parents
        for other_pid, other in self.people.items():
            if other_pid == pid:
                continue
            if me_parents & other.parents:
                sibs.add(other_pid)
        return [self.people[s] for s in sibs]

    def get_grandparents(self, pid):
        gps = set()
        for par in self.people[pid].parents:
            for gpar in self.people[par].parents:
                gps.add(gpar)
        return [self.people[g] for g in gps]

    def get_spouses_alltime(self, pid):
        res = []
        seen = set()
        for other_pid, start, end in self.people[pid].marriages:
            if other_pid not in seen:
                seen.add(other_pid)
                res.append((self.people[other_pid], start, end))
        return res

    def compute_age_in_year(self, pid, year: int):
        p = self.people[pid]
        if p.birth_year is None:
            return None
        if year < p.birth_year:
            return None
        return year - p.birth_year
