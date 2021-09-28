package Handlers;

import DataAccess.DataAccessException;
import Results.FillResult;
import Services.FillService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls FillService to access and populate the database with random generation information for a specified root user.
 * All persons and events associated with the user are deleted.
 */
public class FillHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        System.out.println("FillHandler initiating.");
        try {
            if (isPostRequest(exchange)) {
                Headers requestHeaders = exchange.getRequestHeaders();

                FillService fillService = new FillService();
                String uri = exchange.getRequestURI().toString();
                uri = uri.substring(6);
                String resultJson;

                //Case if the number of generations is specified.
                if (uri.contains("/")) {
                    int location = uri.indexOf("/");
                    String username = uri.substring(0, location);
                    int numGenerations = Integer.parseInt(uri.substring(location + 1));
                    FillResult fillResult = fillService.fill(username, numGenerations);
                    resultJson = gson.toJson(fillResult);
                }
                //Default case, number of generations is not specified
                else {
                    int numGenerations = 4;
                    FillResult fillResult = fillService.fill(uri, numGenerations);
                    resultJson = gson.toJson(fillResult);
                }

                //Send 200, copy data to response body, etc.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                writeToOutputStream(resultJson, respBody);
                respBody.close();
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch(IOException | DataAccessException exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}
