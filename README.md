# WUPHF

Простая социальная сеть. Приложение позволяет пользователям регистрироваться, публиковать короткие сообщения, подписываться друг на друга и читать персональную ленту подписок.

## Структура

```
wuphf/
├── pom.xml                           # Maven parent (multi-module)
├── common/                           # Общий JAR: ApiResponse, exceptions, ApiExceptionHandler, CORS, Spring Security + JWT
├── ums/                              # User Management Service (:9000, БД ums)
│   └── src/main/java/com/kiprin/ums/
├── messages/                         # Messages, Subscriptions & Feed Service (:9001, БД messages)
│   └── src/main/java/com/kiprin/messages/
├── db/init/                          # SQL init (00_databases.sql, 01_ums.sql, 02_messages.sql)
├── deploy/                           # Docker & Kubernetes (kustomize + kind)
├── docker-compose.yml                # Postgres :5432 (один инстанс, две БД: ums и messages)
├── Makefile                          # make up / run-ums / run-messages / test / down
├── UMS Postman Collection.json
└── Messages Postman Collection.json
```

## Быстрый старт

```bash
make up # Postgres (БД ums + messages) + UMS :9000 + Messages :9001
```

Либо раздельно:

```bash
make db wait-db init-db
make run-ums
make run-messages
```

Суперадмин (seed): `admin@wuphf.local` / `superadmin`.
