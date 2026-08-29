# Splitwise API

[![CI](https://github.com/Shubhank2604/Splitwise/actions/workflows/ci.yml/badge.svg)](https://github.com/Shubhank2604/Splitwise/actions/workflows/ci.yml)

A secure expense-sharing backend that records group and personal expenses, maintains a canonical debt ledger, and settles balances transactionally.

This repository focuses on the engineering details that make money movement trustworthy: authenticated ownership, exact decimal arithmetic, database constraints, migrations, pessimistic locking, rollback safety, and integration tests.

## Why this implementation is interesting

- **The server identifies the actor.** Group creators, expense payers, dashboards, and settlement payers are derived from the JWT subject. A client cannot act as another user by changing an ID in a request.
- **Money is never a floating-point number.** Amounts use `BigDecimal` and database `DECIMAL(19,2)` columns.
- **Splits obey conservation of money.** Each participant is unique, every amount is positive, and split amounts must equal the expense total exactly.
- **Debt is canonical.** Opposing debts are netted into one direction instead of storing contradictory rows.
- **Settlements are bounded.** The API rejects nonexistent debt and overpayment, then records the settlement and ledger update in one transaction.
- **Group access is enforced.** Only the creator can add members, and every participant in a group expense must belong to that group.
- **Schema changes are reproducible.** Flyway owns the schema; Hibernate validates it at startup.

## Expense transaction

```mermaid
flowchart TD
    Request[Expense request] --> Actor[Resolve payer from JWT]
    Actor --> Validate[Validate amount, splits, users, and group membership]
    Validate --> Save[Save expense and splits]
    Save --> PairLock[Lock each user pair in deterministic ID order]
    PairLock --> Net[Net reverse debt or update canonical debt]
    Net --> Commit[(Commit one transaction)]
```

For each non-payer split, the participant becomes the debtor and the authenticated payer becomes the creditor. Expense rows, split rows, and every resulting ledger update share one transaction; a validation or ledger failure rolls back the whole request.

Deterministic user-pair locking serializes both updates to an existing balance and the first balance created for a pair. This prevents two concurrent requests from creating contradictory debt rows.

## Canonical debt example

Suppose Alice pays a `$100.00` dinner split as `$20.00` for Alice and `$80.00` for Bob:

```text
Bob -> Alice: $80.00
```

Bob later pays a `$50.00` expense entirely for Alice. That produces debt in the opposite direction, so the service nets it against the existing row:

```text
Before: Bob -> Alice: $80.00
Apply:  Alice -> Bob: $50.00
After:  Bob -> Alice: $30.00
```

The database stores only the final `$30.00` direction. It does not keep two opposing rows whose net value must be reconstructed later.

## Settlement example

Bob settles `$12.50` with Alice:

```bash
curl -X POST http://localhost:8080/api/settlements \
  -H 'Authorization: Bearer BOBS_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"receiverId":1,"groupId":null,"amount":12.50}'
```

The server derives Bob from the token, locks the Alice/Bob pair, verifies that Bob owes Alice at least `$12.50`, reduces the debt to `$17.50`, and records the settlement in the same transaction. An amount above `$30.00` is rejected; a full `$30.00` settlement deletes the debt row.

## Stack

Java 17, Spring Boot, Spring Security, Spring Data JPA, MySQL 8, Flyway, JJWT, JUnit 5, AssertJ, H2, Docker Compose, and GitHub Actions.

## Run locally

Requirements: Java 17+, Maven 3.9+, and Docker.

```bash
docker compose up -d
export JWT_SECRET="replace-this-with-at-least-32-random-characters"
mvn spring-boot:run
```

The default local database values match `compose.yml`. For a different database, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; see `.env.example`.

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## API walkthrough

Register and log in:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","email":"alice@example.com","password":"strong-password"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"strong-password"}'
```

Use the returned token on protected requests:

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "description": "Dinner",
    "amount": 100.00,
    "groupId": null,
    "splits": [
      {"userId": 1, "amount": 20.00},
      {"userId": 2, "amount": 80.00}
    ]
  }'
```

The authenticated user is the payer. There is deliberately no `paidByUserId` field.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/auth/register` | Create an account |
| `POST` | `/api/auth/login` | Obtain a JWT |
| `GET` | `/api/users/me` | Read the authenticated profile |
| `GET` | `/api/users/me/balance` | Read the authenticated user's ledger |
| `POST` | `/api/groups` | Create a group as the authenticated user |
| `POST` | `/api/groups/{id}/members` | Add members as the group creator |
| `POST` | `/api/expenses` | Record and split an expense |
| `POST` | `/api/settlements` | Settle the authenticated user's debt |
| `GET` | `/api/dashboard` | Read personal and per-group net balances |

Positive balances mean another user owes you; negative balances mean you owe them.

## Verification

```bash
mvn verify
```

The test suite covers authentication boundaries, password-hash response safety, expense invariants, group authorization, opposing-debt netting, settlement overpayment, full settlement, and JWT configuration. CI runs the same command for every pull request and every push to `master`.

## Security notes

- Supply `JWT_SECRET` from a secret manager in production; startup rejects secrets shorter than 32 bytes.
- JWT failures return `401` rather than leaking parser errors or becoming server errors.
- Passwords are BCrypt hashes and are never included in response DTOs.
- All protected operations use the authenticated principal as their ownership boundary.
- This project is an educational implementation, not a custodian of real funds.
