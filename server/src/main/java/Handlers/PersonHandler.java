package Handlers;

import DataAccess.DataAccessException;
import Results.PersonResult;
import Services.PersonService;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls the PersonService to return all of the Persons associated with a particular user.  Requires a valid authtoken
 */
public class PersonHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("PersonHandler initiating.");
        try {
            if (isGetRequest(exchange)) {
                PersonService personService = new PersonService();

                //Get the authtoken from the request headers
                Gson gson = new Gson();
                Headers requestHeaders = exchange.getRequestHeaders();
                String authToken = requestHeaders.getFirst("Authorization");

                //Call the method of PersonService
                PersonResult personResult = personService.findAllPersons(authToken);
                String resultJson = gson.toJson(personResult);

                //Case when there is an error, send HTTP_BAD_REQUEST
                if (personResult.getMessage() != null) {
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
