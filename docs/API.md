# Banking App API Reference

## Endpoint Table

| Method | Path                                                       | Description                       | Auth/Role         | Request Body                   | Response Type |
|--------|------------------------------------------------------------|-----------------------------------|-------------------|-------------------------------|--------------|
| POST   | /auth/register                                             | Register a new user               | Public            | CreateUserRequest             | UserResponse  |
| POST   | /auth/login                                                | Login, get JWT token              | Public            | LoginRequest                  | String (JWT)  |
| POST   | /auth/reset-password                                       | Reset password (old req)          | User (self)       | ResetPasswordRequest          | String        |
| GET    | /home                                                      | Welcome message                   | Public            | -                             | String        |

### Users (Admin)

| Method | Path                          | Description                  | Auth/Role     | Request Body         | Response Type |
|--------|-------------------------------|------------------------------|---------------|---------------------|--------------|
| GET    | /admin/users                  | Get all users                | Admin         | -                   | List<User>   |
| GET    | /admin/users/active           | Get all active users         | Admin         | -                   | List<User>   |
| GET    | /admin/users/{publicId}       | Get user by publicId         | Admin         | -                   | User         |
| PUT    | /admin/users/{publicId}       | Update user                  | Admin         | UpdateUserRequest   | User         |
| DELETE | /admin/users/{publicId}       | Delete user                  | Admin         | -                   | void         |
| POST   | /admin/users/{publicId}/promote| Promote user to admin        | Admin         | -                   | User         |

### Account Types (Admin)

| Method | Path                          | Description                  | Auth/Role     | Request Body         | Response Type |
|--------|-------------------------------|------------------------------|---------------|---------------------|--------------|
| POST   | /admin/account-types          | Create account type          | Admin         | AccountType         | AccountType  |
| GET    | /admin/account-types          | Get all account types        | Admin         | -                   | List<AccountType> |
| GET    | /admin/account-types/{id}     | Get account type by id       | Admin         | -                   | AccountType  |
| PUT    | /admin/account-types/{id}     | Update account type          | Admin         | AccountType         | AccountType  |
| DELETE | /admin/account-types/{id}     | Delete account type          | Admin         | -                   | void         |

### Accounts

| Method | Path                                                      | Description                       | Auth/Role         | Request Body                   | Response Type |
|--------|-----------------------------------------------------------|-----------------------------------|-------------------|-------------------------------|--------------|
| POST   | /users/{userPublicId}/accounts/{accountTypePublicIdentifier} | Create account for user & type    | User (self)       | CreateAccountRequest           | Account      |
| GET    | /users/{userPublicId}/accounts                            | Get all accounts for user         | User (self)/Admin | -                             | List<Account>|
| GET    | /users/{userPublicId}/accounts/{accountNumber}            | Get account details               | User (self)/Admin | -                             | Account      |
| DELETE | /users/{userPublicId}/accounts/admin/{accountNumber}      | Close account (soft delete)       | Admin             | -                             | void         |

### Transactions

| Method | Path                                                                       | Description                       | Auth/Role         | Request Body                   | Response Type |
|--------|----------------------------------------------------------------------------|-----------------------------------|-------------------|-------------------------------|--------------|
| POST   | /users/{userPublicId}/accounts/{accountNumber}/transactions/deposit        | Deposit funds                     | User (self)       | TransactionRequestForDeposit   | Transaction  |
| POST   | /users/{userPublicId}/accounts/{accountNumber}/transactions/withdraw       | Withdraw funds                    | User (self)       | TransactionRequestForWithdrawal| Transaction  |
| POST   | /users/{userPublicId}/accounts/{accountNumber}/transactions/transfer       | Transfer to another account       | User (self)       | TransactionRequestForTransfer  | Transaction  |
| GET    | /users/{userPublicId}/accounts/{accountNumber}/transactions                | Get transaction history           | User (self)       | -                             | List<Transaction> |
| POST   | /users/{userPublicId}/accounts/{accountNumber}/transactions/admin/reverse  | Reverse a transaction             | Admin             | ReverseTransactionRequest      | Transaction  |
| POST   | /users/{userPublicId}/accounts/{accountNumber}/transactions/filter         | Filter & paginate transactions    | User (self)       | TransactionHistoryFilterRequest| Page<Transaction> |

---

## Request/Response DTOs

See `/dto/` package in code for details on request and response object fields.

---

## Authentication

- Most endpoints require a JWT token in the `Authorization: Bearer <token>` header.
- Admin endpoints require an account with the `ROLE_ADMIN` role.

---

## Error Handling

- Error responses are standardized as JSON with fields: `timestamp`, `status`, `error`, `message`.
- Common error status codes: `400` (bad request), `401` (unauthorized), `403` (forbidden), `404` (not found), `409` (conflict), `500` (server error).

---

For more details or sample requests, see the full Swagger/OpenAPI docs at `/swagger-ui.html` when the app is running.