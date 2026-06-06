# 🏦 Banking & Finance Management System

A production-grade Banking REST API built with Java Spring Boot, featuring JWT authentication, role-based access control, and atomic financial transactions.

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4, Java 25 |
| Security | Spring Security 7, JWT (jjwt 0.12.3) |
| Database | MySQL 8, Spring Data JPA, Hibernate 7 |
| Documentation | Swagger UI (OpenAPI 3.0) |
| Build Tool | Maven |

## ✨ Features

- ✅ JWT Authentication (Register + Login)
- ✅ Role-based Access Control (CUSTOMER / ADMIN)
- ✅ Bank Account Management (Open / View / Close)
- ✅ Deposit, Withdrawal & Transfer with `@Transactional`
- ✅ Insufficient funds validation
- ✅ Paginated Transaction History
- ✅ UUID-based transaction reference IDs
- ✅ Admin Dashboard (stats, user management, freeze accounts)
- ✅ Global Exception Handling
- ✅ Swagger UI API Documentation

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login + get JWT token | Public |

### Accounts
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/accounts/open` | Open new bank account | CUSTOMER |
| GET | `/api/accounts/my` | Get my accounts | CUSTOMER |
| GET | `/api/accounts/{accountNumber}` | Get account details | CUSTOMER |
| PUT | `/api/accounts/{id}/close` | Close account | CUSTOMER |

### Transactions
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/transactions/deposit` | Deposit money | CUSTOMER |
| POST | `/api/transactions/withdraw` | Withdraw money | CUSTOMER |
| POST | `/api/transactions/transfer` | Transfer between accounts | CUSTOMER |
| GET | `/api/transactions/{accountNumber}/history` | Paginated history | CUSTOMER |

### Admin
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/dashboard` | Stats overview | ADMIN |
| GET | `/api/admin/users` | All users paginated | ADMIN |
| GET | `/api/admin/users/{id}` | User + their accounts | ADMIN |
| PUT | `/api/admin/users/{id}/toggle-status` | Freeze/activate user | ADMIN |
| GET | `/api/admin/transactions` | All transactions | ADMIN |

## 🗄️ Database Schema
users
├── id, full_name, email, password (BCrypt)
├── phone_number, address
├── role (CUSTOMER/ADMIN), status (ACTIVE/FROZEN/CLOSED)
└── created_at, updated_at
accounts
├── id, user_id (FK), account_number (unique)
├── account_type (SAVINGS/CURRENT)
├── balance (DECIMAL 15,2), interest_rate
├── status (ACTIVE/FROZEN/CLOSED)
└── created_at
transactions
├── id, from_account_id (FK), to_account_id (FK)
├── type (DEPOSIT/WITHDRAWAL/TRANSFER/INTEREST)
├── amount (DECIMAL 15,2), status (PENDING/SUCCESS/FAILED)
├── reference_id (UUID), description
└── created_at
## 🚀 Running Locally

### Prerequisites
- Java 17+
- Maven
- MySQL 8 (via XAMPP)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/agarwalkeshar/banking-finance-management-system.git
cd banking-finance-management-system
```

**2. Create the database**
```sql
CREATE DATABASE banking_db;
```

**3. Configure application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
jwt.secret=YOUR_256_BIT_SECRET
jwt.expiration=86400000
```

**4. Run the application**
```bash
mvn spring-boot:run
```
**5. Access Swagger UI**
http://localhost:8080/swagger-ui/index.html
## 🧪 Testing the API

**Register a user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"John Doe","email":"john@gmail.com","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@gmail.com","password":"password123"}'
```

**Deposit money:**
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"accountNumber":"ACC1234567890","amount":5000}'
```

## 👨‍💻 Author

**Keshar Agrawal** — Final year Computer Science student  
GitHub: [@agarwalkeshar](https://github.com/agarwalkeshar)
