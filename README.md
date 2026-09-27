# UnWork - Микросервисы с Kafka и RabbitMQ

## 📚 Архитектура системы

### Поток данных
```
Клиент (curl) 
    ↓ POST /api/orders
order-service (8081)
    ↓ Kafka (topic: order-created)
notification-service (8083)
    ↓ RabbitMQ (exchange: inventory-exchange)
inventory-service (8082)
```

### Роль каждого компонента

#### 🎯 Микросервисы

1. **order-service** (порт 8081)
   - Принимает заказы от клиентов
   - Отправляет события в Kafka
   - REST API: `POST /api/orders`

2. **notification-service** (порт 8083)
   - Слушает Kafka (topic: `order-created`)
   - Получает уведомления о заказах
   - Пересылает события в RabbitMQ

3. **inventory-service** (порт 8082)
   - Слушает RabbitMQ (queue: `inventory-reserved-queue`)
   - Получает события из notification-service
   - Резервирует товары

#### 📨 Middleware

1. **Kafka** (порт 9092)
   - Message Broker для order → notification
   - Гарантирует доставку событий
   - Тема: `order-created`

2. **RabbitMQ** (порт 5672/15672)
   - Message Broker для notification → inventory
   - Web UI: http://localhost:15672 (user/password)
   - Exchange: `inventory-exchange`
   - Queue: `inventory-reserved-queue`

3. **Zookeeper** (порт 2181)
   - Координатор для Kafka
   - Управление брокерами

4. **Kafka UI** (порт 8080)
   - Веб-интерфейс для Kafka
   - http://localhost:8080

---

## 🚀 Запуск системы

### 1. Запуск Docker-сервисов
```bash
docker-compose up -d
```

Проверка:
```bash
docker ps
```

### 2. Запуск Java-сервисов
В IDEA запустите:
- `OrderServiceApplication`
- `NotificationServiceApplication`
- `InventoryServiceApplication`

### 3. Проверка работы
```bash
# Order-service
curl http://localhost:8081/api/orders

# Kafka UI
http://localhost:8080

# RabbitMQ Management
http://localhost:15672 (user/password)
```

---

## 🧪 Тесты

### Типы тестов

#### 1. Интеграционные тесты (Unit + Integration)
Проверяют отдельные компоненты:
- `OrderControllerTest` — REST API order-service
- `NotificationServiceTest` — обработка событий
- `InventoryServiceTest` — работа с Kafka и RabbitMQ

**Запуск:**
```bash
cd order-service && mvn test
cd notification-service && mvn test
cd inventory-service && mvn test
```

#### 2. End-to-End тесты (E2E)
Проверяют полный поток через все сервисы:
- `MicroservicesE2ETest` — тестирование всей системы

**Запуск:**
```bash
mvn test -Dtest=MicroservicesE2ETest
```

### Что проверяют тесты

1. **OrderControllerTest**
   - ✅ POST /api/orders возвращает 202
   - ✅ Пустой productId отклоняется (400)
   - ✅ Правильный формат ответа

2. **OrderEventProducerTest**
   - ✅ События отправляются в Kafka
   - ✅ Producer инициализирован

3. **NotificationServiceTest**
   - ✅ Listener создан и работает
   - ✅ Метод handleInventoryReserved выполняется

4. **InventoryServiceTest**
   - ✅ Получение событий из Kafka
   - ✅ Отправка событий в RabbitMQ

5. **MicroservicesE2ETest**
   - ✅ Полный поток: клиент → order → Kafka → notification → RabbitMQ → inventory
   - ✅ Обработка нескольких заказов
   - ✅ Доступность всех сервисов

---

## 📝 Примеры использования

### Создание заказа (curl)
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d "{\"productId\":\"PROD-123\"}"
```

### Создание заказа (PowerShell)
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/orders" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"productId":"PROD-123"}'
```

### Проверка Kafka UI
Откройте http://localhost:8080
- Кластер: local
- Topics: order-created

### Проверка RabbitMQ
Откройте http://localhost:15672
- Login: user
- Password: password
- Queue: inventory-reserved-queue

---

## 🛠️ Технологии

- **Java 17**
- **Spring Boot 3.2.5**
- **Apache Kafka** — событийная шина
- **RabbitMQ** — асинхронная доставка
- **Maven** — сборка проекта
- **JUnit 5** — тестирование
- **MockMvc** — тестирование REST API
- **EmbeddedKafka** — тестовый Kafka
- **EmbeddedRabbit** — тестовый RabbitMQ

---

## 📂 Структура проекта

```
UnWork/
├── order-service/              # Принимает заказы
│   ├── controller/
│   │   └── OrderController.java
│   ├── service/
│   │   └── OrderEventProducer.java
│   ├── dto/
│   │   └── OrderRequest.java
│   └── test/
│       └── OrderControllerTest.java
├── notification-service/       # Уведомления
│   ├── service/
│   │   └── InventoryEventListener.java
│   └── test/
│       └── NotificationServiceTest.java
├── inventory-service/          # Инвентарь
│   ├── service/
│   │   ├── OrderEventListener.java
│   │   └── InventoryEventProducer.java
│   └── test/
│       └── InventoryServiceTest.java
└── src/test/
    └── e2e/
        └── MicroservicesE2ETest.java
```

---

## 🔍 Отладка

### Логи сервисов
Запустите сервисы в IDEA и смотрите Output window:
- order-service: `>>> Отправлено событие в Kafka: {...}`
- notification-service: `🔔 УВЕДОМЛЕНИЕ: Товар зарезервирован! {...}`
- inventory-service: `<<< Получено событие из Kafka: {...}`

### Возможные ошибки

1. **Connection refused: localhost:9092**
   - Запустите `docker-compose up -d`

2. **Queue declaration failed: inventory-reserved-queue**
   - Проверьте RabbitMQ: http://localhost:15672

3. **404 NOT FOUND**
   - Убедитесь, что сервис запущен на правильном порту

4. **Tests fail: Kafka connection error**
   - Подождите 10-15 секунд после запуска Docker

---

## 📖 Обучение

### Что такое микросервисы?
Архитектура, где приложение разделено на маленькие независимые сервисы:
- Каждый сервис решает свою задачу
- Сервисы общаются через сеть (HTTP,消息)
- Можно масштабировать отдельно

### Зачем Kafka?
- Хранит события долго
- Множество потребителей
- Гарантирует доставку

### Зачем RabbitMQ?
- Быстрая доставка сообщений
- Различные модели маршрутизации
- Надёжность (acknowledgements)

### Тестирование микросервисов
1. **Unit-тесты** — тестируют один метод
2. **Интеграционные** — тестируют взаимодействие с внешними системами
3. **E2E** — тестируют полный поток через все сервисы
