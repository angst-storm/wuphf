# WUPHF

Простая социальная сеть. Приложение позволяет пользователям регистрироваться, публиковать короткие сообщения, подписываться друг на друга и получать персональную ленту сообщений.

## Структура

```
wuphf/
├── ums/                         # User Management Service
│   └── src/main/java/com/kiprin/ums/
│       ├── config/              # CORS, session auth filter, JDBC callbacks
│       ├── controllers/         # HTTP API
│       ├── domain/
│       ├── dto/
│       ├── repository/          # Spring Data JDBC
│       ├── service/             # бизнес-логика и авторизация
│       └── web/
├── db/init/                     # SQL init для Postgres
├── docker-compose.yml           # Postgres :5432
├── Makefile                     # make up / run / down
└── UMS Postman Collection.json  # коллекция API для Postman
```

## Быстрый старт (UMS)

```bash
make up # Postgres + UMS :9000
```

Суперадмин (seed): `admin@wuphf.local` / `superadmin`.
