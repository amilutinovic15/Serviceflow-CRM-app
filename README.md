# ServiceFlow

Mini CRM and invoicing system for small service businesses.

## Local setup

Requirements:

- JDK 21
- Docker Desktop

Start MySQL:

```bash
docker compose up -d
```

Run the application:

```bash
./mvnw spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Authentication

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
POST /api/auth/change-password
```

The first registered user is always created as `ADMIN` and registration is public only while there are no users.
After that, only an authenticated `ADMIN` can register new users (with `STAFF` as the default role).

Example registration:

```json
{
  "fullName": "Admin User",
  "email": "admin@serviceflow.local",
  "password": "password123",
  "role": "ADMIN"
}
```

Example login:

```json
{
  "email": "admin@serviceflow.local",
  "password": "password123"
}
```

The login response contains `accessToken`, `tokenType`, `expiresAt` and the current `user`.
Send the token on every `/api/**` request:

```text
Authorization: Bearer <accessToken>
```

Disabled users and role changes take effect immediately, even for tokens that have not expired yet.

Example password change:

```json
{
  "currentPassword": "password123",
  "newPassword": "newPassword123"
}
```

Security configuration:

```text
SERVICEFLOW_JWT_SECRET=<at least 32 characters, required in production>
SERVICEFLOW_JWT_EXPIRATION=PT8H
SERVICEFLOW_CORS_ALLOWED_ORIGINS=http://localhost:5173
```

`SERVICEFLOW_CORS_ALLOWED_ORIGINS` accepts a comma-separated list.

## Errors

All errors use the same JSON shape:

```json
{
  "timestamp": "2026-06-01T10:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Operation conflicts with related or duplicate data",
  "path": "/api/customers/1",
  "fieldErrors": {}
}
```

`400` validation and malformed input, `401` invalid login or missing token, `403` missing permission,
`404` missing resource, `409` related or duplicate data (for example deleting a customer that has jobs or invoices).

Users endpoint:

```text
GET   /api/users?page=0&size=20&sort=createdAt,desc
GET   /api/users?search=admin
GET   /api/users/{id}
PUT   /api/users/{id}
PATCH /api/users/{id}/enabled?enabled=false
PATCH /api/users/{id}/password
```

Only `ADMIN` users can access these endpoints.

Example user update:

```json
{
  "fullName": "Admin User",
  "email": "admin@serviceflow.local",
  "role": "ADMIN",
  "enabled": true
}
```

Example admin password reset:

```json
{
  "newPassword": "newPassword123"
}
```

Service catalog endpoint:

```text
GET    /api/catalog/items?page=0&size=20&sort=name,asc
GET    /api/catalog/items?search=visit&active=true
GET    /api/catalog/items/{id}
POST   /api/catalog/items
PUT    /api/catalog/items/{id}
PATCH  /api/catalog/items/{id}/active?active=false
DELETE /api/catalog/items/{id}
```

Example catalog item:

```json
{
  "name": "Monthly service visit",
  "category": "Maintenance",
  "description": "Regular recurring service work for a customer.",
  "defaultPrice": 7500.00,
  "active": true
}
```

Customers endpoint:

```text
GET    /api/customers?page=0&size=20&sort=createdAt,desc
GET    /api/customers?search=marko
GET    /api/customers/{id}
GET    /api/customers/{id}/timeline
POST   /api/customers
PUT    /api/customers/{id}
DELETE /api/customers/{id}
```

Example request:

```json
{
  "fullName": "Marko Markovic",
  "email": "marko@example.com",
  "phone": "+381641234567",
  "companyName": "Marko Services",
  "address": "Bulevar Oslobodjenja 10, Novi Sad",
  "notes": "Prefers morning appointments."
}
```

Business profile endpoint:

```text
GET /api/settings/business-profile
PUT /api/settings/business-profile
```

Example request:

```json
{
  "businessName": "ServiceFlow Demo Business",
  "legalName": "ServiceFlow Demo Business DOO",
  "taxNumber": "123456789",
  "registrationNumber": "98765432",
  "email": "office@serviceflow.local",
  "phone": "+381641234567",
  "website": "https://serviceflow.local",
  "address": "Bulevar Oslobodjenja 10",
  "city": "Novi Sad",
  "country": "Serbia",
  "currency": "RSD",
  "invoiceFooter": "Thank you for your business."
}
```

