package Handlers;

import DataAccess.DataAccessException;
import Requests.LoginRequest;
import Results.LoginResult;
import Services.LoginService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Calls the LoginService to login the user and obtain a new authtoken for the user.
 */
public class LoginHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("LoginHandler initiating.");
        Gson gson = new Gson();
        try {
            if (isPostRequest(exchange)) {
                LoginService loginService = new LoginService();

                //Get the login request form the requestBodyStream and convert from JSON
                InputStream requestBodyStream = exchange.getRequestBody();
                String requestBody = readString(requestBodyStream);
                LoginRequest loginRequest = gson.fromJson(requestBody, LoginRequest.class);

                //Call the LoginService method
                LoginResult loginResult = loginService.login(loginRequest);
                boolean success = loginResult.isSuccess();

                //Handle whether the login is a success or a fail (send 200 or 400)
                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                OutputStream responseBody = exchange.getResponseBody();
                String resultJson = gson.toJson(loginResult);
                writeToOutputStream(resultJson, responseBody);
                responseBody.close();
            }

        } catch (IOException | DataAccessException exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
        }
    }
}
