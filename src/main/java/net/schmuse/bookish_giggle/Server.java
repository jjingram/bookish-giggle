package net.schmuse.bookish_giggle;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Server extends NanoHTTPD {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private static Database db;

    Server(int port, Database db) throws IOException {
        super(port);
        Server.db = db;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Server started on port {}", Integer.toString(port));
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        switch (session.getMethod()) {
            case GET:
                String title = session.getUri().substring(1)
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
                        NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED,
                        NanoHTTPD.MIME_PLAINTEXT,
                        "405 Method Not Allowed\n"
                );
        }
    }

    private static NanoHTTPD.Response doPost(NanoHTTPD.IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();

        if (!headers.containsKey("content-type")) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: content-type header required\n"
            );
        }

        NanoHTTPD.ContentType contentType = new ContentType(headers.get("content-type"));
        if (!contentType.getContentType().equals("text/markdown")) {
            logger.info(contentType.getContentType());
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
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
                    NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "500 Internal Server Error\n"
            );
        } catch (NanoHTTPD.ResponseException e) {
            return newFixedLengthResponse(e.getStatus(), NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        }

        if (!files.containsKey("postData")) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT,
                    "400 Bad Request: body empty\n"
            );
        }

        String data = files.get("postData");

        String boundary = "---";
        Integer start = data.indexOf(boundary);
        if (start != 0) {
            return newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
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
                        NanoHTTPD.Response.Status.BAD_REQUEST,
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
                NanoHTTPD.Response.Status.CREATED,
                NanoHTTPD.MIME_PLAINTEXT,
                "201 Created\n"
        );
    }
}