Customer assets endpoint:

```text
GET    /api/assets?page=0&size=20&sort=createdAt,desc
GET    /api/assets?customerId={customerId}&search=gold
GET    /api/assets/due-soon?days=30
GET    /api/assets/{id}
POST   /api/assets
PUT    /api/assets/{id}
DELETE /api/assets/{id}
```

Example request:

```json
{
  "customerId": 1,
  "name": "Gold maintenance package",
  "category": "Subscription",
  "assetIdentifier": "GOLD-12M",
  "externalReference": "CONTRACT-2026-001",
  "location": "Novi Sad branch",
  "serviceStartedAt": "2026-01-01",
  "lastServiceAt": "2025-06-01",
  "nextServiceDueAt": "2026-06-15",
  "notes": "Useful for any recurring service, subscription, asset or client-owned item."
}
```

Service jobs endpoint:

```text
GET    /api/jobs?page=0&size=20&sort=scheduledAt,desc
GET    /api/jobs?customerId={customerId}
GET    /api/jobs?status=SCHEDULED
GET    /api/jobs?priority=URGENT
GET    /api/jobs?assigneeId={userId}
GET    /api/jobs/{id}
GET    /api/jobs/{id}/history
GET    /api/jobs/{id}/comments
POST   /api/jobs
POST   /api/jobs/{id}/comments
PUT    /api/jobs/{id}
PATCH  /api/jobs/{id}/status?status=COMPLETED&note=Finished%20successfully
DELETE /api/jobs/{id}/comments/{commentId}
DELETE /api/jobs/{id}
```

Example request:

```json
{
  "customerId": 1,
  "assigneeId": 1,
  "catalogItemId": 1,
  "title": "Monthly service visit",
  "description": "Regular service work for the customer.",
  "status": "SCHEDULED",
  "priority": "NORMAL",
  "scheduledAt": "2026-06-01T09:30:00",
  "price": 7500.00
}
```

Example job comment:

```json
{
  "body": "Customer prefers appointments after 17:00."
}
```

Invoices endpoint:

```text
GET    /api/invoices?page=0&size=20&sort=issuedAt,desc
GET    /api/invoices?customerId={customerId}
GET    /api/invoices?status=PAID
GET    /api/invoices/{id}
GET    /api/invoices/{id}/pdf
POST   /api/invoices/{id}/send-email
POST   /api/invoices
PUT    /api/invoices/{id}
PATCH  /api/invoices/{id}/status?status=PAID
PATCH  /api/invoices/actions/mark-overdue
DELETE /api/invoices/{id}
```

Example request:

```json
{
  "customerId": 1,
  "invoiceNumber": null,
  "status": "SENT",
  "issuedAt": "2026-05-29",
  "dueAt": "2026-06-05",
  "taxRate": 0,
  "notes": "Payment due within 7 days.",
  "items": [
    {
      "serviceJobId": 1,
      "description": "Monthly service visit",
      "quantity": 1,
      "unitPrice": 7500.00
    }
  ]
}
```

If `invoiceNumber` is empty, the system generates the next number in this format:

```text
INV-2026-0001
```

Example invoice email request:

```json
{
  "to": "marko@example.com",
  "subject": "Invoice INV-2026-0001",
  "message": "Hello,\n\nPlease find your invoice attached.\n\nBest regards."
}
```

By default email is in dry-run mode. To send real emails, configure SMTP and set:

```text
SERVICEFLOW_MAIL_ENABLED=true
SERVICEFLOW_MAIL_FROM=office@your-business.com
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-username
SPRING_MAIL_PASSWORD=your-password
```

Invoice payments endpoint:

```text
GET    /api/invoices/{invoiceId}/payments
POST   /api/invoices/{invoiceId}/payments
DELETE /api/invoices/{invoiceId}/payments/{paymentId}
```

Example request:

```json
{
  "amount": 7500.00,
  "paidAt": "2026-06-03",
  "method": "BANK_TRANSFER",
  "reference": "BANK-2026-0001",
  "notes": "Paid in full."
}
```

Dashboard endpoint:

```text
GET /api/dashboard/summary
```

Returns customer count, job counts by status and priority, urgent job count, invoice counts, overdue invoice count, financial totals, current month revenue, upcoming jobs and unpaid invoices.
