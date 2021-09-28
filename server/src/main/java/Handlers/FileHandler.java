package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.*;

/**
 * Serves up the web api test page or any other file specified in the path.
 * Servers the 404 error page if the file specified is not valid.
 * Does not call a Service class.
 */
public class FileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String pathAsString;
            String requestURI = exchange.getRequestURI().toString();
            requestURI = requestURI.substring(1);

            //Serve up the default web api test page if no document path specified
            if (requestURI.equals("")) {
                pathAsString = "C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\web\\index.html";
            }
            //Try to serve the document specified
            else {
                pathAsString = "C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\web\\" + requestURI;
            }

            //Check to make sure the path to the document is valid
            if (isPathValid(pathAsString)) {
                //Send 200
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                //Pass the file to the response body and close the output stream
                OutputStream responseBody = exchange.getResponseBody();
                Path pathToFile = FileSystems.getDefault().getPath(pathAsString);
                Files.copy(pathToFile, responseBody);
                responseBody.close();
            }
            else {
                //Send the 404 file and HTTP_NOT_FOUND if the file pointed to is not valid.
                String string404 = "C:\\Users\\zacha\\IdeaProjects\\FamilyMapServer\\web\\HTML\\404.html";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                Path pathTo404 = FileSystems.getDefault().getPath(string404);
                OutputStream responseBody = exchange.getResponseBody();
                Files.copy(pathTo404, responseBody);
                responseBody.close();
            }
        } catch (IOException exception) {
            //Send 400 if an error. Close the response body, print the stack trace.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.getResponseBody().close();
            exception.printStackTrace();
        }
    }

    //Helper function to determine the path a file is pointing to contains a valid file
    private boolean isPathValid(String path) {
        try {
            File file = Paths.get(path).toFile();
            if (file.exists()) {
                return true;
            }
        } catch (InvalidPathException e) {
            return false;
        }
        return false;
    }
}
