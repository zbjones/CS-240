package Handlers;

import DataAccess.DataAccessException;
import Results.EventIDResult;
import Services.EventIDService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 *  Calls EventIDService to retrieve a specific event.
 */
public class EventIDHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success;
        System.out.println("EventIDHandler initiating.");
        try {
            if (isGetRequest(exchange)) {
                Headers requestHeaders = exchange.getRequestHeaders();
                if (requestHeaders.containsKey("Authorization")) {
                    EventIDService eventIDService = new EventIDService();
                    Gson gson = new Gson();

                    //Get the eventID we are looking for from the URL and the authtoken from the headers
                    String authToken = requestHeaders.getFirst("Authorization");
                    String uri = exchange.getRequestURI().toString();
                    String eventIDToCheck = uri.substring(7);

                    EventIDResult eventIDResult = eventIDService.findSingleEvent(eventIDToCheck, authToken);
                    String resultJson;

                    //Case when there is an error and the message field of the result is populated
                    if (eventIDResult.getMessage() != null) {
                        resultJson = gson.toJson(eventIDResult);
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }
                    //No error case, event was found and belongs to user with authToken
                    else {
                        resultJson = gson.toJson(eventIDResult);
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }

                    OutputStream responseBody = exchange.getResponseBody();
                    writeToOutputStream(resultJson, responseBody);
                    responseBody.close();
                }
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}