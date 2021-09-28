package Handlers;

import DataAccess.DataAccessException;
import Requests.RegisterRequest;
import Results.RegisterResult;
import Services.RegisterService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls the RegisterService to create a new user with specified parameters.
 * Generate 4 generations of ancestor data
 */
public class RegisterHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("RegisterHandler initiating.");
        Gson gson = new Gson();
        try {
            if (isPostRequest(exchange)) {
                RegisterService registerService = new RegisterService();

                //Get the new user parameters from the request body and convert from Json
                InputStream requestBodyStream = exchange.getRequestBody();
                String requestBody = readString(requestBodyStream);
                RegisterRequest registerRequest = gson.fromJson(requestBody, RegisterRequest.class);

                //Call the Register Service method and handle the results of the method
                RegisterResult registerResult = registerService.registerUser(registerRequest);
                String resultJson = gson.toJson(registerResult);
                boolean success = registerResult.isSuccess();

                //Case if the operation succeeded and ran without errors. Send 200.
                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                //Case if there was an error in running the register service.  We send 400.
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
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
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, 0);
            exchange.getResponseBody().close();
        }
    }
}
