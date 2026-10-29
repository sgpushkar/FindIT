# FindIT — Campus Lost & Found (Spring Boot + React)

> Find what was lost. Return what was found.

A modern, local Lost & Found Management System for a college campus, converted and rebuilt with a **Spring Boot 3.3.5 REST backend**, an interactive **React (Vite) frontend**, and **SQLite**.

---

## 1. Project overview

FindIT allows students to report items they have lost or found, search and filter all campus reports, and receive **automatic "possible match" suggestions** between lost and found items. Owners can mark their reports as resolved once an item is returned, and administrators can moderate reports and view registered students.

Everything runs locally on your computer with embedded Tomcat—**no external Tomcat or cloud services are required**. All data is automatically managed in a local SQLite file: `data/lostfound.db`.

---

## 2. Technologies used

| Layer       | Technology                                                        |
|-------------|-------------------------------------------------------------------|
| Frontend    | React 19, Vite, React Router 7, Lucide Icons, Custom CSS Design System |
| Backend     | Java 17/24, Spring Boot 3.3.5, Spring MVC REST, Embedded Tomcat  |
| Database    | SQLite through JDBC (`org.xerial:sqlite-jdbc`)                    |
| Build Tools | Maven 3.8+, Node.js & npm                                         |

---

## 3. Features

- **Authentication**: Register, log in, session and token-based auth (`HttpSession` + Bearer token), one-click demo login buttons.
- **Report Item**: Interactive toggle between "I lost something" and "I found something", date picker with past-date validation, category selection, and instant match recommendations upon submission.
- **Search & Filter**: Real-time filtering by keyword, type (`LOST` / `FOUND`), category, location, and status (`ACTIVE` / `RESOLVED`).
- **Item Details**: Detailed item view, reporter attribution, and owner/admin "Mark as Resolved" action with confirmation.
- **Matching Algorithm**: Rule-based scoring engine calculating similarity across category (+40), location (+40/20), and keyword overlap (up to +20) with visual percentage match meter bars.
- **User Dashboard ("My Reports")**: Tabbed views (`All`, `Lost`, `Found`, `Active`, `Resolved`), item overview, and immediate alerts for possible matches on active items.
- **Admin Portal**: Moderation dashboard with statistics cards (total users, lost, found, active, resolved), comprehensive reports table with resolve and delete operations, and user directory.
- **Self-Contained Deployment**: Vite builds static assets directly into Spring Boot (`src/main/resources/static/`), so a single `java -jar` serves both the API and the React Single Page App with client-side route fallback.

---

## 4. Default Demo Credentials

| Role    | Email                  | Password     |
|---------|------------------------|--------------|
| Admin   | `admin@findit.local`   | `admin123`   |
| Student | `student@findit.local` | `student123` |

*These accounts are automatically seeded into SQLite on the first application startup.*

---

## 5. How to Run (Single Command)

### Option 1: One-Click Startup Script (Builds & Runs Everything)
```powershell
.\start-server.ps1
```
Open **http://localhost:8080/** in your browser. (To stop: `.\stop-server.ps1`)

---

### Option 2: Run Both with Live Hot-Reloading (Single Command)
```powershell
npm run dev
```
Runs both Spring Boot (`mvn spring-boot:run`) and React Vite (`npm run dev`) simultaneously in the same terminal with live hot-reloading!

---

### Option 3: Run Pre-Built All-in-One JAR
```powershell
java -jar target/FindIT-2.0.0.jar
```
Open **http://localhost:8080/**.

---

#### Option B: Independent Development Mode (Hot Reloading)

If you want live hot-reloading for React development:

1. Start the Spring Boot backend:
   ```bash
   mvn spring-boot:run
   ```
   *(Backend runs on `http://localhost:8080`)*

2. In a separate terminal, start the Vite dev server:
   ```bash
   cd frontend
   npm run dev
   ```
   *(Frontend runs on `http://localhost:5173` with proxy forwarding `/api` to port 8080)*

---

## 6. REST API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` — Register a new student account
- `POST /api/auth/login` — Log in (returns user and session token)
- `POST /api/auth/logout` — Log out current session
- `GET /api/auth/me` — Retrieve current authenticated user profile

