# PayPal Clone — Backend Overview

## Project Summary

PayPal Clone is a Java Spring Boot microservices backend that implements core payment platform capabilities: user authentication (JWT), wallet management (holds, capture, credit, debit), transactions (Kafka events), product catalog, rewards and notifications. The system uses a Spring Cloud Gateway as API gateway to centralize routing, CORS, and JWT enforcement.

## Architecture
- API Gateway (Spring Cloud Gateway) routes client requests and enforces JWT auth and CORS.
- Microservices (each Spring Boot app) expose REST endpoints and use H2 in-memory DB for local development.
- Kafka (docker-compose) provides async eventing between transaction → reward/notification services.

## Services & Local Ports
- API Gateway: `http://localhost:8090`
- User Service: `http://localhost:8081` — `/auth/*`, `/api/users/*`
- Transaction Service: `http://localhost:8082` — `/api/transaction/*`
- Notification Service: `http://localhost:8084` — `/api/notify/*`
- Reward Service: `http://localhost:8085` — `/api/rewards/*`
- Wallet Service: `http://localhost:8088` — `/api/v1/wallets/*`
- Product Service: `http://localhost:8086` — `/products/*`

## Technology Stack
- Java 17
- Spring Boot (3.x), Spring Cloud Gateway, Spring Web / WebFlux
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- Spring Kafka / Apache Kafka
- H2 (in-memory DB for dev)
- Maven (w/ mvnw)
- Docker Compose (Kafka + Zookeeper)

## Authentication (JWT)
- Signup: `POST /auth/signup` — body: `{ email, name, password }` → 201
- Login: `POST /auth/login` — body: `{ email, password }` → returns `{ token: "<jwt>" }`
- Use header `Authorization: Bearer <token>` when calling protected endpoints.

Gateway will validate tokens and append headers `X-User-Email`, `X-User-Id`, `X-User-Role` to downstream requests.

## Important Endpoints (via gateway `http://localhost:8090`)
- User: `POST /auth/signup`, `POST /auth/login`, `POST /api/users`, `GET /api/users/{id}`, `GET /api/users/all`
- Wallet: `POST /api/v1/wallets`, `POST /api/v1/wallets/credit`, `POST /api/v1/wallets/debit`, `GET /api/v1/wallets/{userId}`, `POST /api/v1/wallets/hold`, `POST /api/v1/wallets/capture`, `POST /api/v1/wallets/release/{holdReference}`
- Transaction: `POST /api/transaction/create`, `GET /api/transaction/user/{id}`
- Products: `POST /products/create-product` (multipart), `GET /products/{id}`, `GET /products/all-products`, `GET /products/user-products/{userId}`
- Notifications: `POST /api/notify`, `GET /api/notify/{userId}`
- Rewards: `GET /api/rewards`, `GET /api/rewards/user/{userId}`

## Local Development
Prereqs: Java 17, Maven, Docker (for Kafka), Node (for frontend).

Start Kafka & Zookeeper (from repo root):

```powershell
docker-compose up -d
```

Start services (examples; run as many modules as needed):

```powershell
# from project root
.\mvnw -pl user-service spring-boot:run
.\mvnw -pl transaction-service spring-boot:run
.\mvnw -pl notification-service spring-boot:run
.\mvnw -pl reward-service spring-boot:run
.\mvnw -pl wallet-service spring-boot:run
.\mvnw -pl product-service spring-boot:run
.\mvnw -pl api-gateway-service spring-boot:run
```

Notes:
- Many services use H2 in-memory DB (console available where enabled at `/h2-console`).
- JWT secret is hard-coded for local dev; move to secure config for production.
- Gateway rate limiter filters reference Redis-based settings — Redis is not included by default.

## Sample Requests
- Login (curl):

```bash
curl -X POST http://localhost:8090/auth/login -H "Content-Type: application/json" -d '{"email":"a@b.com","password":"pass"}'
```

- Use token in subsequent request:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8090/api/v1/wallets/123
```

## Developer Notes & Next Steps
- Ensure Kafka is running to test transaction → reward/notification flows.
- Replace the hard-coded JWT secret and consider central config (Spring Cloud Config or environment variables).
- Consider adding Redis (or remove the Redis rate limiter filter) if rate limiting is required in dev.

---
Generated summary and developer-facing docs. For convenience see the gateway configuration at `api-gateway-service/src/main/resources/application.yml` and Kafka setup at `docker-compose.yml`.
