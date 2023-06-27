package Handler;

import DAO.DataAccessException;
import Request.SinglePersonRequest;
import Result.SinglePersonResponse;
import Service.SinglePersonService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

public class SinglePersonHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");

                    // When make single person request, will have the personID for the perosn want to find given in the urlpath for the request
                    // SO need get that personID string out of the urlpath since don't have a request body that read in (read it from the urlpath instead)
                    // Just like the fill handler class

                    // The info that want from the urlpath begins after the /person/ part, so begins at index 7 (/person/ takes up indices 0 through 6)
                    String theUrlPath = exchange.getRequestURI().toString().substring(8);

                    // Use this handler when are working with the single person request so KNOW that the urlpath will contain a / after person like how need it to
                    // This means that can just set the personID to be the url path stuff after the / bc already know that the / is there with this handler
                    int givesPersonID = theUrlPath.indexOf("/") + 1;  // Know that the personID is right after the / and only thing have after the / so dont need specify end index
                    String personID = theUrlPath.substring(givesPersonID);

                    // Now have the personID for the person that need to plug into the request!
                    Gson gson = new Gson();
                    SinglePersonService service = new SinglePersonService();

                    SinglePersonRequest request = new SinglePersonRequest(personID, authToken);

                    // Then input the request object here!
                    SinglePersonResponse response = service.SinglePersonService(request);

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
