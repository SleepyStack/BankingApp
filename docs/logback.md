# Logging & Logback Setup

This project uses [Logback](https://logback.qos.ch/) for logging. Logback is configured via `src/main/resources/logback-spring.xml`.

---

## Log Files & Locations

All log files are created inside the `logs/` directory (relative to your project root):

- **logs/bankingapp.log**  
  Main application log (INFO and above)
- **logs/bankingapp-error.log**  
  Warnings and errors only (WARN and above)
- **logs/adminAuditLogger.log** (if you add a separate admin audit appender; currently admin audit logs go to the main log)

Each log file is automatically rotated daily and/or when it reaches 10MB in size. Up to 30 days are kept, with a 2GB total cap per log.

---

## Log Levels

- **Default root logger:** `INFO`
- **Project package (`com.sleepystack.bankingapp`):** `DEBUG` (see bottom of logback config for this override)

You can adjust these in `logback-spring.xml` if you want more/less verbosity.

---

## Console Logging

Logs are also output to the console by default.

---

## Changing Log Level

To change log verbosity, edit the `<logger>` or `<root>` `level` attribute in `logback-spring.xml`, for example:

```xml
<logger name="com.sleepystack.bankingapp" level="INFO"/>
<root level="WARN">
    ...
</root>
```

---

## Typical Usage

- Check `logs/bankingapp.log` for general application activity, debugging, and all user/admin actions.
- Check `logs/bankingapp-error.log` for warnings and errors.
- If you want a dedicated admin audit log file, you can add a new appender/logger in `logback-spring.xml` for `adminAuditLogger`.

---

## Sample Logback Configuration

See [`src/main/resources/logback-spring.xml`](../src/main/resources/logback-spring.xml) for the full configuration in this project.

---

**Tip:**  
You can tail logs in real time with:
```bash
tail -f logs/bankingapp.log
```