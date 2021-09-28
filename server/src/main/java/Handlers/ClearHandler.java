package Handlers;

import DataAccess.DataAccessException;
import Results.ClearResult;
import Services.ClearService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;


/**
 * Calls ClearService to handle clearing all information from tables.
 */
public class ClearHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("ClearHandler initiating.");
        Gson gson = new Gson();
        try {
            if (isPostRequest(exchange)) {
                ClearService clearService = new ClearService();

                ClearResult clearResult = clearService.clear();
                String resultJson = gson.toJson(clearResult);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = exchange.getResponseBody();
                writeToOutputStream(resultJson, responseBody);
                responseBody.close();
            }
            //If not a post request, send 400
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } catch (IOException | DataAccessException exception) {
            exception.printStackTrace();
        }
    }
}
