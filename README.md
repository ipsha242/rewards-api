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

* **Java 17**
* **Spring Boot 3.3.6**
* **Spring Data JPA**
* **H2 Database** (In-Memory)
* **Maven** (Build Management)
* **JUnit 5 & Mockito** (Unit/Integration Testing)
* **JaCoCo 0.8.14** (Code Coverage Tracking Engine)
* **Lombok** (Boilerplate reduction)

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.example.rewards
│   │       ├── controller         # REST Controller Endpoints
│   │       ├── dto                # Data Transfer Objects (RewardDTO)
│   │       ├── entity             # Database Entities (Customer, Transaction)
│   │       ├── exception          # Custom Exception Definitions & RestAdvice
│   │       ├── repository         # Data Access Layers (RewardRepository)
│   │       └── service            # Business Logic Specifications
│   │           └── serviceImpl    # Core Calculation Engines
│   │     
│   └── resources
│       ├── application.properties # System Properties Configuration
│       └── sampleData.sql               # Automated Database Seed Records
│
└── test
    └── java
        └── com.example.rewards
            ├── controller         # MockMvc Endpoint Unit & Integration Tests
            └── service            # Aggregation Engine Boundary Logic Tests
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

## Monthly Reward Aggregation

Monthly rewards are grouped using `YearMonth` derived from the transaction date.

Example:

```json
{
  "monthlyRewards": {
    "2026-03": 90.00,
    "2026-04": 25.00,
    "2026-05": 290.00
  }
}
```

This ensures transactions from different years are not combined into the same month bucket.

---

## API Endpoints

### 1. Get Rewards For All Customers (Last 3 Months)

```http
GET /transaction/rewards
```

Returns monthly and total rewards for all customers based on transactions from the last three months.

#### Sample Response

```json
[
  {
    "customerId": 1,
    "customerName": "John",
    "monthlyRewards": {
      "2026-03": 90.00,
      "2026-04": 25.00,
      "2026-05": 290.00
    },
    "totalRewards": 405.00
  }
]
```

---

### 2. Get Rewards For All Customers Within a Date Range

```http
GET /transaction/rewards/range
```

#### Query Parameters

| Parameter | Required |
| --------- | -------- |
| startDate | No       |
| endDate   | No       |

#### Examples

All transactions:

```http
GET /transaction/rewards/range
```

Specific date range:

```http
GET /transaction/rewards/range?startDate=2026-01-01&endDate=2026-12-31
```

---

### 3. Get Rewards For a Specific Customer

```http
GET /transaction/rewards/{customerId}
```

#### Examples

All rewards for customer:

```http
GET /transaction/rewards/1
```

Customer rewards within a date range:

```http
GET /transaction/rewards/1?startDate=2026-01-01&endDate=2026-12-31
```

#### Sample Response

```json
{
  "customerId": 1,
  "customerName": "John",
  "monthlyRewards": {
    "2026-03": 90.00,
    "2026-04": 25.00,
    "2026-05": 290.00
  },
  "totalRewards": 405.00
}
```

---

## Validation & Exception Handling

The application includes global exception handling using `@RestControllerAdvice`.

Handled scenarios:

- No transactions found
- Customer not found
- Missing customer information
- Missing transaction date
- Negative transaction amount
- Invalid date range
- Missing start date or end date
- Unexpected server errors
---

### Unit Tests

Implemented using JUnit 5 and Mockito.

Covered scenarios:

- Reward calculation boundaries
- Monthly reward aggregation
- Multiple transactions for a customer
- Multiple customers
- Customer reward retrieval
- Date range filtering
- Missing customer validation
- Missing transaction date validation
- Negative amount validation
- Invalid date range validation
- Customer not found scenarios

### Integration Tests

Implemented using Spring Boot Test and MockMvc.

Covered scenarios:

- GET /transaction/rewards
- GET /transaction/rewards/range
- GET /transaction/rewards/{customerId}
- Response validation
- End-to-end reward calculation verification

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

Sample transaction data is loaded automatically using `sampleData.sql` during application startup.

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

---

## Code Coverage

Code coverage is generated using JaCoCo.

Generate report:

```bash
.\mvnw clean test
```

Coverage report:

```text
target/site/jacoco/index.html
```
