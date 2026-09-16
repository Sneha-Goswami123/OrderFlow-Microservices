# OrderFlow Microservices

OrderFlow is a backend microservices-based order management system built using **Spring Boot, PostgreSQL, RabbitMQ, and Docker Compose**.

The project demonstrates how multiple independent microservices communicate asynchronously using RabbitMQ events.

The system consists of three microservices:

- **Order Service** – manages orders
- **Inventory Service** – manages inventory and updates stock when an order is created
- **Notification Service** – consumes notification events and simulates sending a notification

---

## Architecture

```text
                         Client / Postman
                               |
                               v
                    +----------------------+
                    |    Order Service     |
                    |       :8080          |
                    +----------+-----------+
                               |
                               | OrderCreatedEvent
                               v
                    +----------------------+
                    |       RabbitMQ       |
                    |      order.queue     |
                    +----------+-----------+
                               |
                               | consumes event
                               v
                    +----------------------+
                    |  Inventory Service   |
                    |       :8081          |
                    +----------+-----------+
                               |
                               | Update Stock
                               v
                    +----------------------+
                    |     PostgreSQL       |
                    |    inventorydb       |
                    +----------+-----------+
                               |
                               | Notification Event
                               v
                    +----------------------+
                    |       RabbitMQ       |
                    | notification.queue   |
                    +----------+-----------+
                               |
                               | consumes event
                               v
                    +----------------------+
                    | Notification Service |
                    |       :8082          |
                    +----------------------+
```

---

## Services

### 1. Order Service

The Order Service is responsible for creating and managing customer orders.

#### Responsibilities

- Create orders
- Retrieve all orders
- Retrieve an order by ID
- Update orders
- Delete orders
- Validate order data
- Store orders in PostgreSQL
- Publish an `OrderCreatedEvent` to RabbitMQ

#### Port

```text
8080
```

#### Database

```text
orderflow
```

#### Main Flow

```text
POST /orders
      |
      v
OrderController
      |
      v
OrderService
      |
      v
OrderRepository
      |
      v
PostgreSQL
      |
      v
OrderCreatedEvent
      |
      v
RabbitMQ
```

---

### 2. Inventory Service

The Inventory Service manages product inventory and reacts to newly created orders.

#### Responsibilities

- Add inventory
- Retrieve all inventory
- Retrieve inventory by ID
- Retrieve inventory by item name
- Update inventory
- Delete inventory
- Reduce stock when an order is created
- Consume order events from RabbitMQ
- Publish notification events after processing an order

#### Port

```text
8081
```

#### Database

```text
inventorydb
```

#### Main Flow

```text
RabbitMQ
    |
    | OrderCreatedEvent
    v
InventoryConsumer
    |
    v
InventoryService
    |
    v
reduceStock()
    |
    v
InventoryRepository
    |
    v
PostgreSQL
```

#### Stock Reduction Logic

When an order is received:

1. Find the item by name.
2. Check whether the item exists.
3. Check whether sufficient stock is available.
4. Reduce the quantity.
5. Save the updated inventory.
6. Publish a notification event.

Example:

```text
Initial stock = 20
Order quantity = 2

Updated stock = 18
```

If:

```text
Stock = 3
Order quantity = 5
```

the operation is rejected because there is insufficient stock.

---

### 3. Notification Service

The Notification Service consumes notification events generated after inventory processing.

#### Responsibilities

- Listen to `notification.queue`
- Consume notification events
- Process order information
- Simulate sending a notification through console output

Currently, the service does not connect to a real email or SMS provider. Notification delivery is simulated through console output for learning and demonstration purposes.

#### Port

```text
8082
```

#### Main Flow

```text
RabbitMQ
    |
    | Notification Event
    v
Notification Service
    |
    v
Console Notification
```

Example console output:

```text
Order notification received
Order ID: 10
Item: Keyboard
Quantity: 2
```

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot | Microservices framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Persistent database |
| RabbitMQ | Asynchronous communication |
| Spring AMQP | RabbitMQ integration |
| Docker | Containerization |
| Docker Compose | Infrastructure management |
| Maven | Build and dependency management |
| IntelliJ IDEA | Development environment |
| Postman | API testing |
| Git | Version control |
| GitHub | Source code hosting |

---

## Project Structure

