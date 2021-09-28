package com.application.familymapclient;

import com.application.familymapclient.backend.ServerProxy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;

public class ServerProxyTest {
    ServerProxy serverProxy;
    RegisterRequest registerRequest;
    RegisterResult registerResult;

    String serverHost = "localhost";
    String serverPort = "8080";


    @Before
    public void setUp() {
        serverProxy = ServerProxy.getServerProxy();
        registerRequest = new RegisterRequest("prestonk","password",
                "preston@mail.net","Preston","Keller", "m");
         registerResult = serverProxy.register("localhost", "8080", registerRequest);
    }

    @Test
    public void registerSuccess() {
        Assert.assertNotNull(registerResult);
        Assert.assertTrue(registerResult.isSuccess());
    }

    @Test
    public void loginSuccess() {
        LoginRequest loginRequest = new LoginRequest("prestonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        Assert.assertNull(loginResult.getMessage());
        Assert.assertTrue(loginResult.isSuccess());
        Assert.assertNotNull(loginResult.getAuthtoken());
    }

    @Test
    public void registerFail() {
        registerResult = serverProxy.register(serverHost, serverPort, registerRequest);
        Assert.assertFalse(registerResult.isSuccess());
    }

    @Test
    public void loginFail() {
        LoginRequest loginRequest = new LoginRequest("presstonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        Assert.assertFalse(loginResult.isSuccess());
    }

    @Test
    public void getPersonsSuccess() {
        LoginRequest loginRequest = new LoginRequest("prestonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        String authToken = loginResult.getAuthtoken();
        PersonResult personResult = serverProxy.getPersons(serverHost, serverPort, authToken);
        Assert.assertNotNull(personResult);
        Assert.assertNull(personResult.getMessage());
        Assert.assertTrue(personResult.isSuccess());

        Assert.assertNotNull(personResult.getData());
    }

    @Test
    public void getEventSuccess() {
        LoginRequest loginRequest = new LoginRequest("prestonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        String authToken = loginResult.getAuthtoken();
        EventResult eventResult = serverProxy.getEvents(serverHost, serverPort, authToken);
        Assert.assertNotNull(eventResult);
        Assert.assertNull(eventResult.getMessage());
        Assert.assertTrue(eventResult.isSuccess());

        Assert.assertNotNull(eventResult.getData());
    }

    @Test
    public void getPersonFail() {
        LoginRequest loginRequest = new LoginRequest("prestonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        String authToken = loginResult.getAuthtoken();
        PersonResult personResult = serverProxy.getPersons(serverHost, serverPort, "randomAuthTokenSequence");
        Assert.assertNotNull(personResult);
        Assert.assertNotNull(personResult.getMessage());
        Assert.assertFalse(personResult.isSuccess());
    }

    @Test
    public void getEventFail() {
        LoginRequest loginRequest = new LoginRequest("prestonk", "password");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        String authToken = loginResult.getAuthtoken();
        EventResult eventResult = serverProxy.getEvents(serverHost, serverPort, "fictitious auth token");
        Assert.assertNotNull(eventResult);
        Assert.assertNotNull(eventResult.getMessage());
        Assert.assertFalse(eventResult.isSuccess());
    }


}
