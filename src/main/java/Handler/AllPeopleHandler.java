package Handler;

import DAO.DataAccessException;
import Request.AllPeopleRequest;
import Result.AllPeopleResponse;
import Service.AllPeopleService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

public class AllPeopleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");

                    // Only need an authtoken for the request so go straight into making the service obj part of handler class
                    // No request body and nothing have to interpret from the urlpath so all good to go!
                    Gson gson = new Gson();
                    AllPeopleService service = new AllPeopleService();

                    // Only need an authtoken to make the request to get all the people for a user out of the DB
                    AllPeopleRequest request = new AllPeopleRequest(authToken);

                    // Then input the request object here!
                    AllPeopleResponse response = service.AllPeopleService(request);

                    if (!response.getSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }

                    // Get the response body output stream
                    Writer resBody = new OutputStreamWriter(exchange.getResponseBody());

                    // Get the response we have from the fill request / fill service
                    gson.toJson(response, resBody);

                    resBody.close();
                }
            }
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
        catch (IOException | DataAccessException e) {
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
