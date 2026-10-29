# FindIT — Campus Lost & Found System

> **"Find what was lost. Return what was found."**  
> A localized, full-stack Lost & Found Management System built specifically for college campuses using **Spring Boot 3**, **React 19**, and **SQLite**.

---

## 1. About The Project

On any college campus, students lose valuable personal items every day: student ID cards, wallets, keys, headphones, water bottles, and notebooks. Traditional solutions like bulletin notice boards, WhatsApp groups, or social media posts are unorganized, easily buried, and require manual searching.

**FindIT** provides a centralized, dedicated digital platform for the entire campus:
- Students can quickly report items they **lost** or items they **found**.
- A custom **rule-based matching engine** automatically compares reports in real time and calculates a percentage similarity score to suggest possible matches.
- Item owners can mark their items as **Resolved** once returned.
- Campus moderators/administrators have an **Admin Portal** to oversee all reports, inspect statistics, and manage registered users.
- **100% Local-First & Zero-Cloud**: Runs entirely on the host machine without external servers, third-party cloud subscriptions, or external database setups. All data lives in a local SQLite database (`data/lostfound.db`).

---

## 2. Technology Stack — What We Used to Make It

FindIT was engineered with a modern full-stack decoupled architecture featuring an embedded Spring Boot backend and an interactive React Single Page Application (SPA).

| Layer | Technology | Version | Purpose & Rationale |
| :--- | :--- | :--- | :--- |
| **Frontend Framework** | **React** | 19.3 | High-performance component-based user interface with declarative state management. |
| **Frontend Tooling** | **Vite** | 8.3 | Ultra-fast bundling, lightning-quick Hot Module Replacement (HMR), and optimized production builds. |
| **Client Routing** | **React Router DOM** | 7.18 | Single Page Application (SPA) routing with seamless browser history without page reloads. |
| **Icons & Visuals** | **Lucide React** | 1.64 | Crisp, consistent SVG icons across all buttons, cards, tags, and navigation elements. |
| **Design System** | **Custom CSS** | CSS3 | Minimalist editorial aesthetic using CSS variables, off-white/crimson palette, smooth animations, and responsive layouts. |
| **Backend Framework** | **Spring Boot** | 3.3.5 | Production-grade Java framework providing RESTful API controllers, dependency injection, and automatic configuration. |
| **Web Server** | **Embedded Apache Tomcat** | 10.1 | Embedded directly inside Spring Boot—no external Tomcat installation or WAR deployment required. |
| **Language & Runtime** | **Java** | 17+ (supports 21, 24) | Modern Java featuring records, pattern matching, clean exception handling, and robust concurrency. |
| **Database Engine** | **SQLite** | 3.46 | Serverless, zero-configuration SQL database storing everything in a single, portable `.db` file. |
| **Data Access** | **Spring JDBC & HikariCP** | 3.3.5 | Parameterized SQL queries using `PreparedStatement` to ensure complete protection against SQL injection. |
| **Task Runner / Build** | **Maven & npm** | 3.8+ / 10+ | Standard build management for backend dependencies and frontend packaging. |

---

## 3. Architecture & How It Works

