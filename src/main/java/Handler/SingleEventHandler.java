package Handler;

import DAO.DataAccessException;
import Request.SingleEventRequest;
import Result.SingleEventResponse;
import Service.SingleEventService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

// Just like SinglePersonHandler but replace all personID with eventID
public class SingleEventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");

                    // Get the eventID string
                    String theUrlPath = exchange.getRequestURI().toString().substring(7);  // the eventID is given directly after the / and /event/ takes up indices 0-5 in the urlpath

                    // Now that this handler is for single event requests so ALWAYS have urlpath of format /event/[EVENT ID WANT HERE]
                    // So know that the eventID will always come after the / here
                    int givesEventID = theUrlPath.indexOf("/") + 1;
                    String eventID = theUrlPath.substring(givesEventID);   // No ending index here bc know nothing comes after the eventID

                    // Now have the eventID for the request so have everything need to make the request
                    Gson gson = new Gson();
                    SingleEventService service = new SingleEventService();

                    SingleEventRequest request = new SingleEventRequest(eventID, authToken);

                    // Then input the request object here!
                    SingleEventResponse response = service.SingleEventService(request);

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
