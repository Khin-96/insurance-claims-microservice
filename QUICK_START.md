# Quick Start Guide

## Prerequisites

- Java 21+
- Docker Desktop running
- 8GB RAM available

## Start the Application

```bash
# 1. Start infrastructure (PostgreSQL, Redis, Kafka)
docker-compose up -d

# 2. Wait 30 seconds for services to initialize

# 3. Run the application
./gradlew bootRun

# Application starts on http://localhost:8080
```

## Access Points

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Base:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/actuator/health

## Test the API

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "CUSTOMER"
  }'
```

### 2. Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

Save the token from the response.

### 3. Submit a Claim

```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL-2024-001",
    "claimType": "MEDICAL",
    "amount": 50000.00,
    "description": "Hospital treatment",
    "idempotencyKey": "unique-key-123"
  }'
```

### 4. Get Claim Details

```bash
curl -X GET http://localhost:8080/api/claims/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Approve Claim (Admin Only)

```bash
# Login as admin first
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Then approve
curl -X PUT http://localhost:8080/api/claims/1/approve \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Default Users

The database is pre-populated with these users:

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| agent | admin123 | AGENT |
| customer | admin123 | CUSTOMER |

## Stop Everything

```bash
# Stop application: Ctrl+C

# Stop infrastructure
docker-compose down

# Remove volumes (clean slate)
docker-compose down -v
```

## Troubleshooting

### Application won't start

Check if ports are available:
```bash
netstat -ano | findstr :8080
netstat -ano | findstr :5432
netstat -ano | findstr :6379
netstat -ano | findstr :9092
```

### Database connection error

Ensure PostgreSQL is running:
```bash
docker-compose ps
```

### Redis connection error

Check Redis:
```bash
docker exec -it claims-redis redis-cli PING
```

## Next Steps

1. Explore the Swagger UI for all endpoints
2. Test idempotency by submitting the same claim twice
3. Check Kafka topics for events
4. View audit logs in the database
5. Test role-based access control

## Documentation

- [README.md](./README.md) - Project overview
- [API Documentation](./docs/API.md) - Complete API reference
- [Postman Collection](./postman/claims-api.postman_collection.json)
