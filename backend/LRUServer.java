
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;


public class LRUServer {

    private static LRUCache cache;


    public static void main(String[] args) throws Exception {

        /*
         * Default cache capacity = 3
         */
        cache = new LRUCache(3);


        /*
         * Create HTTP server on port 8080.
         */
        HttpServer server =
            HttpServer.create(
                new InetSocketAddress(8080),
                0
            );


        /*
         * API endpoints.
         */
        server.createContext(
            "/put",
            LRUServer::handlePut
        );

        server.createContext(
            "/get",
            LRUServer::handleGet
        );

        server.createContext(
            "/cache",
            LRUServer::handleCache
        );

        server.createContext(
            "/clear",
            LRUServer::handleClear
        );

        server.createContext(
            "/capacity",
            LRUServer::handleCapacity
        );


        /*
         * Start server.
         */
        server.setExecutor(null);

        server.start();


        System.out.println(
            "LRU Server started at:"
        );

        System.out.println(
            "http://localhost:8080"
        );
    }


    /*
     * PUT API
     *
     * Example:
     * /put?key=10&value=100
     */
    private static void handlePut(
        HttpExchange exchange
    ) throws IOException {

        Map<String, String> params =
            getQueryParameters(
                exchange.getRequestURI()
            );


        if (!params.containsKey("key")
            || !params.containsKey("value")) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Key and value are required\"}"
            );

            return;
        }


        try {

            int key =
                Integer.parseInt(
                    params.get("key")
                );


            int value =
                Integer.parseInt(
                    params.get("value")
                );


            cache.put(key, value);


            String response =
                createResponse(
                    "PUT",
                    key,
                    value
                );


            sendResponse(
                exchange,
                200,
                response
            );

        }

        catch (NumberFormatException e) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Key and value must be numbers\"}"
            );
        }
    }


    /*
     * GET API
     *
     * Example:
     * /get?key=10
     */
    private static void handleGet(
        HttpExchange exchange
    ) throws IOException {

        Map<String, String> params =
            getQueryParameters(
                exchange.getRequestURI()
            );


        if (!params.containsKey("key")) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Key is required\"}"
            );

            return;
        }


        try {

            int key =
                Integer.parseInt(
                    params.get("key")
                );


            int value =
                cache.get(key);


            boolean hit = value != -1;


            String response =
                "{"
                + "\"operation\":\"GET\","
                + "\"key\":" + key + ","
                + "\"value\":" + value + ","
                + "\"hit\":" + hit + ","
                + "\"cache\":" + cache.getCacheAsJson()
                + "}";


            sendResponse(
                exchange,
                200,
                response
            );

        }

        catch (NumberFormatException e) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Key must be a number\"}"
            );
        }
    }


    /*
     * CACHE API
     *
     * Returns current cache state.
     */
    private static void handleCache(
        HttpExchange exchange
    ) throws IOException {

        String response =
            "{"
            + "\"capacity\":"
            + cache.getCapacity()
            + ","
            + "\"size\":"
            + cache.getSize()
            + ","
            + "\"cache\":"
            + cache.getCacheAsJson()
            + "}";


        sendResponse(
            exchange,
            200,
            response
        );
    }


    /*
     * CAPACITY API
     *
     * Example:
     * /capacity?value=5
     *
     * Changes the cache capacity.
     *
     * Existing cache is cleared.
     */
    private static void handleCapacity(
        HttpExchange exchange
    ) throws IOException {

        Map<String, String> params =
            getQueryParameters(
                exchange.getRequestURI()
            );


        if (!params.containsKey("value")) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Capacity is required\"}"
            );

            return;
        }


        try {

            int newCapacity =
                Integer.parseInt(
                    params.get("value")
                );


            if (newCapacity <= 0) {

                sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Capacity must be greater than 0\"}"
                );

                return;
            }


            /*
             * Create a new cache with
             * the selected capacity.
             */
            cache =
                new LRUCache(newCapacity);


            String response =
                "{"
                + "\"message\":\"Capacity updated\","
                + "\"capacity\":"
                + cache.getCapacity()
                + ","
                + "\"size\":"
                + cache.getSize()
                + ","
                + "\"cache\":[]"
                + "}";


            sendResponse(
                exchange,
                200,
                response
            );

        }

        catch (NumberFormatException e) {

            sendResponse(
                exchange,
                400,
                "{\"error\":\"Capacity must be a number\"}"
            );
        }
    }


    /*
     * CLEAR API
     */
    private static void handleClear(
        HttpExchange exchange
    ) throws IOException {

        cache.clear();


        String response =
            "{"
            + "\"message\":\"Cache cleared\","
            + "\"cache\":[]"
            + "}";


        sendResponse(
            exchange,
            200,
            response
        );
    }


    /*
     * Parse URL query parameters.
     */
    private static Map<String, String>
    getQueryParameters(URI uri) {

        Map<String, String> result =
            new HashMap<>();


        String query =
            uri.getQuery();


        if (query == null) {
            return result;
        }


        String[] pairs =
            query.split("&");


        for (String pair : pairs) {

            String[] parts =
                pair.split("=", 2);


            if (parts.length == 2) {

                result.put(
                    parts[0],
                    parts[1]
                );
            }
        }


        return result;
    }


    /*
     * Create PUT response.
     */
    private static String createResponse(
        String operation,
        int key,
        int value
    ) {

        return "{"
            + "\"operation\":\""
            + operation
            + "\","
            + "\"key\":"
            + key
            + ","
            + "\"value\":"
            + value
            + ","
            + "\"cache\":"
            + cache.getCacheAsJson()
            + "}";
    }


    /*
     * Send JSON response to browser.
     */
    private static void sendResponse(
        HttpExchange exchange,
        int statusCode,
        String response
    ) throws IOException {

        /*
         * Allow frontend to communicate
         * with Java backend.
         */
        exchange.getResponseHeaders()
            .set(
                "Access-Control-Allow-Origin",
                "*"
            );


        exchange.getResponseHeaders()
            .set(
                "Content-Type",
                "application/json"
            );


        byte[] data =
            response.getBytes(
                StandardCharsets.UTF_8
            );


        exchange.sendResponseHeaders(
            statusCode,
            data.length
        );


        try (
            OutputStream output =
                exchange.getResponseBody()
        ) {

            output.write(data);
        }
    }
}

