# Online Bonafide Certificate Portal

A Java Spring Boot web app that lets college students request bonafide certificates online, and lets registrar staff (admin) review, approve/reject requests, with signed PDF certificate generation on approval.

## Features

- **Student**: register/login, submit a certificate request (purpose + details), track status, download PDF once approved.
- **Admin**: view all requests, approve or reject with optional remarks.
- **PDF generation**: approved requests generate a formatted bonafide certificate PDF (iText).
- Session-based login (no external auth service needed), H2 in-memory database (no DB setup required).

## Tech stack

- Java 17, Spring Boot 3 (Web, Thymeleaf, Spring Data JPA, Validation)
- H2 in-memory database
- iText for PDF generation
- Maven

## Running locally

Requires **Java 17+** and **Maven** installed.

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** in your browser.

### Demo accounts (seeded automatically on first run)

| Role    | Email               | Password   |
|---------|----------------------|------------|
| Admin   | admin@college.edu    | admin123   |
| Student | student@college.edu  | student123 |

New students can also self-register from the login page.

## Project structure

```
src/main/java/com/college/bonafide/
  BonafideApplication.java      # entry point + demo data seeding
  model/                        # Student, BonafideRequest, Role, RequestStatus
  repository/                   # Spring Data JPA repositories
  controller/                   # AuthController, StudentController, AdminController
  service/                      # PdfCertificateService (iText)
src/main/resources/
  application.properties
  templates/                    # Thymeleaf HTML pages
  static/css/style.css
```

## Notes

- Data is stored in-memory (H2) and resets each time the app restarts. To persist data, swap the datasource in `application.properties` for MySQL/PostgreSQL.
- Passwords are stored in plain text for simplicity — **do not use this as-is in production**. Add Spring Security + password hashing (BCrypt) before deploying for real use.
- The college name/address on the certificate is a placeholder — edit `PdfCertificateService.java` to customize.

## Pushing to GitHub

```bash
git init
git add .
git commit -m "Initial commit: bonafide certificate portal"
git branch -M main
git remote add origin https://github.com/arunkadam827-prog/onlinebonaFIDE.git
git push -u origin main
```
