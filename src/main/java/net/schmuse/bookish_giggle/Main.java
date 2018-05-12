package net.schmuse.bookish_giggle;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Database db = new Database();
            new Server(Integer.parseInt(System.getenv("PORT")), db);
        } catch (IOException e) {
            logger.error("Couldn't start server: {}", e);
        } catch (NumberFormatException e) {
            logger.error("$PORT must be set and a number");
        } catch (SQLException e) {
            logger.error("Couldn't connect to database");
        }
    }

}