### Items (`/api/items`)
- `GET /api/items` — Search and filter items (`q`, `type`, `category`, `location`, `status`)
- `GET /api/items/recent?limit=6` — Get recent reports
- `GET /api/items/stats` — Summary counts (lost, found, resolved, total)
- `GET /api/items/{id}` — Get report details and permissions
- `POST /api/items` — Submit a lost or found report (returns created item + matches)
- `POST /api/items/{id}/resolve` — Mark a report as resolved (owner or admin)
- `GET /api/items/{id}/matches` — Get candidate matches for an item

### User Dashboard (`/api/dashboard`)
- `GET /api/dashboard/my-reports?filter=ALL` — User's reported items and active matches

### Administration (`/api/admin`)
- `GET /api/admin/overview` — Admin stats, all reports, and all registered users
- `DELETE /api/admin/items/{id}` — Delete a report permanently

### Categories (`/api/categories`)
- `GET /api/categories` — Get list of valid campus categories

---

## 7. Project Structure

```
FindIT/
├── pom.xml                               # Spring Boot Maven configuration
├── README.md                             # Documentation & instructions
├── start-server.ps1                      # One-click startup script
├── stop-server.ps1                       # Server termination script
├── data/
│   └── lostfound.db                      # SQLite database file (created automatically)
├── src/main/
│   ├── java/com/findit/
│   │   ├── FindItApplication.java        # Spring Boot entry point (@SpringBootApplication)
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java       # SQLite DataSource & auto-seeding
│   │   │   └── WebConfig.java            # CORS & React SPA route forwarding
│   │   ├── controller/
│   │   │   ├── AuthController.java       # Authentication endpoints
│   │   │   ├── ItemController.java       # Items search, create, resolve endpoints
│   │   │   ├── DashboardController.java  # User dashboard endpoints
│   │   │   ├── AdminController.java      # Admin overview & moderation endpoints
│   │   │   └── CategoryController.java   # Category list endpoint
│   │   ├── dao/
│   │   │   ├── UserDAO.java              # User data access
│   │   │   └── ItemDAO.java              # Item data access & dynamic search
│   │   ├── dto/                          # Request & response transfer objects
│   │   ├── model/                        # User, Item, Match entities
│   │   ├── service/
│   │   │   ├── AuthService.java          # Authentication & session token management
│   │   │   ├── ItemService.java          # Item business logic & permissions
│   │   │   └── MatchService.java         # Rule-based matching algorithm
│   │   └── util/
│   │       ├── PasswordUtil.java         # Password hashing & verification
│   │       └── Validator.java            # Form validation & category definitions
│   └── resources/
│       ├── application.properties        # Port & database properties
│       └── static/                       # Compiled React frontend distribution
└── frontend/                             # React Single Page Application (Vite)
    ├── package.json
    ├── vite.config.js                    # Vite configuration with proxy to port 8080
    ├── index.html
    └── src/
        ├── App.jsx                       # Main router & layout
        ├── index.css                     # FindIT Design System styles
        ├── context/
        │   ├── AuthContext.jsx           # Global user authentication state
        │   └── ToastContext.jsx          # Toast notification management
        ├── services/
        │   └── api.js                    # Unified REST API client
        ├── components/
        │   ├── Header.jsx                # Responsive navbar
        │   ├── Footer.jsx                # Site footer
        │   ├── ItemCard.jsx              # Report card component
        │   └── MatchCard.jsx             # Possible match card with score meter
        └── pages/
            ├── HomePage.jsx              # Landing page, quick search, live stats
            ├── SearchPage.jsx            # Browse and dynamic filter page
            ├── ReportPage.jsx            # Report submission with match suggestions
            ├── ItemDetailsPage.jsx       # Item details & resolve action
            ├── DashboardPage.jsx         # User dashboard with tabs
            ├── AdminPage.jsx             # Admin portal
            ├── LoginPage.jsx             # Login with demo accounts quick-fill
            ├── RegisterPage.jsx          # Registration form
            └── NotFoundPage.jsx          # Friendly 404 page
```