```text
orderflow-microservices/
│
├── order-service/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── inventory-service/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── notification-service/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── docker-compose.yml
├── init.sql
├── .gitignore
└── README.md
```

Each microservice is an independent Spring Boot application with its own Maven configuration.

---

## Database Design

The project uses PostgreSQL with separate databases for the Order and Inventory services.

```text
PostgreSQL
│
├── orderflow
│
└── inventorydb
```

This keeps the data of the Order and Inventory services logically separated.

### Order Database

The Order Service stores order information such as:

```text
id
itemName
quantity
price
status
```

### Inventory Database

The Inventory Service stores:

```text
id
itemName
quantity
```

---

## RabbitMQ

RabbitMQ is used for asynchronous communication between the microservices.

The project uses two queues:

```text
order.queue
notification.queue
```

### order.queue

```text
Producer:
Order Service

Consumer:
Inventory Service
```

The Order Service publishes an `OrderCreatedEvent`.

The Inventory Service consumes this event and updates the inventory.

### notification.queue

```text
Producer:
Inventory Service

Consumer:
Notification Service
```

After successfully processing the order, the Inventory Service publishes a notification event.

The Notification Service consumes that event.

---

## RabbitMQ Event Flow

```text
Order Service
      |
      | OrderCreatedEvent
      v
order.queue
      |
      v
Inventory Service
      |
      | Notification Event
      v
notification.queue
      |
      v
Notification Service
```

This allows the services to communicate asynchronously without directly calling each other through REST APIs.

---

## OrderCreatedEvent

The Order Service creates an event after successfully saving an order.

The event contains information such as:

```text
orderId
itemName
quantity
price
status
```

Example:

```json
{
  "orderId": 10,
  "itemName": "Keyboard",
  "quantity": 2,
  "price": 1500,
  "status": "PLACED"
}
```

The event is serialized into JSON and sent through RabbitMQ.

The Inventory Service receives the event and uses the `itemName` and `quantity` to reduce inventory.

---

## JSON Message Conversion

RabbitMQ messages are converted between Java objects and JSON using Spring AMQP's JSON message converter.

This allows an event such as:

```java
OrderCreatedEvent
```

to be published as JSON and automatically converted back into a Java object by the consumer.

---

## Exception Handling

The Inventory Service uses centralized exception handling.

A global exception handler is implemented using:

```java
@RestControllerAdvice
```

Exceptions are handled using:

```java
@ExceptionHandler
```

For example, when inventory is not found:

```text
HTTP 404 NOT FOUND
```

A validation error returns:

```text
HTTP 400 BAD REQUEST
```

This keeps error handling centralized instead of putting repetitive exception-handling code inside every controller.

---

## Validation

The services use Jakarta Validation for validating incoming request data.

Validation helps prevent invalid data from entering the system.

Invalid requests can result in:

```text
400 BAD REQUEST
```

with information about the fields that failed validation.

---

## Logging

The Order Service uses SLF4J logging to record important operations such as:

- Fetching orders
- Creating orders
- Updating orders
- Deleting orders
- Missing orders
- Publishing events

Example:

```text
Creating new order: Keyboard
Order event published successfully.
```

Logging makes it easier to understand and debug the application.

---

## Docker Compose

Docker Compose is used to manage the infrastructure required by the microservices.

Currently, Docker Compose manages:

- PostgreSQL
- RabbitMQ

The three Spring Boot microservices are run from IntelliJ during development.

### Docker Architecture

```text
Docker Compose
│
├── PostgreSQL
│     ├── orderflow
│     └── inventorydb
│
└── RabbitMQ
      ├── order.queue
      └── notification.queue
```

---

## Docker Compose Services

### PostgreSQL

The PostgreSQL container uses:

```text
Image: postgres:16
Container: orderflow-postgres
```

The host port is:

```text
5433
```

which maps to PostgreSQL's container port:

```text
5432
```

Therefore, the Spring Boot services connect using:

```text
localhost:5433
```

### RabbitMQ

The RabbitMQ container uses:

```text
Image: rabbitmq:3-management
Container: orderflow-rabbitmq
```

Ports:

```text
5672  → AMQP
15672 → RabbitMQ Management UI
```

RabbitMQ Management UI:

```text
http://localhost:15672
```

Development credentials:

```text
Username: guest
Password: guest
```

