# Домашнее задание 1. HTTP-сервис

**ФИО**:

**Группа**:

**Предметная область**:Покемоновские карточки

URL: `http://localhost:8080`

## Эндпоинты

**Карточки**
- `GET /cards` — список (`?rarity=`, `?name=`)
- `POST /cards` — создать `{name, type, rarity, art}`
- `GET /cards/{id}` — получить
- `PUT /cards/{id}` — обновить `{name, type, rarity, art}`
- `DELETE /cards/{id}` — удалить

**Игроки**
- `GET /players` — список (`?name=`)
- `POST /players` — создать `{name}`
- `GET /players/{id}` — получить
- `PUT /players/{id}` — переименовать `{name}`
- `DELETE /players/{id}` — удалить

**Казик**
- `GET /players/{id}/cards` — коллекция игрока
- `POST /players/{id}/draw` — выбить карточку (80% COMMON / 20% LEGENDARY)