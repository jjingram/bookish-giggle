package net.schmuse.bookish_giggle;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Database {

    private Connection connection;

    Database() throws SQLException {
        String url = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");
        Flyway flyway = new Flyway();
        flyway.setDataSource(url, username, password);
        flyway.migrate();
        connection = DriverManager.getConnection(url);
    }

    Connection getConnection() {
        return connection;
    }
}
