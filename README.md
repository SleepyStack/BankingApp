# SleepyStack Banking App Backend

A modern, secure banking backend for managing users, accounts, and transactions.  
Built with **Spring Boot** and **MongoDB**.

---

## 🚀 Features

- **User Registration & Login**
    - Secure JWT-based authentication
    - Argon2 password hashing
    - Account lockout after repeated failed logins

- **User Management**
    - Create/update/delete users (admin)
    - Promote users to admin (admin)
    - Password reset with old password verification
    - User status & login attempt tracking

- **Account Types**
    - Flexible account type CRUD (admin)
    - 2-character public identifiers (e.g., "SV" for Savings)

- **Account Operations**
    - Create/check/view/close accounts
    - Unique, random account numbers
    - Soft-close by status (admin)

- **Transactions**
    - Deposit, withdraw, and transfer funds
    - Transaction history per account
    - Advanced transaction filtering (type, date, amount, etc.)
    - Transaction reversal (admin-only)

- **Security & Auditing**
    - Role-based access control
    - Global exception handling and error codes
    - Rate limiting for abuse prevention
    - Admin actions audit-logged to dedicated file

- **Other**
    - API docs via Swagger/OpenAPI (`/swagger-ui/index.html`)
    - Modular, extensible codebase

---

## 📚 Documentation

- [API Reference](docs/API.md)
- [Example Use Cases](docs/use_cases.md)
- [Application Properties Setup](docs/application_properties.md)
- [Logging & Logback](docs/logback.md)
- [Project Roadmap & TODOs](docs/NOTES.txt)

---

## 🛠️ Quickstart

1. **Clone the repo**
   ```sh
   git clone https://github.com/SleepyStack/BankingApp.git
   cd BankingApp
   ```

2. **Configure your environment**
    - Copy and edit `src/main/resources/application.properties`
    - See [docs/application_properties.md](docs/application_properties.md) for details

3. **Start MongoDB** (local or Atlas)

4. **Build & run**
   ```sh
   ./mvnw spring-boot:run
   ```

5. **API Docs**
    - Visit: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---
## 🔍 Health Check

- The application exposes a health check endpoint at [`/actuator/health`](http://localhost:8080/actuator/health).
- Useful for Kubernetes, Docker, AWS, etc. monitoring.

## 🤝 Contributing

- Issues and PRs welcome!
- For upcoming features and roadmap, see [docs/NOTES.txt](docs/NOTES.txt)
- Want to help with the frontend? Reach out!

---

## 📝 License

[MIT](https://opensource.org/licenses/MIT) © SleepyStack (Chirag)
---

Made with ☕ by [SleepyStack](https://github.com/SleepyStack)