---

## Starting Infrastructure

Make sure Docker Desktop is running.

From the project root:

```powershell
docker compose up -d
```

Check running containers:

```powershell
docker ps
```

Expected containers:

```text
orderflow-postgres
orderflow-rabbitmq
```

To stop the containers:

```powershell
docker compose down
```

---

## Database Initialization

The project includes:

```text
init.sql
```

The script creates the second database required by the Inventory Service:

```sql
CREATE DATABASE inventorydb;
```

The PostgreSQL container creates:

```text
orderflow
```

and the initialization script creates:

```text
inventorydb
```

The result is:

```text
PostgreSQL
│
├── orderflow
└── inventorydb
```

---

## Running the Application

### Step 1 — Start Docker Desktop

Make sure Docker Desktop is running.

### Step 2 — Start PostgreSQL and RabbitMQ

From the project root:

```powershell
docker compose up -d
```

Verify:

```powershell
docker ps
```

### Step 3 — Start Order Service

Run the Order Service from IntelliJ.

Order Service runs on:

```text
http://localhost:8080
```

### Step 4 — Start Inventory Service

Run the Inventory Service from IntelliJ.

Inventory Service runs on:

```text
http://localhost:8081
```

### Step 5 — Start Notification Service

Run the Notification Service from IntelliJ.

Notification Service runs on:

```text
http://localhost:8082
```

---

## API Endpoints

### Order Service

```text
GET    /orders
POST   /orders
GET    /orders/{id}
PUT    /orders/{id}
DELETE /orders/{id}
```

### Inventory Service

```text
GET    /inventory
POST   /inventory
GET    /inventory/{id}
GET    /inventory/item/{itemName}
PUT    /inventory/{id}
DELETE /inventory/{id}
```

A test endpoint is also available:

```text
GET /inventory/test
```

---

## Example API Requests

### Add Inventory

```http
POST http://localhost:8081/inventory
```

Request body:

```json
{
  "itemName": "Keyboard",
  "quantity": 20
}
```

### Create Order

```http
POST http://localhost:8080/orders
```

Request body:

```json
{
  "itemName": "Keyboard",
  "quantity": 2,
  "price": 1500,
  "status": "PLACED"
}
```

---

## End-to-End Flow

Suppose the inventory contains:

```text
Keyboard → 20
```

A user creates:

```json
{
  "itemName": "Keyboard",
  "quantity": 2,
  "price": 1500,
  "status": "PLACED"
}
```

The complete flow is:

```text
1. Client sends order
        |
        v
2. Order Service
        |
        | Saves order
        v
3. PostgreSQL
        |
        | Generates order ID
        v
4. Order Service creates OrderCreatedEvent
        |
        v
5. RabbitMQ - order.queue
        |
        v
6. Inventory Service consumes event
        |
        v
7. Inventory Service checks stock
        |
        v
8. Inventory quantity decreases
        |
        v
9. PostgreSQL updates inventory
        |
        v
10. Inventory Service publishes notification event
        |
        v
11. RabbitMQ - notification.queue
        |
        v
12. Notification Service consumes event
        |
        v
13. Notification is simulated in console
```

Example:

```text
Before order:

Keyboard = 20


Order:

Keyboard quantity = 2


After order:

Keyboard = 18
```

---

## Testing

The project was tested using Postman and the RabbitMQ Management UI.

The main event-driven flow was verified:

```text
Order Service
      ↓
RabbitMQ
      ↓
Inventory Service
      ↓
PostgreSQL
      ↓
RabbitMQ
      ↓
Notification Service
```

The successful test demonstrated that:

- The order was saved in PostgreSQL.
- The database generated an order ID.
- The Order Service published an order event.
- The Inventory Service received the event.
- Inventory stock was reduced.
- The updated inventory was saved.
- A notification event was published.
- The Notification Service received the event.
- Order information was displayed by the Notification Service.

---

## RabbitMQ Monitoring

RabbitMQ Management UI can be used to monitor:

- Queues
- Messages
- Consumers
- Ready messages
- Unacknowledged messages

Queue responsibilities:

```text
order.queue
    Producer → Order Service
    Consumer → Inventory Service

notification.queue
    Producer → Inventory Service
    Consumer → Notification Service
```

When the Notification Service is running, the notification queue has an active consumer.

---

