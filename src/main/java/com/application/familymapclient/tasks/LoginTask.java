package com.application.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.ui.MainActivity;
import com.application.familymapclient.backend.ServerProxy;

import Models.Person;
import Requests.LoginRequest;
import Results.LoginResult;


/**
 * Class implementing runnable to be run in a separate thread asynchronously.
 * Called by LoginFragment. After successful login, LoginTask calls DataSync to get events and persons
 * that correspond to the provided authToken
 */
public class LoginTask implements Runnable {

    private final MainActivity mainActivity = new MainActivity();

    private final String ipAddress;
    private final String serverPort;

    private final LoginRequest[] loginRequests;

    private final Handler messageHandler;

    private static final String LOGIN_RESULT_KEY = "LoginResultKey";

    public LoginTask(Handler messageHandler, String ipAddress, String serverPort, LoginRequest... loginRequests) {
        this.loginRequests = loginRequests;
        this.messageHandler = messageHandler;
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy();

        LoginResult loginResult = null;
        for (LoginRequest loginRequest : loginRequests) {
            loginResult = serverProxy.login(ipAddress, serverPort, loginRequest);
        }
        //If the login was a success
        if (loginResult != null) {
            if (loginResult.getMessage() == null) {
                DataCache dataCache = null;
                dataCache = DataCache.getDataCache();
                dataCache.setAuthToken(loginResult.getAuthtoken());

                boolean syncSucceeded = false;
                try {
                    //Get the persons and events that correspond to the authtoken
                    DataSync dataSync = new DataSync(ipAddress, serverPort, loginResult.getAuthtoken());
                    syncSucceeded = dataSync.run();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (syncSucceeded) {
                    dataCache.structureData(loginResult.getPersonID());
                    Person user = dataCache.getUser();
                    sendMessage("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
                } else {
                    sendMessage("Login Failure: " + loginResult.getMessage());
                }
            } else {
                sendMessage(loginResult.getMessage());
            }
        }
    }

    private void sendMessage(String result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putString(LOGIN_RESULT_KEY, result);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
