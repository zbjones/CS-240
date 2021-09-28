package Handlers;

import DataAccess.DataAccessException;
import Results.PersonIDResult;
import Services.PersonIDService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls the PersonIDService to return a specified event associated with a particular user.
 * Requires a valid authtoken
 */
public class PersonIDHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("PersonIDHandler initiating.");
        try {
            if (isGetRequest(exchange)) {
                PersonIDService personIDService = new PersonIDService();

                //Get the authtoken from the headers and the uri from the requestURI
                Gson gson = new Gson();
                Headers requestHeaders = exchange.getRequestHeaders();
                String authToken = requestHeaders.getFirst("Authorization");
                String uri = exchange.getRequestURI().toString();
                String personIDToCheck = uri.substring(8);

                //Call the PersonIDService method and convert result to JSON
                PersonIDResult personIDResult = personIDService.findSinglePerson(personIDToCheck, authToken);
                String resultJson = gson.toJson(personIDResult);

                //Case when there is an error, send HTTP_BAD_REQUEST
                if (personIDResult.getMessage() != null) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                //No error, person was found and belongs to user with authToken case
                else {
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
