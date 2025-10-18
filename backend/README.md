# Mini Casino Backend

Spring Boot REST API for a mini casino application.

## Quick Start

### Prerequisites
- Java 21
- Maven 3.9+

### Run the Application

```bash
# Build and run
mvn clean install
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### API Documentation

**Swagger UI**: http://localhost:8080/swagger-ui/index.html
**OpenAPI JSON**: http://localhost:8080/api-docs

## API Endpoints

### Player Management
- `POST /api/players/register` - Register a new player
- `GET /api/players/{id}` - Get player details
- `POST /api/players/deposit` - Deposit money

### Games
- `GET /api/games` - List all games
- `GET /api/games/{id}` - Get specific game

### Betting
- `POST /api/bets/place` - Place a bet

## Tech Stack

- Spring Boot 3.3.5
- Java 21
- Maven
- SpringDoc OpenAPI (Swagger)
- In-memory storage (ConcurrentHashMap)
