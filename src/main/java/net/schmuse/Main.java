package net.schmuse;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main extends NanoHTTPD {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private Main(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Running at http://localhost:{}/", Integer.toString(port));
    }

    public static void main(String[] args) {
        try {
            new Main(Integer.parseInt(System.getenv("PORT")));
        } catch (IOException e) {
            logger.error("Couldn't start server: {}", e);
        } catch (NumberFormatException e) {
            logger.error("$PORT must be a number");
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        return newFixedLengthResponse("Hello, World!");
    }

}
