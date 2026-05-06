# Splitwise Backend

## What it does

This project is a Spring Boot backend for a Splitwise-style expense sharing web application. It provides RESTful APIs that let users register, log in, create groups, add members, record shared expenses, split expenses between users, track balances, and settle debts.

The backend uses JWT-based authentication for protected endpoints and BCrypt password hashing for storing user passwords securely. Data is persisted in a MySQL database through Spring Data JPA and Hibernate.

Core capabilities include:

- User registration and login
- JWT-protected API access
- Group creation and group membership management
- Expense creation with custom split amounts
- Personal and group balance tracking
- Settle-up transaction recording
- Dashboard summaries for personal and group balances

## Project Structure

```text
backend/
  pom.xml
  src/
    main/
      java/
        com/splitwise/
          SplitwiseApplication.java
          controller/
            AuthController.java
            DashboardController.java
            ExpenseController.java
            GroupController.java
            UserController.java
          dto/
            AddGroupMembersRequest.java
            AuthResponse.java
            CreateExpenseRequest.java
            CreateGroupRequest.java
            DashboardResponseDTO.java
            ExpenseDTO.java
            GroupBalanceDTO.java
            LoginRequest.java
            PersonalBalanceDTO.java
            RegisterRequest.java
            UserBalanceDTO.java
            UserBalanceResponse.java
          entity/
            Expense.java
            Group.java
            GroupMember.java
            Split.java
            Transaction.java
            User.java
            UserBalance.java
          repository/
            ExpenseRepository.java
            GroupMemberRepository.java
            GroupRepository.java
            SplitRepository.java
            TransactionRepository.java
            UserBalanceRepository.java
            UserRepository.java
          security/
            JwtFilter.java
            JwtUtil.java
            SecurityConfig.java
          service/
            DashboardService.java
            ExpenseService.java
            GroupService.java
            TransactionService.java
            UserBalanceService.java
            UserService.java
      resources/
        application.properties
    test/
      java/
        com/splitwise/
          SplitwiseApplicationTests.java
          service/
            UserServiceTest.java
```

Important packages:

- `controller`: REST API endpoints.
- `dto`: Request and response objects used by the APIs.
- `entity`: JPA entities mapped to relational database tables.
- `repository`: Spring Data JPA repositories for database access.
- `security`: JWT authentication and Spring Security configuration.
- `service`: Business logic for users, groups, expenses, balances, dashboard data, and transactions.

## Architecture/Flow

The backend follows a standard layered Spring Boot architecture:

```text
Client
  -> REST Controller
  -> Service Layer
  -> Repository Layer
  -> MySQL Database
```

Authentication flow:

1. A user registers through `POST /auth/register`.
2. The password is hashed with BCrypt before being stored.
3. The user logs in through `POST /auth/login`.
4. Spring Security validates the username and password.
5. A JWT is generated and returned to the client.
6. Protected requests include the token in the `Authorization: Bearer <token>` header.
7. `JwtFilter` validates the token and sets the authenticated user in the security context.

Expense flow:

1. A client sends an expense request to `POST /expenses`.
2. `ExpenseController` passes the request to `ExpenseService`.
3. `ExpenseService` loads the payer and validates group membership when the expense belongs to a group.
4. An `Expense` record is created with related `Split` records.
5. `UserBalanceService` updates the amount owed from each split user to the payer.
6. Data is persisted using JPA repositories.

Balance flow:

1. `GET /users/{userId}/balance` returns a user's net balances with other users.
2. `GET /dashboard?userId={userId}` returns personal totals and group-level balance summaries.
3. `UserBalanceRepository` provides aggregate queries for amounts owed by and owed to a user.

Settle-up flow:

1. A client calls `POST /users/{payerId}/settle-up/{receiverId}` with an amount.
2. `TransactionService` creates a `Transaction` record.
3. `UserBalanceService` reduces or removes the corresponding outstanding balance.

Main database tables:

- `users`
- `user_groups`
- `group_members`
- `expenses`
- `splits`
- `user_balances`
- `transactions`

## Run the project

Prerequisites:

- Java 17
- Maven
- MySQL

Create a MySQL database and user matching `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/splitwise_db
spring.datasource.username=splitwise_user
spring.datasource.password=mypassword
```

From the project root, run:

```bash
cd backend
mvn spring-boot:run
```

The application starts as a Spring Boot service, typically on:

```text
http://localhost:8080
```

Run tests with:

```bash
cd backend
mvn test
```

Current test classes are present, but they are disabled with `@Disabled`.
