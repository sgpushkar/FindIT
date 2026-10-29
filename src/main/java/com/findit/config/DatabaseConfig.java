package com.findit.config;

import com.findit.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${findit.db.path:data/lostfound.db}")
    private String configuredDbPath;

    @Bean
    @Primary
    public DataSource dataSource() {
        File dbFile = resolveDbFile();
        File dir = dbFile.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            log.error("Failed to create database directory: {}", dir.getAbsolutePath());
        }

        String resolvedDatabasePath = dbFile.getAbsolutePath();
        log.info("SQLite Database file path: {}", resolvedDatabasePath);

        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:" + resolvedDatabasePath);
        com.findit.util.Database.setDataSource(ds);
        return ds;
    }

    private File resolveDbFile() {
        String path = System.getProperty("findit.db.path");
        if (isBlank(path)) {
            path = System.getenv("FINDIT_DB_PATH");
        }
        if (!isBlank(path)) {
            return new File(path);
        }

        String relative = isBlank(configuredDbPath) ? "data/lostfound.db" : configuredDbPath;
        File file = new File(relative);
        if (file.isAbsolute()) {
            return file;
        }

        String base = System.getProperty("user.dir");
        return new File(base, relative);
    }

    @Bean
    public ApplicationRunner databaseInitializer(DataSource dataSource) {
        return args -> {
            try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");

                String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "email TEXT UNIQUE NOT NULL,"
                        + "password TEXT NOT NULL,"
                        + "role TEXT DEFAULT 'USER',"
                        + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

                String itemsTable = "CREATE TABLE IF NOT EXISTS items ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "user_id INTEGER NOT NULL,"
                        + "type TEXT NOT NULL,"
                        + "item_name TEXT NOT NULL,"
                        + "category TEXT NOT NULL,"
                        + "description TEXT,"
                        + "location TEXT NOT NULL,"
                        + "item_date TEXT,"
                        + "status TEXT DEFAULT 'ACTIVE',"
                        + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)";

                st.execute(usersTable);
                st.execute(itemsTable);
                st.execute("CREATE INDEX IF NOT EXISTS idx_items_type_status ON items(type, status);");

                seedDataIfEmpty(c);
                log.info("Database tables verified and seeded successfully.");
            } catch (SQLException e) {
                log.error("Failed to initialise database tables", e);
                throw new RuntimeException(e);
            }
        };
    }

    private void seedDataIfEmpty(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        c.setAutoCommit(false);
        try {
            insertUser(c, "Admin", "admin@findit.local", "admin123", "ADMIN");
            long studentId = insertUser(c, "Demo Student", "student@findit.local", "student123", "USER");

            String sql = "INSERT INTO items (user_id, type, item_name, category, description, location, item_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, date('now', ?))";
            Object[][] samples = {
                    {"LOST", "Black Wallet", "Wallet", "Black leather wallet with a college ID and some cash.", "Library", "-1 day"},
                    {"FOUND", "Blue Water Bottle", "Accessories", "Steel bottle with a few stickers, left on a table.", "Canteen", "-2 day"},
                    {"LOST", "Boat Earphones", "Electronics", "Black Boat wireless earphones in a small case.", "Computer Lab", "-3 day"},
                    {"FOUND", "Student ID Card", "ID Cards", "ID card of a second-year student, found at the entrance.", "Main Gate", "-1 day"},
                    {"FOUND", "Black Wireless Earphones", "Electronics", "Wireless earphones found near the lab benches.", "Computer Lab", "-2 day"},
                    {"LOST", "Engineering Maths Textbook", "Books", "Grey cover, name written on the first page.", "Classroom 204", "-4 day"}
            };
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (Object[] s : samples) {
                    ps.setLong(1, studentId);
                    ps.setString(2, (String) s[0]);
                    ps.setString(3, (String) s[1]);
                    ps.setString(4, (String) s[2]);
                    ps.setString(5, (String) s[3]);
                    ps.setString(6, (String) s[4]);
                    ps.setString(7, (String) s[5]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
            log.info("Demo users and items successfully seeded into database.");
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    private long insertUser(Connection c, String name, String email, String password, String role)
            throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password));
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : -1;
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
