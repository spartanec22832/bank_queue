# BankQueue Infrastructure

В этом репозитории находится файл `docker-compose.yml`, который запускает всю систему:
- СУБД PostgreSQL
- Backend (Kotlin + Spring Boot/Ktor)
- Frontend (React + nginx)

## Предварительные требования

1. На машине должен быть установлен Docker (Engine + Docker Compose).
2. Версия JDK 17
3. Клонированы все 4 репозитория гит (данные репозитории приватны):
   ```bash
   git clone https://github.com/inaidE/bank_queue_backend.git
   git clone https://github.com/inaidE/bank_queue_web.git
   git clone https://github.com/inaidE/bank_queue_android.git
   git clone https://github.com/inaidE/bank_queue_infrastructure.git
   ```
   Публичный репозиторий со всеми файлами из проекта (секции backend, web, infrastructure, android) расположен по ссылке:
   ```bash
   git clone https://github.com/spartanec22832/bank_queue.git
   ```
4. Команда для запуска:
   ```bash
   docker compose up -d --build   
   ```
5. Проверка на то, что контейнер запустился:
   ```bash
   docker compose ps
   ```