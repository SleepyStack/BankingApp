# Application Properties Setup

This file documents all required and optional configuration parameters for running the backend.

## Required Properties (Place in `src/main/resources/application.properties`)

```properties
# MongoDB connection URI
spring.data.mongodb.uri=mongodb://localhost:27017/bankingapp

# JWT Secret (must be at least 32 bytes for HMAC SHA)
jwt.secret=ReplaceThisWithYourSuperSecretKeyForJWT!ChangeMe!

# Server port (optional, default is 8080)
# server.port=8080

# Logging directory (see logback-spring.xml)
# logs will be placed in ./logs/
```

### Where to get/set these values

- **MongoDB URI:**
    - Use your local or cloud MongoDB instance.
    - Format: `mongodb://<user>:<password>@<host>:<port>/<dbname>`
- **JWT Secret:**
    - Must be a secure, random string (at least 32 characters).
    - Do **not** share or commit this to version control!
    - Generate with a command like:
      ```
      openssl rand -base64 32
      ```

- **Server Port (optional):**
    - Change if you want your app to run on a different port.

---

## Actuator & Info Endpoints

Spring Boot Actuator endpoints provide application health and info for monitoring tools.

```properties
# Expose health and info endpoints at /actuator/health and /actuator/info
management.endpoints.web.exposure.include=health,info
management.info.env.enabled=true
# Show detailed health info (database, etc) on /actuator/health
management.endpoint.health.show-details=always

# Metadata for /actuator/info (optional, but recommended)
info.app.name=BankingApp
info.app.description=Banking App Backend Service
info.app.version=0.0.1
```
- `/actuator/health`: Returns health status (public, no auth).
- `/actuator/info`: Returns these metadata values as JSON (public, no auth by default).

---

## Example `application.properties` for Local Dev

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/bankingapp
jwt.secret=supersecretdevkeythatshouldbereplacedinprod123456!
server.port=8080

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

info.app.name=BankingApp
info.app.description=Banking App Backend Service
info.app.version=0.0.1
```

---

## Tips

- Never commit actual secrets or passwords to Git.
- For production, set secrets using OS environment variables or a secrets manager.
- You can override any property via `-D` JVM args or environment variables.
- If `/actuator/info` returns `{}`: Make sure your info properties are in the correct file and restart the app.

---

**See also:**
- [Logging/Logback setup](./logback.md)
- [API Reference](./API.md)