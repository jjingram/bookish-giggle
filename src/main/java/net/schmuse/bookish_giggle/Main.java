package net.schmuse.bookish_giggle;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends NanoHTTPD {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private static Database db;

    private Main(int port) throws IOException, SQLException {
        super(port);
        db = new Database();
        logger.info("Database connection started");
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Server started on port {}", Integer.toString(port));
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
        Method method = session.getMethod();
        switch (method) {
            case GET:
                String title = uri
                        .replaceAll("[^a-zA-Z0-9 ]", "")
                        .replaceAll("\\s+", "-")
                        .toLowerCase();
                return newFixedLengthResponse(title);
            case POST:
                return doPost(session);
            case PATCH:
                return newFixedLengthResponse("PATCH");
            default:
                return newFixedLengthResponse(
                        Response.Status.METHOD_NOT_ALLOWED,
                        NanoHTTPD.MIME_PLAINTEXT,
                        "405 Method Not Allowed\n"
                );
        }
    }

    private Response doPost(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();

        if (!headers.containsKey("content-type")) {
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: content-type header required\n"
            );
        }

        ContentType contentType = new ContentType(headers.get("content-type"));
        if (!contentType.getContentType().equals("text/markdown")) {
            logger.info(contentType.getContentType());
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: content-type must be text/markdown\n"
            );
        }

        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            logger.error("Internal Server Error: IOException: {}", e.toString());
            return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "500 Internal Server Error\n"
            );
        } catch (ResponseException e) {
            return newFixedLengthResponse(e.getStatus(), NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        }

        if (!files.containsKey("postData")) {
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: body empty\n"
            );
        }

        String data = files.get("postData");
        logger.info(data);

        String boundary = "---";
        Integer start = data.indexOf(boundary);
        if (start != 0) {
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: no preamble\n"
            );
        }
        Integer end = data.indexOf(boundary, boundary.length());
        String preamble = data.substring(boundary.length(), end);

        Map<String, List<String>> keyValueStore = new HashMap<>();
        String[] splits = preamble.split(";");
        for (String split : splits) {
            String[] keyValue = split.split(":");
            if (keyValue.length != 2) {
                return newFixedLengthResponse(
                        Response.Status.BAD_REQUEST,
                        NanoHTTPD.MIME_PLAINTEXT,
                        String.format("400 Bad Request: bad key-value pair: %s\n", Arrays.toString(keyValue))
                );
            }
            String key = keyValue[0].trim();
            String[] values = keyValue[1].split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            keyValueStore.put(key, Arrays.asList(values));
        }

        return newFixedLengthResponse(
                Response.Status.CREATED,
                NanoHTTPD.MIME_PLAINTEXT,
                "201 Created\n"
        );
    }
}
