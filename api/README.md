# StockHome — API

REST API for **StockHome** (home pantry stock control), the backend for the Android
app (`../app`). It tracks pantry items (name, quantity, expiry date), computes a status
and alerts when something is **running low** or **about to expire**.

It mirrors the app's data model (`app/.../data/MockData.kt`): same categories, same 15
sample products and the **same status logic**.

> Note: user-facing strings (error messages, status labels, category and product names)
> are kept in pt-BR on purpose, since the app's UI is in Brazilian Portuguese. All code,
> identifiers, JSON field names and docs are in English.

## Stack

- **Node.js + TypeScript** (ESM)
- **Express** — HTTP server
- **PostgreSQL** running in **Docker**
- **Prisma ORM** — schema, migrations and typed client
- **JWT + bcrypt** — authentication
- **Zod** — input validation

## Requirements

- Node.js 20+
- Docker (Docker Desktop running)

## Getting started

```bash
cd api
cp .env.example .env          # a ready-to-use dev .env is already provided
npm install

# 1. start Postgres in Docker
npm run db:up

# 2. create the tables (migration)
npm run prisma:migrate        # use the name "init" on the first run

# 3. seed categories + demo user + 15 products
npm run db:seed

# 4. start the API in dev mode (hot-reload)
npm run dev
```

> Shortcut: `npm run setup` runs steps 1–3 in one go.

The API listens on `http://localhost:3000`. Healthcheck: `GET /health`.

### Demo user (from the seed)

| Email | Password |
| --- | --- |
| `marina.alves@email.com` | `123456` |

## Scripts

| Script | What it does |
| --- | --- |
| `npm run dev` | API in dev mode with hot-reload (tsx watch) |
| `npm run build` / `npm start` | Compile to `dist/` and run for production |
| `npm run db:up` / `npm run db:down` | Start / stop Postgres in Docker |
| `npm run prisma:migrate` | Create and apply a migration (dev) |
| `npm run db:seed` | Seed the database with sample data |
| `npm run db:reset` | Recreate the database from scratch and reseed |

## Authentication

Every route under `/api` (except `/auth/*`) requires the header:

```
Authorization: Bearer <token>
```

The token is returned by `POST /api/auth/login` and `POST /api/auth/register`.

## Endpoints

Base: `http://localhost:3000/api`

### Auth
| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Create account. Body: `name`, `email`, `password`, `alertDays?` |
| `POST` | `/auth/login` | Log in. Body: `email`, `password`. Returns `{ token, user }` |

### Profile
| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/me` | Logged-in user data |
| `PATCH` | `/me` | Update `name` and/or `alertDays` (alert window) |

### Categories
| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/categories` | List categories (alimentos, bebidas, limpeza, higiene) |

### Products
| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/products` | List the user's products. Filters via query string (below) |
| `POST` | `/products` | Create a product |
| `GET` | `/products/:id` | Product detail |
| `PATCH` | `/products/:id` | Edit product fields |
| `PATCH` | `/products/:id/quantity` | Adjust quantity (app steppers) |
| `DELETE` | `/products/:id` | Delete a product |

**`GET /products` filters (query string):**

- `search` — search by name (case-insensitive)
- `category` — category id (`alimentos`, `bebidas`, ...)
- `status` — `ok` | `low` | `expiring` | `expired`
- `sort` — `name` (default) | `expiresAt` | `lastUpdated` | `quantity`

Example: `GET /products?category=limpeza&status=low&sort=expiresAt`

**Quantity adjustment** (`PATCH /products/:id/quantity`) accepts:
- `{ "delta": 1 }` or `{ "delta": -1 }` — increment/decrement (never below 0)
- `{ "quantity": 5 }` — absolute value

### Dashboard / Alerts
| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/dashboard/summary` | Total, counters and low/expiring lists (Home screen) |
| `GET` | `/alerts` | Items needing attention, grouped by type (Alerts screen) |

## Product payload

```json
{
  "id": 3,
  "name": "Café torrado 500g",
  "categoryId": "alimentos",
  "category": { "id": "alimentos", "name": "Alimentos" },
  "quantity": 1,
  "minQuantity": 1,
  "unit": "pacote",
  "expiresAt": "2026-06-05",
  "lastUpdated": "2026-05-20",
  "daysUntilExpiry": 4,
  "status": { "type": "expiring", "label": "Vence em breve" }
}
```

## Status model

Computed on every response (same as the app's `statusItem`), in this order:

1. **expired** — the expiry date has passed
2. **low** — `quantity < minQuantity` or `quantity == 0`
3. **expiring** — expires within the user's `alertDays` window (default 7 days)
4. **ok** — otherwise

Each product includes `daysUntilExpiry` (days until expiry; negative = expired;
`null` if no expiry date) and `status: { type, label }`. The `label` is pt-BR display text.

## Project layout

```
api/
  docker-compose.yml         ← Postgres 16
  prisma/
    schema.prisma            ← User, Category, Product
    seed.ts                  ← sample data (mirrors MockData.kt)
  src/
    server.ts                ← server bootstrap
    app.ts                   ← Express app (middleware, routes)
    config/env.ts            ← environment variables
    schemas.ts               ← Zod validation per route
    lib/                     ← prisma, jwt, errors, status (helpers)
    middleware/              ← auth (JWT), validate (Zod), error handler
    controllers/             ← auth, user, category, product, dashboard
    routes/index.ts          ← route map
```
