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

## Example `application.properties` for Local Dev

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/bankingapp
jwt.secret=supersecretdevkeythatshouldbereplacedinprod123456!
server.port=8080
```

## Tips

- Never commit actual secrets or passwords to Git.
- For production, set secrets using OS environment variables or a secrets manager.
- You can override any property via `-D` JVM args or environment variables.

---

**See also:**
- [Logging/Logback setup](./logback.md)
- [API Reference](./API.md)