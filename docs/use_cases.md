# Example Use Cases

Below are step-by-step examples of how users and admins interact with the system.

---

## 🧑‍💼 User Flows

### 1. Register & Login

**Register a new user**
- `POST /auth/register`
- Example body:
  ```json
  {
    "name": "Chirag",
    "email": "chirag@example.com",
    "phone": "1234567890",
    "password": "securePassword123"
  }
  ```

**Login and receive a JWT**
- `POST /auth/login`
- Example body:
  ```json
  {
    "email": "chirag@example.com",
    "password": "securePassword123"
  }
  ```
- Use the returned JWT as `Authorization: Bearer <token>` for all protected endpoints.

---

### 2. Account Management

**Create an account**
- `POST /users/{userPublicId}/accounts/{accountTypePublicIdentifier}`
- Example body:
  ```json
  {
    "initialBalance": 750.0
  }
  ```

**View all accounts**
- `GET /users/{userPublicId}/accounts`

---

### 3. Transactions

**Deposit funds**
- `POST /users/{userPublicId}/accounts/{accountNumber}/transactions/deposit`
- Example body:
  ```json
  {
    "amount": 200.0,
    "description": "First deposit"
  }
  ```

**Withdraw funds**
- `POST /users/{userPublicId}/accounts/{accountNumber}/transactions/withdraw`
- Example body:
  ```json
  {
    "amount": 50.0,
    "description": "Snacks"
  }
  ```

**Transfer funds**
- `POST /users/{userPublicId}/accounts/{accountNumber}/transactions/transfer`
- Example body:
  ```json
  {
    "amount": 100.0,
    "targetAccountNumber": "9876543210",
    "description": "Paying a friend"
  }
  ```

**View account transaction history**
- `GET /users/{userPublicId}/accounts/{accountNumber}/transactions`

---

## 🛡️ Admin Flows

### 1. Promote a user to admin

- `POST /admin/users/{publicId}/promote`
- (Requires admin JWT)

### 2. Delete a user

- `DELETE /admin/users/{publicId}`
- (Requires admin JWT)

### 3. Create a new account type

- `POST /admin/account-types`
- Example body:
  ```json
  {
    "typeName": "Premium Savings",
    "publicIdentifier": "PS",
    "description": "Premium savings for select users"
  }
  ```

### 4. Reverse a transaction

- `POST /users/{userPublicId}/accounts/{accountNumber}/transactions/admin/reverse`
- Example body:
  ```json
  {
    "transactionId": "txnIdHere",
    "reason": "Mistaken transaction"
  }
  ```

---

## Notes

- All requests (except register/login/home) require a valid JWT in the `Authorization` header.
- Admin endpoints require a user with the `ROLE_ADMIN` role.
- For the full list of endpoints, see [API.md](./API.md).