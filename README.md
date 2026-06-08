# WorkspaceBooking

Минимальный запуск через Docker Compose (приложение + PostgreSQL 18).

## Переменные окружения

Создайте файл `.env` в корне проекта и укажите секреты JWT и настройки БД:

```
APP_JWT_ACCESS_SECRET=base64-secret
APP_JWT_REFRESH_SECRET=base64-secret
APP_JWT_ACCESS_EXPIRATION_MINUTES=120
APP_JWT_REFRESH_EXPIRATION_DAYS=14
POSTGRES_DB=workspace_booking
POSTGRES_USER=user
POSTGRES_PASSWORD=password
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/workspace_booking
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## Быстрый старт

```bash
docker compose up --build
```

После старта приложение доступно на `http://localhost:8080`.

## Полезные команды

```bash
# Остановить и удалить контейнеры
docker compose down

# Остановить и удалить контейнеры вместе с томами
docker compose down -v
```
