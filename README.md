# Customer Rewards API

## Overview

This project is a Spring Boot REST API that calculates reward points earned by customers based on their purchase transactions.

A customer receives:

* 2 points for every dollar spent over $100
* 1 point for every dollar spent between $50 and $100

Example:

* Purchase Amount: $120
* Reward Points: 50 + (20 × 2) = 90 points

The application calculates monthly reward points as well as total reward points for each customer.

---

## Technologies Used

* Java 17
* Spring Boot
* Spring Data JPA
* H2 Database
* Maven
* JUnit 5
* Mockito

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.example.rewards
│   │       ├── controller
│   │       ├── DTO
│   │       ├── entity
│   │       ├── exception
│   │       ├── repository
│   │       ├── service
│   │           └── ServiceImpl
│   │     
│   └── resources
│       ├── application.properties
│       └── sampleData.sql
│
└── test
    └── java
        ├── controller
        └── service
```

---

## Implementation Details

### Reward Calculation Logic

| Transaction Amount | Reward Points             |
| ------------------ | ------------------------- |
| ≤ $50              | 0                         |
| $51 - $100         | Amount - 50               |
| > $100             | 50 + ((Amount - 100) × 2) |

### Example Calculations

| Amount | Points |
| ------ | ------ |
| $45    | 0      |
| $75    | 25     |
| $120   | 90     |
| $220   | 290    |

### Monthly Rewards

The month is derived dynamically from the transaction date. No months are hardcoded in the implementation.

---

## API Endpoint

### Get Customer Rewards

```http
GET /transaction/rewards
```

### Sample Response

```json
[
  {
    "customerId": 1,
    "customerName": "John",
    "monthlyRewards": {
      "MARCH": 90,
      "APRIL": 25,
      "MAY": 290
    },
    "totalRewards": 405
  }
]
```

---

## Exception Handling

The application includes global exception handling using `@RestControllerAdvice`.

Handled scenarios:

* No transactions found
* Negative transaction amount
* Unexpected server errors

---

## Testing

### Unit Tests

Implemented using JUnit 5 and Mockito.

Covered scenarios:

- No transactions found
- Amount below $50
- Amount equal to $50
- Amount between $50 and $100
- Amount equal to $100
- Amount greater than $100
- Multiple transactions for the same customer
- Monthly reward aggregation
- Multiple transactions within the same month
- Multiple customer reward calculation
- Negative transaction amount validation
- Missing customer ID validation
- Missing transaction date validation

### Integration Tests

Implemented using Spring Boot Test and MockMvc.

Covered scenarios:

* GET /transaction/rewards returns HTTP 200
* Response structure validation

---

## Running the Application

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

---

## Database

The application uses an in-memory H2 database.

Sample transaction data is loaded automatically using `data.sql` during application startup.

H2 Console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:rewardsTestDB
```

Username:

```text
sa
```

Password:

```text
<empty>
```
