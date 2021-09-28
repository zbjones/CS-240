package Handlers;

import DataAccess.DataAccessException;
import Requests.LoadRequest;
import Results.LoadResult;
import Services.LoadService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * Calls the LoadService to import new Person, User, and Event data into the database.
 */
public class LoadHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("LoadHandler initiating.");
        Gson gson = new Gson();
        try {
            if (isPostRequest(exchange)) {
                LoadService loadService = new LoadService();
                InputStream requestBodyStream = exchange.getRequestBody();
                String requestBody = readString(requestBodyStream);

                LoadRequest loadRequest = gson.fromJson(requestBody, LoadRequest.class);
                LoadResult loadResult = loadService.loadData(loadRequest);
                String resultJson = gson.toJson(loadResult);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
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
