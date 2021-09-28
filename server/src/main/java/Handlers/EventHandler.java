package Handlers;

import DataAccess.DataAccessException;
import Results.EventResult;
import Services.EventService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls EventService to retrieve all events attached to a particular user.
 */
public class EventHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("EventHandler initiating.");
        try {
            if (isGetRequest(exchange)) {
                EventService eventService = new EventService();
                Gson gson = new Gson();
                Headers requestHeaders = exchange.getRequestHeaders();
                String authToken = requestHeaders.getFirst("Authorization");

                EventResult eventResult = eventService.findAllEvents(authToken);
                String resultJson;

                //There was an error and the message field within the result is not null
                if (eventResult.getMessage() != null) {
                    resultJson = gson.toJson(eventResult);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                //No error, case when the events were found and belongs to user with the provided authToken
                else {
                    resultJson = gson.toJson(eventResult);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }

                OutputStream responseBody = exchange.getResponseBody();
                writeToOutputStream(resultJson, responseBody);
                responseBody.close();
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
