# AI Integration Server (Java)

A Spring Boot microservice that integrates with OpenAI's GPT-4 Mini to provide AI-powered chat functionality for the Campus Marketplace application.

## Overview

This service provides intelligent assistance to students using the campus marketplace by:
- Answering questions about available products
- Providing product recommendations
- Comparing prices across items
- Analyzing reported listings
- Offering marketplace insights

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **OpenAI Java SDK 0.18.2**
- **Maven**
- **Lombok**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- OpenAI API Key

## Configuration

1. Copy the example environment file:
```bash
cp .env.example .env
```

2. Add your OpenAI API key to the `.env` file:
```
OPENAI_API_KEY=your-api-key-here
```

## Building the Application

```bash
./mvnw clean install
```

## Running the Application

### Using Maven
```bash
./mvnw spring-boot:run
```

### Using Java
```bash
java -jar target/ai-integration-server-0.0.1-SNAPSHOT.jar
```

The server will start on port **3001** by default.

## API Endpoints

### POST /api/chat
Process user messages and return AI-generated responses.

**Request Body:**
```json
{
  "messages": [
    { "role": "user", "content": "What laptops are available?" },
    { "role": "assistant", "content": "I found 3 laptops..." }
  ],
  "listingsContext": "Current marketplace listings...",
  "reportsContext": "Reported listings data..."
}
```

**Response:**
```json
{
  "message": "AI-generated response text"
}
```

### GET /api/health
Health check endpoint.

**Response:**
```json
{
  "status": "ok",
  "hasApiKey": true
}
```

## Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `OPENAI_API_KEY` | OpenAI API authentication key | Yes | - |
| `server.port` | Server port | No | 3001 |

## Project Structure

```
ai-integration-server/
├── src/
│   └── main/
│       ├── java/com/commandlinecommandos/aiintegration/
│       │   ├── AiIntegrationServerApplication.java
│       │   ├── config/
│       │   │   └── OpenAIConfig.java
│       │   ├── controller/
│       │   │   └── AIController.java
│       │   ├── service/
│       │   │   └── AIService.java
│       │   └── dto/
│       │       ├── ChatMessage.java
│       │       ├── ChatRequest.java
│       │       ├── ChatResponse.java
│       │       ├── HealthResponse.java
│       │       └── ErrorResponse.java
│       └── resources/
│           └── application.properties
├── pom.xml
├── .env.example
└── README.md
```

## Integration with Frontend

The frontend component at `frontend/src/components/AskAIPage.tsx` communicates with this service via the `/api/chat` endpoint. It sends:
- Conversation history
- Current marketplace listings context
- Reported listings context

## Error Handling

The service handles the following error scenarios:
- Missing OpenAI API key (returns 500 with configuration message)
- OpenAI API failures (returns 500 with error details)
- Invalid request data (returns appropriate error response)

## Logging

Logs are configured at DEBUG level for the application package and INFO for root. Check the console output for request/response details and errors.

## Development

### Hot Reload
Spring Boot DevTools is included for automatic restarts during development.

### Testing
Run tests with:
```bash
./mvnw test
```

## Production Deployment

1. Build the JAR file:
```bash
./mvnw clean package
```

2. Run with production profile:
```bash
java -jar target/ai-integration-server-0.0.1-SNAPSHOT.jar
```

3. Ensure `OPENAI_API_KEY` is set in your environment variables.

## Docker Support

To add Docker support, create a `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/ai-integration-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t ai-integration-server .
docker run -p 3001:3001 -e OPENAI_API_KEY=your-key ai-integration-server
```

## License

Part of the Campus Marketplace project by Command Line Commandos.