```
┌────────────────────────────────────────────────────────────────────────┐
│                        USER BROWSER / CLIENT                          │
│                                                                        │
│   React 19 Single Page Application (Served at http://localhost:8080)   │
│   • Home Feed & Live Counters       • Dynamic Search & Filter Controls │
│   • Dual-Choice Report Submission   • Item Details & Resolve Dialog    │
│   • Tabbed Student Dashboard        • Full Admin Portal & User Directory│
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │ JSON API (CORS & Cookies / Bearer)
                                  ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT REST BACKEND                          │
│                                                                        │
│   • AuthController     (/api/auth)      → Session & Token Security     │
│   • ItemController     (/api/items)     → Search, Create, Resolve     │
│   • DashboardController(/api/dashboard) → Filtered User Reports        │
│   • AdminController    (/api/admin)     → Moderation & Metrics         │
│   • CategoryController (/api/categories)→ Campus Categories            │
│   • MatchService       (Algorithm)      → Real-time similarity scoring │
│   • WebConfig          (SPA Forwarding) → Rewrites non-API routes      │
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │ JDBC Connection (HikariCP)
                                  ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        SQLITE DATABASE FILE                            │
│                         data/lostfound.db                              │
│                                                                        │
│   • users table (id, name, email, password_hash, role, created_at)     │
│   • items table (id, user_id, type, name, category, location, status)  │
│   • idx_items_type_status index for rapid query execution              │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Key Features

### 1. Unified Lost & Found Reporting
- A clean, interactive form allows users to toggle between **"I lost something"** and **"I found something"**.
- Inputs include: item name, category dropdown, incident date (with past-date validation), campus location, and optional description.
- Immediately upon submission, the matching algorithm runs and displays **instant possible matches** if an opposite report already exists.

### 2. Intelligent Rule-Based Matching Algorithm
When a **LOST** item is created (or vice versa), the system compares it against all active opposite-type reports using a weighted scoring model:
- **Category Match (+40 points)**: Exact match between categories (e.g. *Electronics* == *Electronics*).
- **Location Match (+40 points)**: Exact or substring location match (e.g. *Library* == *Central Library*). Partial word overlap gives **+20 points**.
- **Keyword Overlap (up to +20 points)**: String tokenization ignoring stop words (*the*, *a*, *in*, *near*, *with*, etc.) calculating the Jaccard-like overlap of descriptive words.
- Any score $\ge 60\%$ is surfaced as a **Possible Match** with a visual percentage meter bar and direct link to inspect the item.

### 3. Dynamic Search & Multi-Filter Engine
- Live search filtering by **Keyword** (matches item name, description, category, and location).
- Filters for **Type** (*All*, *Lost*, *Found*), **Category** (*Electronics*, *Wallet*, *Keys*, *Books*, *ID Cards*, etc.), **Location**, and **Status** (*Active*, *Resolved*).
- Instant counter displaying matching result quantities.

### 4. Personal Student Dashboard ("My Reports")
- Shows reports submitted by the logged-in student.
- Filter tabs: **All**, **Lost**, **Found**, **Active**, **Resolved**.
- Dedicated notification box highlighting potential matches for any currently active report owned by the student.

### 5. "Mark as Resolved" Workflow
- Protects item state: only the original reporter or an administrator can mark an item resolved.
- Interactive modal confirmation prevents accidental clicks.
- Resolved items remain visible with a distinct resolved badge for historical reference.

### 6. Admin Moderation Portal
- High-level metric cards: *Total Users*, *Lost Reports*, *Found Reports*, *Active Reports*, and *Resolved Count*.
- Comprehensive reports management table with quick **Resolve** and **Delete** actions.
- Directory of registered students showing name, email, role, and registration date.

---

## 5. Demo Credentials

For testing and viva demonstrations, the database is pre-seeded on first start with sample data and accounts:

| Role | Email | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@findit.local` | `admin123` | Full access to moderation portal, deletion, and user lists |
| **Student** | `student@findit.local` | `student123` | Standard student account owning demo sample reports |

> **Tip**: The login page includes **one-click demo login buttons** so you can sign in instantly without typing credentials!

---

## 6. Prerequisites

To run FindIT, ensure the following are installed on your machine:
- **Java JDK 17** or newer (`java -version`)
- **Node.js 18+** & **npm** (`node -v` and `npm -v`) — *Only needed if you want to rebuild the frontend or run in dev mode.*
- **Maven 3.8+** (`mvn -v`) — *Maven is automatically discovered if installed or if using the portable script.*

---

## 7. How to Run

### Method 1: The One-Click Startup Script (Windows Recommended)
We provide automated launcher scripts that automatically free port 8080 from rogue processes, detect your installed JDK, and start the app:

**Using Batch (Double-click or run in terminal):**
```cmd
start-server.bat
```

**Using PowerShell:**
```powershell
.\start-server.ps1
```
*(If PowerShell blocks script execution on a new PC, run: `powershell -ExecutionPolicy Bypass -File .\start-server.ps1`)*

Open your browser to: **http://localhost:8080/**  
To stop the server, run: `.\stop-server.bat` or `.\stop-server.ps1`.

---

### Method 2: Single Command for Live Development (Hot-Reloading)
If you want to edit React code or backend code with immediate live reload:

