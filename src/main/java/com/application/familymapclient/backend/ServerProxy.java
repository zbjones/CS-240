package com.application.familymapclient.backend;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;

/**
 * ServerProxy methods access the server and deserialize objects.
 * Calls Login, Register, Event, and Person API's
 * Adapted from the provided "Game" sample ServerProxy.
 */
public class ServerProxy {

    private static ServerProxy singletonInstance;

    //Singleton Constructor, we can only have one instance of ServerProxy
    public static ServerProxy getServerProxy() {
        if (singletonInstance == null) {
            singletonInstance = new ServerProxy();
        }
        return singletonInstance;
    }

    public LoginResult login(String serverHost, String serverPort, LoginRequest request) {

        try {
            Gson gson = new Gson();
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("POST");
            //We need to have an HTTP body
            http.setDoOutput(true);

            //Add a header
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();

            String loginRequest = gson.toJson(request);
            OutputStream body = http.getOutputStream();
            writeString(loginRequest, body);


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            int response = http.getResponseCode();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                return gson.fromJson(respData, LoginResult.class);
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                return new LoginResult("ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            return new LoginResult("There was an error encountered while logging in.");
        }
    }


    public RegisterResult register(String serverHost, String serverPort, RegisterRequest request) {

        try {
            Gson gson = new Gson();
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("POST");
            //We need to have an HTTP body
            http.setDoOutput(true);

            //Add a header
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();

            String registerRequest = gson.toJson(request);
            OutputStream body = http.getOutputStream();
            writeString(registerRequest, body);


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            int response = http.getResponseCode();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                return gson.fromJson(respData, RegisterResult.class);
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                return new RegisterResult("ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            return new RegisterResult("There was an error encountered while registering a user.");
        }
    }

    public EventResult getEvents(String serverHost, String serverPort, String authToken) {

        try {
            Gson gson = new Gson();
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            //We need to have an HTTP body
            http.setDoOutput(false);

            //Add a header
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", authToken);

            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                return gson.fromJson(respData, EventResult.class);
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                return new EventResult("ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            return new EventResult("There was an error encountered while fetching user events.");
        }
    }

    public PersonResult getPersons(String serverHost, String serverPort, String authToken) {

        try {
            Gson gson = new Gson();
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            //We need to have an HTTP body
            http.setDoOutput(false);

            //Add a header
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", authToken);

            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);
                PersonResult personResult = gson.fromJson(respData, PersonResult.class);
                return personResult;
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                return new PersonResult("ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            return new PersonResult("There was an error encountered while fetching user persons.");
        }
    }

    /*
        The readString method shows how to read a String from an InputStream.
    */
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
