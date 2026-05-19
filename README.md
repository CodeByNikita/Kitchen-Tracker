# Kitchen Tracker

A full-stack kitchen inventory app for tracking food, expiry dates, opened items, saved recipes, and expiry reminders.

The project has:

- Spring Boot backend API
- React + Vite frontend
- H2 file database for local persistence
- PWA setup so the app can be added to a phone home screen
- Gemini-powered recipe suggestions from expiring items
- Saved recipes
- Optional Web Push notifications for expiry alerts

## Project Structure

```text
kitchen-tracker-project/
├── kitchen-tracker-api/   # Spring Boot backend
└── kitchen-tracker-ui/    # React/Vite frontend
```

## Features

- Add, edit, delete kitchen items
- Track quantity, unit, category, location, and expiry date
- Mark items as opened
- Optional "use-by after opening" tracking
- Filter by category and location
- Sort by expiry, name, or added order
- Generate recipe ideas from expiring food using Gemini
- Save recipe ideas to a Saved Recipes view
- Installable PWA
- Optional push notifications for items nearing expiry

## Requirements

- Java 21+
- Maven wrapper included in the backend
- Node.js + npm
- Optional: Gemini API key for recipe suggestions
- Optional: VAPID keys for background push notifications

## Backend Setup

```bash
cd kitchen-tracker-api
./mvnw spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

## Frontend Setup

In a second terminal:

```bash
cd kitchen-tracker-ui
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

## Environment Variables

Secrets are intentionally not committed.

The backend reads optional keys from environment variables:

```bash
export GEMINI_API_KEY="your_gemini_key"
export VAPID_PUBLIC_KEY="your_vapid_public_key"
export VAPID_PRIVATE_KEY="your_vapid_private_key"
export NOTIFICATION_CRON_TOKEN="your_long_random_cron_secret"
```

For deployed Postgres/CORS config, the backend also supports:

```bash
export DATABASE_URL="jdbc:postgresql://host:5432/database"
export DATABASE_DRIVER="org.postgresql.Driver"
export DATABASE_USERNAME="your_database_user"
export DATABASE_PASSWORD="your_database_password"
export FRONTEND_ORIGINS="https://your-frontend.vercel.app"
```

The frontend supports:

```bash
VITE_API_BASE_URL=https://your-backend.onrender.com
```

Then start the backend in the same terminal:

```bash
./mvnw spring-boot:run
```

You can also copy the example file:

```bash
cd kitchen-tracker-api
cp .env.example .env
```

Fill in `.env`, then run:

```bash
./run-local.sh
```

Do not commit `.env`.

## Gemini Recipes

Recipe suggestions use Gemini through the backend endpoint:

```text
POST /api/recipes/suggest
```

If `GEMINI_API_KEY` is not set, the rest of the app still works, but recipe generation will show a clear error.

Get a Gemini API key from:

```text
https://aistudio.google.com/app/apikey
```

## Push Notifications

Push notifications are optional.

If `VAPID_PUBLIC_KEY` and `VAPID_PRIVATE_KEY` are set, the backend exposes push subscription endpoints and can send expiry alerts.

If VAPID keys are missing, the app still runs, but background push notification setup is disabled.

For free deployments where the backend may sleep, you can use an external cron service such as cron-job.org to wake the backend and trigger expiry alerts.

Reminder times can be changed in the app under **Settings**. You can add more than one reminder time. Configure cron-job.org to call the backend every hour; the backend checks the saved reminder times and only sends once per reminder time per day.

Protected endpoint:

```text
GET /api/push/run-expiry-alerts?token=YOUR_NOTIFICATION_CRON_TOKEN
```

Example production URL:

```text
https://your-backend.onrender.com/api/push/run-expiry-alerts?token=YOUR_NOTIFICATION_CRON_TOKEN
```

Set `NOTIFICATION_CRON_TOKEN` as an environment variable on the backend host. Use a long random value and do not commit it.

For testing, you can force the notification job:

```text
GET /api/push/run-expiry-alerts?token=YOUR_NOTIFICATION_CRON_TOKEN&force=true
```

## PWA Build

To create a production build:

```bash
cd kitchen-tracker-ui
npm run build
```

The app uses `vite-plugin-pwa` and a custom service worker for push notifications.

## Deployment

Recommended personal-use deployment:

- Frontend: Vercel
- Backend: Render
- Database: Neon Postgres

### Frontend on Vercel

Project root:

```text
kitchen-tracker-ui
```

Build command:

```bash
npm run build
```

Output directory:

```text
dist
```

Environment variable:

```text
VITE_API_BASE_URL=https://your-backend.onrender.com
```

### Backend on Render

Project root:

```text
kitchen-tracker-api
```

Build command:

```bash
./mvnw clean package -DskipTests
```

Start command:

```bash
java -jar target/kitchen-tracker-api-0.0.1-SNAPSHOT.jar
```

Environment variables:

```text
DATABASE_URL=jdbc:postgresql://host:5432/database
DATABASE_DRIVER=org.postgresql.Driver
DATABASE_USERNAME=your_database_user
DATABASE_PASSWORD=your_database_password
FRONTEND_ORIGINS=https://your-frontend.vercel.app
GEMINI_API_KEY=your_gemini_key
VAPID_PUBLIC_KEY=your_vapid_public_key
VAPID_PRIVATE_KEY=your_vapid_private_key
NOTIFICATION_CRON_TOKEN=your_long_random_secret
```

For Neon, copy the host/database/user/password from the connection details and convert it into the JDBC URL shown above.

## Safety Notes

Before pushing to GitHub:

- Do not commit `.env`
- Do not commit H2 database files
- Do not commit API keys
- Keep Gemini and VAPID keys in environment variables

The backend `.gitignore` excludes:

- `.env`
- `.env.*`
- `data/`
- `*.mv.db`
- `*.trace.db`

## Useful Commands

Backend compile:

```bash
cd kitchen-tracker-api
./mvnw compile
```

Frontend lint:

```bash
cd kitchen-tracker-ui
npm run lint
```

Frontend build:

```bash
cd kitchen-tracker-ui
npm run build
```