```bash
npm run dev
```
- Concurrently runs the **Spring Boot backend** on `http://localhost:8080`
- Runs the **Vite React frontend** on `http://localhost:5173` with proxy forwarding for `/api`
- Open your browser to: **http://localhost:5173/**

---

### Method 3: Run the Pre-Packaged All-in-One JAR
The React frontend is compiled directly into Spring Boot's `src/main/resources/static/`, creating a completely self-contained `.jar`:

```bash
java -jar target/FindIT-2.0.0.jar
```
Open your browser to: **http://localhost:8080/**.

---

## 8. REST API Reference

All backend communication happens over RESTful JSON APIs:

### Authentication (`/api/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user (returns user info and Bearer token). |
| `POST` | `/api/auth/register` | Register a new student account. |
| `POST` | `/api/auth/logout` | Terminate session and invalidate token. |
| `GET` | `/api/auth/me` | Fetch currently logged-in user profile. |

### Reports & Items (`/api/items`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/items` | Search & filter reports (`q`, `type`, `category`, `location`, `status`). |
| `GET` | `/api/items/recent?limit=6` | Fetch recent reports for home page feed. |
| `GET` | `/api/items/stats` | Aggregated report counts (lost, found, resolved, total). |
| `GET` | `/api/items/{id}` | Detailed report information and permission metadata. |
| `POST` | `/api/items` | Submit a new lost/found report (returns item + immediate matches). |
| `POST` | `/api/items/{id}/resolve`| Mark report as RESOLVED (owner or admin only). |
| `GET` | `/api/items/{id}/matches`| Fetch candidate matches for a specific report. |

### Dashboard & Admin (`/api/dashboard` & `/api/admin`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard/my-reports` | Retrieve user's reports with tab filter and active matches. |
| `GET` | `/api/admin/overview` | Fetch admin statistics, all reports, and all registered users. |
| `DELETE`| `/api/admin/items/{id}` | Permanently delete a report (admin only). |
| `GET` | `/api/categories` | Retrieve list of allowed campus categories. |

---

## 9. Project Directory Structure

```
FindIT/
├── pom.xml                               # Maven project configuration (Spring Boot 3.3.5)
├── README.md                             # Comprehensive project documentation
├── package.json                          # Root runner configuration (concurrent dev launcher)
├── start-server.bat                      # Windows one-click start script
├── stop-server.bat                       # Windows one-click stop script
├── start-server.ps1                      # PowerShell startup script (auto-detects JDK & port)
├── stop-server.ps1                       # PowerShell termination script
├── data/
│   └── lostfound.db                      # SQLite database file (created automatically)
│
├── src/main/
│   ├── java/com/findit/
│   │   ├── FindItApplication.java        # Spring Boot main class (@SpringBootApplication)
│   │   │
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java       # SQLite DataSource, table init & data seeder
│   │   │   └── WebConfig.java            # CORS mappings & SPA client route forwarding
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java       # Login, register, logout, me endpoints
│   │   │   ├── ItemController.java       # Search, create, resolve, match endpoints
│   │   │   ├── DashboardController.java  # Student dashboard endpoints
│   │   │   ├── AdminController.java      # Admin moderation & user directory
│   │   │   └── CategoryController.java   # Category metadata endpoint
│   │   │
│   │   ├── dao/
│   │   │   ├── UserDAO.java              # User database access with PreparedStatements
│   │   │   └── ItemDAO.java              # Item queries, dynamic SQL filtering & updates
│   │   │
│   │   ├── dto/                          # API Request & Response transfer objects
│   │   ├── model/                        # User, Item, and Match domain models
│   │   ├── service/
│   │   │   ├── AuthService.java          # Authentication & session token store
│   │   │   ├── ItemService.java          # Business logic, validation & permission checks
│   │   │   └── MatchService.java         # Rule-based similarity scoring engine
│   │   │
│   │   └── util/
│   │       ├── PasswordUtil.java         # SHA-256 password hashing & verification
│   │       └── Validator.java            # Input validation & category constants
│   │
│   └── resources/
│       ├── application.properties        # Port configuration & database path
│       └── static/                       # Compiled React distribution (index.html, JS, CSS)
│
└── frontend/                             # React 19 Single Page Application
    ├── package.json                      # Frontend dependencies (React, Vite, Lucide)
    ├── vite.config.js                    # Vite configuration with proxy to port 8080
    ├── index.html                        # HTML entry point with Inter typography
    └── src/
        ├── App.jsx                       # Top-level client router and layout wrapper
        ├── index.css                     # FindIT Design System (editorial minimal aesthetic)
        ├── context/
        │   ├── AuthContext.jsx           # Global user authentication state & session hooks
        │   └── ToastContext.jsx          # Notification toast manager
        ├── services/
        │   └── api.js                    # Centralized fetch wrapper for backend APIs
        ├── components/
        │   ├── Header.jsx                # Responsive navigation bar with mobile drawer
        │   ├── Footer.jsx                # Campus Lost & Found footer
        │   ├── ItemCard.jsx              # Card component with tags, status, & meta
        │   └── MatchCard.jsx             # Match preview with similarity score meter
        └── pages/
            ├── HomePage.jsx              # Landing hero, quick search, & live stats
            ├── SearchPage.jsx            # Dynamic search & multi-criteria filter page
            ├── ReportPage.jsx            # Dual-choice reporting with instant match review
            ├── ItemDetailsPage.jsx       # Detailed report view & resolve dialog
            ├── DashboardPage.jsx         # Student personal dashboard with tabs
            ├── AdminPage.jsx             # Admin metrics & moderation tables
            ├── LoginPage.jsx             # Sign-in form with one-click demo credentials
            ├── RegisterPage.jsx          # Student account registration
            └── NotFoundPage.jsx          # Custom 404 navigation view
```

