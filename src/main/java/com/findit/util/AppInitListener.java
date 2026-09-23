package com.findit.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.SQLException;

/** Creates the SQLite database and tables when Tomcat starts the app. */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String configured = event.getServletContext().getInitParameter("dbPath");
        try {
            Database.init(configured);
            event.getServletContext().log("FindIT database ready at " + Database.getDatabasePath());
        } catch (SQLException e) {
            event.getServletContext().log("FindIT could not initialise the database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // nothing to clean up - connections are closed after every query
    }
}
