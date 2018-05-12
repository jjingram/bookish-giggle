package net.schmuse.bookish_giggle;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Database {

    private final static Logger logger = LoggerFactory.getLogger(Database.class);

    private Connection connection;

    Database() throws SQLException {
        String url = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");
        Flyway flyway = new Flyway();
        flyway.setDataSource(url, username, password);
        flyway.migrate();
        connection = DriverManager.getConnection(url);
        logger.info("Database connection established on {}", url);
    }

    Connection getConnection() {
        return connection;
    }
}
