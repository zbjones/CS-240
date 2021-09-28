package Server;

import Handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main Server class.
 * Server can handle 12 waiting connections
 * Default port number is 8080
 */
public class Server {

    private static final int MAX_CONNECTIONS_WAITING = 12;
    private HttpServer mainServer;

    //Port number of localhost is set to 8080, run the server
    public static void main(String[] args) {
        //String portNum = args[0];
        String portNum = "8080";
        new Server().runServer(portNum);
    }

    //Creates an HttpServer with the given port number and connections waiting
    private void runServer(String portNum) {
        System.out.println("Starting the HTTP server...");

        try {
            mainServer = HttpServer.create(new InetSocketAddress(Integer.parseInt(portNum)), MAX_CONNECTIONS_WAITING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainServer.setExecutor(null);

        System.out.println("Creating contexts...");

        mainServer.createContext("/", new FileHandler());
        mainServer.createContext("/load", new LoadHandler());
        mainServer.createContext("/clear", new ClearHandler());
        mainServer.createContext("/user/login", new LoginHandler());
        mainServer.createContext("/event/", new EventIDHandler());
        mainServer.createContext("/event", new EventHandler());
        mainServer.createContext("/person/", new PersonIDHandler());
        mainServer.createContext("/person", new PersonHandler());
        mainServer.createContext("/fill", new FillHandler());
        mainServer.createContext("/user/register", new RegisterHandler());

        mainServer.start();

        System.out.println("The server has been started and is now running.");
    }
}
