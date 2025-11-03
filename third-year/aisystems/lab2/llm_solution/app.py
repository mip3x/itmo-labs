import json
import re
import time
from pathlib import Path
from typing import Optional, List, Dict, Any

import requests
from pyswip import Prolog

OLLAMA_CHAT_URL = "http://localhost:11434/api/chat"
MODEL = "llama3.1:8b"
KB_PATH = Path("kb.pl")
HISTORY_LOG = Path("history.json")

WELCOME = (
    "LLM <-> Prolog чат для базы знаний семейного дерева.\n"
    "Примеры: «я Денис Иванов. кто мои дети?», «кто мой сын?», "
    "«сколько лет моим детям в 2021?», «кто моя жена в 2019?»\n"
    "Выход — /exit\n"
)

SYSTEM_PLANNER = """Ты — компилятор русских вопросов к семейной БЗ (SWI-Prolog).
ВЕРНИ СТРОГО ОДИН JSON (без текста вокруг) со схемой:
{
  "assumptions": "string",
  "queries": [{"purpose":"string", "prolog":"string"}],
  "need_user": false
}
ТРЕБОВАНИЯ:
- Для вопросов вида «я/мои/мой/моя ...» ты ОБЯЗАТЕЛЬНО используешь идентификатор current_user в теле запроса.
  current_user — это точный атом person-id из БЗ, который мы передаем тебе в JSON входа (или null, если пользователь не представился).
- НЕЛЬЗЯ строить запросы, перечисляющие всех людей (например, просто name(Id,Name).). Любой запрос должен быть о current_user.
- Каждое выражение Prolog завершай точкой. БЗ не модифицируй и cut (!) не используй.
- Возвращай имена через name/2.

ПРИМЕРЫ:

1) «кто мои дети?»
{
  "assumptions": "",
  "queries": [{"purpose":"поиск детей", "prolog":"(parent(current_user, C), name(C, Name))."}],
  "need_user": false
}

2) «кто мой сын?»
{
  "assumptions": "",
  "queries": [{"purpose":"поиск сыновей", "prolog":"(parent(current_user, C), male(C), name(C, Name))."}],
  "need_user": false
}

3) «кто мои внуки?»
{
  "assumptions": "",
  "queries": [{"purpose":"поиск внуков", "prolog":"(grandparent(current_user, G), name(G, Name))."}],
  "need_user": false
}

4) «сколько лет моим детям в 2021?»
{
  "assumptions": "",
  "queries": [{"purpose":"возраст детей в 2021", "prolog":"(parent(current_user, C), age_in(C, 2021, Age), name(C, Name))."}],
  "need_user": false
}

5) «кто моя жена в 2019?»
{
  "assumptions": "",
  "queries": [{"purpose":"супруга в 2019", "prolog":"(spouse_in(current_user, 2019, S), name(S, Name))."}],
  "need_user": false
}

Если current_user отсутствует и без него нельзя понять, о ком речь — верни:
{"assumptions": "", "queries": [], "need_user": true}"""

SYSTEM_VERBALIZER = """Ты — формируешь краткий и точный ответ ТОЛЬКО на основе пришедших результатов Prolog (results).
- Если результатов нет — скажи, что в БЗ нет данных для ответа.
- НЕЛЬЗЯ додумывать факты.
- Имена бери из name/2, перечисляй по-человечески.
"""

def chat(messages: List[Dict[str, str]], *, format_json: bool = False) -> str:
    payload: Dict[str, Any] = {
        "model": MODEL,
        "messages": messages,
        "stream": False,
        "options": {"temperature": 0.2, "seed": 42, "num_ctx": 8192},
    }
    if format_json:
        payload["format"] = "json"
    r = requests.post(OLLAMA_CHAT_URL, json=payload, timeout=120)
    r.raise_for_status()
    data = r.json()
    return data.get("message", {}).get("content", "").strip()

def parse_json_strict(s: str) -> Dict[str, Any]:
    try:
        return json.loads(s)
    except Exception:
        m = re.search(r"\{.*\}\s*$", s, flags=re.DOTALL)
        if m:
            try:
                return json.loads(m.group(0))
            except Exception:
                pass
    return {"assumptions": "", "queries": [], "need_user": True, "raw": s}

def run_prolog(prolog: Prolog, query: str) -> List[Dict[str, Any]]:
    q = query.rstrip(".")
    try:
        res = []
        for row in prolog.query(q, maxresult=200):
            res.append({k: (v.decode() if isinstance(v, bytes) else v) for k, v in row.items()})
        return res
    except Exception as e:
        return [{"__error__": f"{e}"}]

def _to_str(x: Any) -> str:
    if isinstance(x, bytes):
        try:
            return x.decode("utf-8", errors="ignore")
        except Exception:
            return str(x)
    return str(x)

