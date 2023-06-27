package Handler;

import DAO.DataAccessException;
import Request.LoadRequest;
import Result.LoadResponse;
import Service.LoadService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

public class LoadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            // Load request is a POST HTTP method so make sure have POST request with exchange obj
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                // Get headers out of request
                Headers reqHeaders = exchange.getRequestHeaders();

                // Authtoken not required for a load request so can skip that part and go straight to getting request body input stream part
                InputStream reqBody = exchange.getRequestBody();

                // Read JSON string from the input stream
                String reqData = readString(reqBody);

                // NOW need to get all the request data out

                // Start by deserializing JSON request body to java request obj using gson.fromJson
                // So first need to create the Gson obj will use to do this
                Gson gson = new Gson();
                LoadRequest request = (LoadRequest) gson.fromJson(reqData, LoadRequest.class);

                // Call service class to perform requested function, pass it the java request obj
                LoadService service = new LoadService();

                // Recieve java response obj from service (so call the service class method have)
                LoadResponse response = service.LoadService(request);

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

    /*
		The readString method shows how to read a String from an InputStream.
	*/
    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}
