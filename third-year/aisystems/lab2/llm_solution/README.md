# llm-solution

## Локальное развёртывание `ollama`

1. Развёртывание самой `LLM`:

```sh
docker compose up -d
```

2. Установка модели

```sh
docker exec -it ollama ollama pull llama3.1:8b
```

3. Просмотр доступных моделей

```sh
docker exec -it ollama ollama list
```

Пример вывода:

```
NAME           ID              SIZE      MODIFIED
llama3.1:8b    46e0c10c039e    4.9 GB    6 hours ago
```