## Important Issue Solved: Order ID Mapping

During development, the Order ID was initially received as `null` by the Notification Service.

The problem was a mismatch between the event field names.

The producer sent:

```text
orderId
```

while the consumer DTO initially used:

```text
id
```

Since JSON property names are matched during deserialization, the fields did not map correctly.

The DTOs were updated to use the same property:

```text
orderId
```

After the fix, the Notification Service correctly received the order ID.

Example:

```text
Order ID: 10
```

---

## Git and GitHub

The project is maintained using Git and hosted on GitHub.

Repository structure:

```text
orderflow-microservices/
│
├── order-service/
├── inventory-service/
├── notification-service/
├── docker-compose.yml
├── init.sql
├── .gitignore
└── README.md
```

Basic Git workflow:

```powershell
git status
git add .
git commit -m "Describe your changes"
git push
```

---

## Project Development Process

The project was developed incrementally.

### Phase 1 — Order Service

Implemented:

- Spring Boot application
- REST APIs
- PostgreSQL integration
- JPA/Hibernate
- CRUD operations
- Validation
- Exception handling
- Logging

### Phase 2 — RabbitMQ Integration

Implemented:

- RabbitMQ setup
- Event producer
- `OrderCreatedEvent`
- Asynchronous order event publishing

### Phase 3 — Inventory Service

Implemented:

- Inventory CRUD APIs
- PostgreSQL integration
- Inventory repository
- Stock reduction
- RabbitMQ consumer
- Event processing
- Exception handling

### Phase 4 — Notification Service

Implemented:

- Spring Boot Notification Service
- RabbitMQ consumer
- Notification queue
- Event consumption
- Console-based notification simulation

### Phase 5 — Docker Compose

Implemented:

- PostgreSQL Docker container
- RabbitMQ Docker container
- PostgreSQL database initialization
- Docker volume for PostgreSQL data
- Docker Compose configuration

---

## What I Learned

This project provided practical experience with:

### Spring Boot

- Controllers
- Services
- Repositories
- Dependency Injection
- REST APIs
- Configuration
- Exception handling
- Validation
- Logging

### Spring Data JPA

- Entities
- Repositories
- CRUD operations
- Derived query methods
- PostgreSQL integration
- Hibernate

### RabbitMQ

- Producers
- Consumers
- Queues
- Events
- Asynchronous communication
- Message conversion
- RabbitMQ Management UI

### Microservices

- Separating business responsibilities
- Service-to-service communication
- Event-driven architecture
- Independent service databases
- Loose coupling

### Docker

- Docker images
- Containers
- Ports
- Volumes
- Docker Compose
- Running PostgreSQL and RabbitMQ in containers

### Development Tools

- IntelliJ IDEA
- Maven
- Postman
- Docker Desktop
- Git
- GitHub

---

## Current Project Scope

The project intentionally focuses on the core microservices architecture.

### Implemented

- Order Service
- Inventory Service
- Notification Service
- PostgreSQL
- RabbitMQ
- REST APIs
- CRUD operations
- Validation
- Exception handling
- Logging
- Event-driven communication
- End-to-end event flow
- Docker
- Docker Compose

### Not Included in the Current Version

The following technologies/features are intentionally outside the current project scope:

- Redis
- Kafka
- Kubernetes
- Real email/SMS provider
- Cloud deployment
- Advanced message retry mechanisms
- Distributed tracing

These can be added later as separate learning exercises.

---

## Future Improvements

Possible future improvements include:

- Add Redis caching
- Add retry and dead-letter queue handling
- Add automated unit and integration tests
- Add real email/SMS notification provider
- Add API documentation with OpenAPI/Swagger
- Add centralized configuration
- Add authentication and authorization
- Dockerize the Spring Boot services themselves
- Deploy the application to the cloud
- Add monitoring and distributed tracing

---

## Key Learning Outcome

The main goal of OrderFlow is to understand how independent Spring Boot microservices can work together using asynchronous communication.

The core concept can be summarized as:

```text
Order Created
     ↓
RabbitMQ Event
     ↓
Inventory Updated
     ↓
RabbitMQ Event
     ↓
Notification Processed
```

This project demonstrates a basic **event-driven microservices architecture** using **Spring Boot, PostgreSQL, RabbitMQ, and Docker Compose**.
