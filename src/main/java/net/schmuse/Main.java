package net.schmuse;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends NanoHTTPD {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private static Connection db;

    private Main(int port) throws IOException, SQLException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Server started on port {}", Integer.toString(port));
        db = getDb();
        logger.info("Database connection started");
    }

    public static void main(String[] args) {
        try {
            new Main(Integer.parseInt(System.getenv("PORT")));
        } catch (IOException e) {
            logger.error("Couldn't start server: {}", e);
        } catch (NumberFormatException e) {
            logger.error("$PORT must be set and a number");
        } catch (SQLException e) {
            logger.error("Couldn't connect to database");
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().substring(1);
        String query = session.getQueryParameterString();
        return newFixedLengthResponse("Hello, World!");
    }

    private static Connection getDb() throws SQLException {
        String url = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");
        Migration migration = new Migration(url, username, password);
        migration.migrate();
        return DriverManager.getConnection(url);
    }

}
