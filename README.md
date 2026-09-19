# FindIT — Campus Lost & Found

> Find what was lost. Return what was found.

A local-only Lost & Found Management System for a college campus, built as a **Java mini project** with Servlets, JSP, JDBC and SQLite on Apache Tomcat 10.1.

---

## 1. Project overview

FindIT lets students report items they have lost or found, search everything that has been reported, and get **automatic "possible match" suggestions** between lost and found reports. Owners can mark their reports as resolved, and an administrator can moderate all reports.

Everything runs on your own computer. No internet, cloud service or external API is needed while the app runs. All data lives in one SQLite file: `lostfound.db`.

## 2. Features

- Register, log in and log out (sessions with `HttpSession`)
- Report a **LOST** or **FOUND** item with one shared form
- Search and filter by keyword, type, category, location and status
- Item details page with "Reported by"
- **Mark as Resolved** (only the owner or an admin can do this)
- Personal dashboard with filters: All / Lost / Found / Active / Resolved
- **Possible match system**: plain Java scoring on category, location and name keywords
- Admin dashboard: statistics, all reports, resolve or delete reports, list of users
- Friendly error pages (400 / 403 / 404 / 500) with no stack traces shown
- Empty states, responsive layout and subtle animations
- Demo data inserted on first start only

## 3. Technologies used

| Layer      | Technology                                   |
|------------|----------------------------------------------|
| Frontend   | HTML, CSS (custom, CSS variables), JSP + JSTL, a little vanilla JS |
| Backend    | Java 17, Jakarta Servlets 6.0, JSP 3.1       |
| Database   | SQLite through JDBC (`org.xerial:sqlite-jdbc`) |
| Server     | Apache Tomcat 10.1                            |
| Build      | Maven                                         |

## 4. Requirements

