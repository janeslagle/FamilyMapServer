package Handler;

import DAO.DataAccessException;
import Result.ClearResponse;
import Service.ClearService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

public class ClearHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            // Clear request is a POST HTTP method so make sure have POST request with exchange obj
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                Gson gson = new Gson();

                // Have no clear request class so skip straight to the service part of the handler classes

                // Call service class to perform requested function, pass it the java request obj
                ClearService service = new ClearService();

                // Recieve java response obj from service (so call the service class method have)
                ClearResponse response = service.ClearService();

                // Send HTTP response back to client with status code, response body
                if (!response.getSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }

                // Get the response body output stream
                Writer resBody = new OutputStreamWriter(exchange.getResponseBody());

                gson.toJson(response, resBody);

                resBody.close();
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
