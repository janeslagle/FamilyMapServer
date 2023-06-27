package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

// This file will be based off of the Handler class examples we walked through in class videos and also based off of info given about it in Implementation video
public class FileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            // Want ignore everything except GET requests
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                // Get request URI from the exchange
                String urlPath = exchange.getRequestURI().toString();

                // Set to "/index.html" if the url path is null or just /
                if (urlPath == null || urlPath.equals("/")) {
                    urlPath = "/index.html";
                }

                // Now that have the url path, get the file path for that url
                String filePath = "web" + urlPath;

                // Now that have the file path for the file want, create a file object for it
                File wantedFile = new File(filePath);

                // Get response body output stream as seen in the handler class examples
                OutputStream respBody = exchange.getResponseBody();

                // Check if the file want exists or not
                // First go through case where we do have the file
                if (wantedFile.exists()) {
                    // If the file exists then want to put it in the response
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                // Case where file does not exist
                else {
                    // Then need to send a 404 error and response that HTTP was not found
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);

                    // Account for how we need to say there is a 404 error in the file (need keep track of that)
                    String urlPath404 = "web/HTML/404.html";
                    wantedFile = new File(urlPath404);
                }

                // Now that have finished going through the file, copy contents of file into the response body output stream
                Files.copy(wantedFile.toPath(), respBody);

                // Now done here so close the output stream response body
                respBody.close();

                success = true;
            }

            // This covers how want to send an error that says Method Not Allowed for if don't use a get request
            if (!success) {
                // The HTTP request was invalid somehow, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                // Since the client request was invalid, they will not receive the
                // list of games, so we close the response body output stream,
                // indicating that the response is complete.
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code
            // to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            // Since the server is unable to complete the request, the client will
            // not receive the list of games, so we close the response body output stream,
            // indicating that the response is complete.
            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
        }
    }
}
