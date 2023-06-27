package Handler;

import DAO.DataAccessException;
import Result.FillResponse;
import Service.FillService;
import Request.FillRequest;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Don't actually have request body for fill request SO don't read request obj from request body
                // INSTEAD use exchange.getRequestURI() and manually fill request class from info given in URI instead of reading it from request body

                // Gets the urlpath starting after the /fill/ part of the url of the request
                String theUrlPath = exchange.getRequestURI().toString().substring(6);  // Gets urlpath starting with the username (so after the /fill/ part which takes up indices 0-5 in the urlpath)

                // Want to get the username and generations out of the urlpath
                String theUsername;
                int generations;

                // Figure out if have default case or not!
                // Will only have / in urlPath if have generations (bc have /generations in the urlpath)
                if (!theUrlPath.contains("/")) {
                    // If don't have a slash, means just have a username after /fill/ and not /generations after the username so want default case of 4 generations
                    theUsername = theUrlPath;
                    generations = 4;
                }
                else {
                    // This means have / after the username and after the / comes the number of generations want generate for the username given
                    int endOfUsername = theUrlPath.indexOf("/");         // The username comes right before the /
                    theUsername = theUrlPath.substring(0, endOfUsername);

                    // Generations is the next thing right after the / and know nothing comes after it so dont need specify ending index
                    int givesGenerations = theUrlPath.indexOf("/") + 1;
                    String generationsStr = theUrlPath.substring(givesGenerations);
                    generations = Integer.parseInt(generationsStr);      // Turn it into an int so that can use it how want to
                }

                // Now can call the request with the username and generations parameters just got from the url path that the user will actually input
                Gson gson = new Gson();
                FillService service = new FillService();

                // Make the request object with username, generations just found from the url path thing
                FillRequest request = new FillRequest(theUsername, generations);

                // Then input the request object here!
                FillResponse response = service.FillService(request);

                // Send HTTP response back to client with status code, response body
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