---

## 10. Frequently Asked Questions & Viva Notes

<details>
<summary><strong>Q: Why did you convert the project from JSP / Servlets to React + Spring Boot?</strong></summary>

1. **User Experience**: JSP requires full page reloads for every filter change, submission, or tab switch. React provides a fluid, instant Single Page Application (SPA) experience without screen flickers.
2. **Modern Architecture**: Decoupling the frontend (React) and backend (Spring Boot REST API) follows industry best practices. The backend exposes clean JSON APIs that could also support future mobile apps (Android/iOS) without rewriting business logic.
3. **No External Server Dependency**: Legacy Servlets required a manually downloaded and configured standalone Tomcat server. Spring Boot packages an embedded Tomcat server directly inside the executable JAR, making deployment effortless on any PC.
</details>

<details>
<summary><strong>Q: How does the matching algorithm work?</strong></summary>

It uses a deterministic, rule-based scoring function in `MatchService.java`:
- Category match gives **40%**.
- Location match gives **up to 40%** (exact match or containment = 40%, partial token overlap = 20%).
- Keyword overlap between item names (filtering out common English stop words) gives **up to 20%**.
- If total score $\ge 60\%$, it is suggested as a possible match.
</details>

<details>
<summary><strong>Q: How does security and authentication work?</strong></summary>

Authentication is handled by `AuthService.java` supporting two concurrent mechanisms:
- Standard HTTP session cookies (`HttpSession`).
- Bearer session tokens stored in a thread-safe token registry for REST API calls.
- Passwords are encrypted using SHA-256 hashing in `PasswordUtil.java`, and password hashes are filtered out of all JSON responses using Jackson's `@JsonProperty(access = WRITE_ONLY)`.
</details>

<details>
<summary><strong>Q: How are single-page client routes handled by Spring Boot?</strong></summary>

In `WebConfig.java`, a custom `PathResourceResolver` intercepts non-API requests and maps them to `index.html`. This ensures that if a user directly accesses or refreshes a URL like `http://localhost:8080/search` or `http://localhost:8080/dashboard`, Spring Boot returns `index.html` and React Router loads the correct page instead of throwing a 404 error.
</details>

<details>
<summary><strong>Q: How can I reset the database back to clean demo data?</strong></summary>

Stop the server (`.\stop-server.bat`), delete the file `data/lostfound.db`, and start the server again (`.\start-server.bat`). The database will be automatically recreated and seeded with default demo accounts and sample items.
</details>

---

## 11. License

This project is developed for educational and campus demonstration purposes.