def _norm_name(s: str) -> str:
    s = s.strip()
    s = re.sub(r"\s+", " ", s)
    return s.lower()

def find_current_user(prolog: Prolog, text: str) -> Optional[str]:
    m = re.search(r"(?:\bя\b|\bменя зовут\b|\bя\s*-\s*)\s*([А-ЯЁA-Za-zё]+)\s+([А-ЯЁA-Za-zё]+)", text, re.I)
    if not m:
        return None
    full = f"{m.group(1)} {m.group(2)}"
    full_norm = _norm_name(full)
    for r in prolog.query("name(Id, Name)."):
        name_val = _to_str(r["Name"])
        if _norm_name(name_val) == full_norm:
            return r["Id"]
    return None

def materialize_query(q: str, current_user: Optional[str]) -> str:
    if current_user and "current_user" in q:
        return q.replace("current_user", current_user)
    return q

def query_mentions_user(q: str, current_user: Optional[str]) -> bool:
    if "current_user" in q:
        return True
    if current_user:
        pattern = r"\b" + re.escape(current_user) + r"\b"
        return re.search(pattern, q) is not None
    return False

def save_history(entry: Dict[str, Any]):
    HISTORY_LOG.parent.mkdir(parents=True, exist_ok=True)
    if HISTORY_LOG.exists():
        try:
            prev = json.loads(HISTORY_LOG.read_text(encoding="utf-8"))
            if not isinstance(prev, list):
                prev = []
        except Exception:
            prev = []
    else:
        prev = []
    prev.append(entry)
    HISTORY_LOG.write_text(json.dumps(prev, ensure_ascii=False, indent=2), encoding="utf-8")

def main():
    print(WELCOME)

    prolog = Prolog()
    prolog.consult(str(KB_PATH))

    current_user: Optional[str] = None

    while True:
        try:
            user_text = input("YOU> ").strip()
        except (EOFError, KeyboardInterrupt):
            print("\nbye!")
            break

        if not user_text:
            continue
        if user_text.lower() in ("/exit", "/quit"):
            print("bye!")
            break

        if not current_user:
            cu = find_current_user(prolog, user_text)
            if cu:
                current_user = cu
                print(f"[debug] current_user = {current_user}")

        planner_msgs = [
            {"role": "system", "content": SYSTEM_PLANNER},
            {"role": "user", "content": json.dumps({"current_user": current_user, "text": user_text}, ensure_ascii=False)},
        ]
        raw_plan = chat(planner_msgs, format_json=True)
        plan = parse_json_strict(raw_plan)

        if plan.get("need_user") and not plan.get("queries"):
            answer = "Нужно уточнить, о ком речь. Скажи: «я Имя Фамилия», а затем вопрос (например, «кто мои дети?»)."
            print("BOT>", answer)
            save_history({"ts": time.time(), "q": user_text, "current_user": current_user, "plan": plan, "answer": answer})
            continue

        queries = plan.get("queries", [])
        if not queries:
            answer = "Не удалось построить запрос к БЗ. Переформулируй, пожалуйста."
            print("BOT>", answer)
            save_history({"ts": time.time(), "q": user_text, "current_user": current_user, "plan": plan, "answer": answer})
            continue

        safe_queries = []
        for q in queries:
            pq = q.get("prolog", "").strip()
            if not pq.endswith("."):
                pq += "."
            if not query_mentions_user(pq, current_user):
                continue
            materialized = materialize_query(pq, current_user)
            safe_queries.append({"purpose": q.get("purpose", ""), "prolog": materialized})

        if not safe_queries:
            answer = "Мне нужен ваш контекст (current_user) в запросах. Скажи: «я Имя Фамилия», затем вопрос."
            print("BOT>", answer)
            save_history({"ts": time.time(), "q": user_text, "current_user": current_user, "plan": plan, "answer": answer})
            continue

        results: List[Dict[str, Any]] = []
        for item in safe_queries:
            rows = run_prolog(prolog, item["prolog"])
            results.append({"purpose": item["purpose"], "query": item["prolog"], "rows": rows})

        verbalizer_msgs = [
            {"role": "system", "content": SYSTEM_VERBALIZER},
            {"role": "user", "content": json.dumps({
                "question": user_text,
                "current_user": current_user,
                "results": results
            }, ensure_ascii=False)},
        ]
        answer = chat(verbalizer_msgs, format_json=False)

        print("BOT>", answer)

        save_history({
            "ts": time.time(),
            "q": user_text,
            "current_user": current_user,
            "plan": plan,
            "queries_safe": safe_queries,
            "results": results,
            "answer": answer
        })

if __name__ == "__main__":
    try:
        main()
    except requests.exceptions.RequestException as e:
        print("[Ошибка HTTP к Ollama]", e)
    except Exception as e:
        print("[Фатальная ошибка]", e)
        raise
