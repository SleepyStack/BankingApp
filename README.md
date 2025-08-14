# Banking App Backend

A secure, modern banking application backend built using Spring Boot and MongoDB.

## Features Implemented

- **User Registration & Authentication**
    - Secure JWT authentication
    - Argon2 password hashing
    - Unique email/phone validation

- **User Management**
    - Create, Read, Update, Delete users
    - User status enforcement (active, locked, deactivated, etc.)
    - Password change endpoint (old password required)
    - User activity logging (last login, failed attempts)

- **Account Management**
    - Create accounts for users with selectable account types
    - Unique account number generation
    - Soft account closure (status flag)

- **Account Types**
    - CRUD operations for account types (e.g., Savings, Checking)

- **Transactions**
    - Deposit, withdrawal, and transfer between accounts
    - Transaction history per account
    - Transaction reversal (admin-only)
    - Pagination and filtering for transaction history

- **Security**
    - Role-based access (basic; more coming soon)
    - Global exception handling

## Tech Stack

- Java 24, Spring Boot 3.5
- MongoDB for data storage
- Spring Security for authentication
- JWT for stateless sessions
- Lombok for model boilerplate

## Progress & Roadmap

This project is still in active development. More features (admin tools, advanced security, testing, frontend) are planned.

For a list of remaining tasks and upcoming features, see [NOTES.txt](docs/NOTES.txt).

---

Looking for contributors (especially frontend devs).  
Feel free to open issues, PRs, or reach out!