- **JDK 17** or newer (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Apache Tomcat 10.1.x**. Tomcat 9 will not work because it uses `javax.*` and this project uses `jakarta.*`.
- Internet is needed **once**, the first time Maven downloads the dependencies. After that, no internet is needed.

## 5. Installation

1. Unzip `FindIT.zip` anywhere, for example `C:\Projects\FindIT` or `~/Projects/FindIT`.
2. Download Tomcat 10.1 (the "Core" zip or tar.gz) from https://tomcat.apache.org/download-10.cgi and unzip it, for example to `C:\tomcat` or `~/tomcat`. This folder is called **TOMCAT_HOME** below.

## 6. How to configure Tomcat

No special configuration is needed.

- Make sure `JAVA_HOME` points to your JDK 17+.
- Tomcat listens on port **8080** by default. If something else uses 8080, change `port="8080"` in `TOMCAT_HOME/conf/server.xml`.
- **IntelliJ IDEA Ultimate:** Run → Edit Configurations → + → Tomcat Server → Local → choose TOMCAT_HOME → Deployment tab → + → Artifact → `FindIT:war exploded` → set Application context to `/FindIT`.
- **Eclipse (Enterprise Java):** File → Import → Existing Maven Project. Add a Tomcat 10.1 server in the Servers view, then right-click the project → Run As → Run on Server.
- **VS Code:** install "Extension Pack for Java" and "Community Server Connectors". Add Tomcat 10.1, build the WAR (see below), then right-click the server → Add Deployment → `target/FindIT.war`.

## 7. How to run the project

**Step 1 — Build the WAR.** Run this from the **project folder** (the folder containing `pom.xml`):

```bash
mvn clean package
```

This creates `target/FindIT.war`.

**Step 2 — Deploy.** Run this from the **project folder**:

```bash
# Windows (Command Prompt)
copy target\FindIT.war C:\tomcat\webapps\

# macOS / Linux
cp target/FindIT.war ~/tomcat/webapps/
```

**Step 3 — Start Tomcat.** Run this from the **TOMCAT_HOME/bin** folder:

```bash
# Windows
startup.bat

# macOS / Linux  (first time only: chmod +x *.sh)
./startup.sh
```

**Step 4 — Open the app:** http://localhost:8080/FindIT/

**Stop Tomcat** by running `shutdown.bat` or `./shutdown.sh` from **TOMCAT_HOME/bin**.

> After changing code, run `mvn clean package` again and copy the new WAR. Tomcat redeploys it automatically.

## 8. SQLite database location

The database is created **automatically** on first start:

```
TOMCAT_HOME/data/lostfound.db
```

It sits **outside** `webapps/`, so nobody can download it through the browser. The path comes from the `dbPath` context-param in `web.xml` (`data/lostfound.db`, resolved against Tomcat's base folder). You can override it:

```bash
# from TOMCAT_HOME/bin, before startup
set CATALINA_OPTS=-Dfindit.db.path=C:\FindIT\data\lostfound.db          # Windows
export CATALINA_OPTS="-Dfindit.db.path=$HOME/FindIT/data/lostfound.db"  # macOS/Linux
```

To reset all data, stop Tomcat, delete `lostfound.db` and start Tomcat again. You can open the file with "DB Browser for SQLite" to show the tables during your viva.

## 9. Default admin credentials

> **Demo credentials for the college project only. Do not use them in a real deployment.**

| Role    | Email                  | Password     |
|---------|------------------------|--------------|
| Admin   | admin@findit.local     | admin123     |
| Student | student@findit.local   | student123   |

The student account owns the sample reports, so the site does not look empty.

## 10. Project structure

```
FindIT/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/findit/
    │   ├── model/        User.java, Item.java, Match.java
    │   ├── dao/          UserDAO.java, ItemDAO.java
    │   ├── service/      MatchService.java         (matching algorithm)
    │   ├── filter/       AuthFilter.java           (login / admin protection)
    │   ├── servlet/      BaseServlet.java          (parent of all servlets)
    │   │                 HomeServlet, LoginServlet, RegisterServlet, LogoutServlet,
    │   │                 AddItemServlet, SearchServlet, ItemDetailsServlet,
    │   │                 MyReportsServlet, ResolveItemServlet,
    │   │                 AdminServlet, DeleteItemServlet
    │   └── util/         Database.java, AppInitListener.java,
    │                     PasswordUtil.java, Validator.java
    └── webapp/
        ├── index.jsp                  (redirects to /home)
        ├── css/style.css
        ├── js/app.js
        └── WEB-INF/
            ├── web.xml
            └── views/                 (JSPs, not directly reachable from the browser)
                ├── includes/ header.jspf, footer.jspf, card.jspf, matches.jspf
                ├── home.jsp, login.jsp, register.jsp, report.jsp, report-success.jsp
                ├── search.jsp, item.jsp, dashboard.jsp, admin.jsp, error.jsp
```

### URL map

| URL              | Servlet              | Login? |
|------------------|----------------------|--------|
| `/home`          | HomeServlet          | No     |
| `/search`        | SearchServlet        | No     |
| `/item?id=`      | ItemDetailsServlet   | No     |
| `/login` `/register` `/logout` | Login/Register/LogoutServlet | No |
| `/report`        | AddItemServlet       | Yes    |
| `/dashboard`     | MyReportsServlet     | Yes    |
| `/resolve` (POST)| ResolveItemServlet   | Yes, owner or admin |
| `/admin`         | AdminServlet         | Admin  |
| `/admin/delete` (POST) | DeleteItemServlet | Admin |

### Request flow (MVC)

```
Browser form ──► Servlet (Controller) ──► DAO ──► SQLite
                     │                     ▲
                     ▼                     │
              Model objects (User, Item) ──┘
                     │
                     ▼
              JSP (View) ──► HTML back to the browser
```

Example, reporting an item: `report.jsp` form → `AddItemServlet.doPost()` → validation → `ItemDAO.create()` → `INSERT` into SQLite → `MatchService.findMatches()` → `report-success.jsp`.

## 11. Explanation of Java concepts used

### The matching algorithm (`MatchService`)

For a LOST report, every ACTIVE FOUND report is scored, and the other way round:

| Rule | Points |
|------|--------|
| Same category | +40 |
| Same location (or one contains the other) | +40, partial word overlap +20 |
| Item-name keyword overlap (shared words ÷ smaller word set) | up to +20 |

Text is lower-cased, punctuation is removed and stop words ("the", "near", "my"…) are ignored. Reports scoring **60 or more** are shown as "Possible Match Found" with a confidence percentage. Matches appear after submitting a report, on the item page (for the owner) and on the dashboard.

## Java Concepts Demonstrated

| Concept | Where |
|---------|-------|
| **Classes** | `User`, `Item`, `Match`, the DAOs, servlets and utilities |
| **Objects** | `new Item()` in `AddItemServlet`; DAOs create `User`/`Item` objects from each `ResultSet` row |
| **Encapsulation** | Model fields are `private` with getters/setters; `Database` hides the JDBC URL behind `getConnection()` |
| **Inheritance** | Every servlet `extends BaseServlet`, which `extends HttpServlet`; `AuthFilter extends HttpFilter` |
| **Polymorphism / overriding** | `doGet()` / `doPost()` overridden in each servlet; `Match.compareTo()` |
| **Interfaces** | `Match implements Comparable<Match>`; `AppInitListener implements ServletContextListener`; models implement `Serializable` |
| **Exception handling** | `try/catch (SQLException)` in every servlet → friendly error page; try-with-resources closes connections; rollback in `Database.seedData()` |
| **Collections** | `List<Item>`, `ArrayList`, `HashSet` (keyword tokens), `Collections.sort()` for match ranking |
| **JDBC** | `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`, generated keys, batch inserts, transactions |
| **Servlets** | 11 `@WebServlet` classes acting as controllers |
| **JSP** | Views in `WEB-INF/views`, JSTL `<c:forEach>`, `<c:if>`, `<c:out>` (escapes HTML), static includes |
| **Sessions** | `HttpSession` stores the logged-in `User`; invalidated on logout; new session on login |
| **CRUD** | Create `ItemDAO.create`, Read `findById/search`, Update `updateStatus`, Delete `delete` |
| **MVC** | Model = `model/`, View = JSPs, Controller = servlets, Data access = `dao/` |
| **SQL** | `CREATE TABLE`, `INSERT`, `SELECT … JOIN`, `LIKE`, `COUNT`, `UPDATE`, `DELETE` |
| **PreparedStatement** | All queries use `?` placeholders. User input is never concatenated into SQL |
| **Filters & listeners** | `AuthFilter` protects pages; `AppInitListener` creates the database at startup |

### Security notes

- `PreparedStatement` everywhere prevents SQL injection
- `<c:out>` / `fn:escapeXml` escape all user content, which prevents XSS
- Ownership is checked on the server before resolving a report
- The admin area is protected by role in `AuthFilter`
- Passwords are stored as SHA-256 hashes via `PasswordUtil`, a single place to upgrade to BCrypt later
- The session ID is regenerated at login; session cookies are `HttpOnly`
- The database file lives outside the web root

## 12. Viva questions and answers

**Q1: Why did you use SQLite?**
A: SQLite is lightweight and serverless, and it stores the whole database in one local file. There is nothing to install or configure, which makes it ideal for a small local college project.

**Q2: What is a Servlet?**
A: A Java class running inside a web container (Tomcat) that receives HTTP requests and produces responses. Here servlets act as controllers.

**Q3: Difference between Servlet and JSP?**
A: A servlet is Java code that may produce HTML. A JSP is HTML with embedded tags that Tomcat compiles into a servlet. We use servlets for logic and JSPs for display.

**Q4: What is MVC and how is it applied?**
A: Model-View-Controller separates data, presentation and logic. Models are `User`/`Item`, views are JSPs, controllers are servlets, and DAOs handle the database.

**Q5: What is JDBC?**
A: Java Database Connectivity, the standard API for connecting to databases with `Connection`, `PreparedStatement` and `ResultSet`.

**Q6: Why PreparedStatement instead of Statement?**
A: It pre-compiles SQL and binds values safely through `?`, which prevents SQL injection and handles quoting automatically.

**Q7: What is a DAO?**
A: Data Access Object, a class that holds all SQL for one table, so servlets never contain SQL.

**Q8: How does session management work here?**
A: On login we store the `User` object in `HttpSession`. Tomcat tracks it with a `JSESSIONID` cookie. `AuthFilter` checks it, and logout calls `session.invalidate()`.

**Q9: How do you stop users from accessing pages without logging in?**
A: `AuthFilter` (a `@WebFilter`) intercepts `/report`, `/dashboard`, `/resolve` and `/admin/*`, and redirects to `/login` if no user is in the session.

**Q10: How do you stop someone from resolving another user's report?**
A: `ResolveItemServlet` loads the item and compares `item.userId` with the session user's id on the server. Only the owner or an admin may update it, otherwise it returns 403.

**Q11: What is the difference between GET and POST?**
A: GET sends data in the URL and should only read data (search, view). POST sends data in the body and is used for changes (login, report, resolve, delete).

**Q12: Explain the matching algorithm.**
A: It compares a report with opposite-type active reports: +40 for the same category, +40 for the same location, and up to +20 for shared name keywords. Scores of 60 or more are shown as possible matches.

**Q13: What is try-with-resources?**
A: A Java 7 feature that automatically closes `Connection`, `PreparedStatement` and `ResultSet` objects declared in `try(...)`, even when an exception occurs.

**Q14: How is the database created automatically?**
A: `AppInitListener` runs when Tomcat starts the app. It calls `Database.init()`, which creates the folder, runs `CREATE TABLE IF NOT EXISTS`, and inserts demo data only if the users table is empty.

**Q15: Why are JSPs inside WEB-INF?**
A: Files under `WEB-INF` cannot be opened directly from the browser, so every page must go through its servlet. This keeps the MVC flow and access checks intact.

**Q16: What is the role of web.xml when you also use annotations?**
A: Annotations (`@WebServlet`, `@WebFilter`, `@WebListener`) register components. `web.xml` holds app-wide settings: the database path, session timeout, welcome file and error pages.

**Q17: How do you prevent XSS?**
A: All user text is printed with JSTL `<c:out>` or `fn:escapeXml`, which turn `<` and `>` into `&lt;` and `&gt;`.

**Q18: Why Tomcat 10.1 and not Tomcat 9?**
A: Tomcat 10+ implements Jakarta EE (`jakarta.servlet.*`). Tomcat 9 uses the old `javax.servlet.*` package names.

**Q19: What is a foreign key here?**
A: `items.user_id` references `users.id`, so every report belongs to a valid user. Foreign keys are enabled with `PRAGMA foreign_keys = ON`.

**Q20: How would you improve this project?**
A: BCrypt password hashing, image uploads for items, email notifications for matches, CSRF tokens and pagination.

---

## RUNNING THE PROJECT (quick reference)

```bash
# 1. From the project folder (contains pom.xml)
mvn clean package
cp target/FindIT.war <TOMCAT_HOME>/webapps/        # Windows: copy target\FindIT.war <TOMCAT_HOME>\webapps\

# 2. From <TOMCAT_HOME>/bin
./startup.sh                                       # Windows: startup.bat

# 3. Open in the browser
http://localhost:8080/FindIT/

# 4. Log in with admin@findit.local / admin123  or  student@findit.local / student123

# 5. Stop the server, from <TOMCAT_HOME>/bin
./shutdown.sh                                      # Windows: shutdown.bat
```

Troubleshooting:
- **404 at /FindIT/**: check that `FindIT.war` is in `webapps/` and that Tomcat is version **10.1**.
- **Port in use**: change `8080` in `TOMCAT_HOME/conf/server.xml`.
- **"Database not initialised" error**: check `TOMCAT_HOME/logs/catalina.*.log`. Usually the `data/` folder is not writable.
