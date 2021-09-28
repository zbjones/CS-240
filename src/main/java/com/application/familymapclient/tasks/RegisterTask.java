package com.application.familymapclient.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.backend.ServerProxy;

import Models.Person;
import Requests.RegisterRequest;
import Results.RegisterResult;

/**
 * Register Task implements runnable and is run asynchronously in sa separate thread
 * Calls DataSync to get persons and events
 */
public class RegisterTask implements Runnable {

    private final String ipAddress;
    private final String serverPort;

    private final RegisterRequest[] registerRequests;

    private final Handler messageHandler;

    private static final String REGISTER_RESULT_KEY = "RegisterResultKey";

    public RegisterTask(Handler messageHandler, String ipAddress, String serverPort, RegisterRequest... registerRequests) {
        this.registerRequests = registerRequests;
        this.messageHandler = messageHandler;
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy();

        RegisterResult registerResult = null;
        for (RegisterRequest registerRequest : registerRequests) {
            registerResult = serverProxy.register(ipAddress, serverPort, registerRequest);
        }
        //If the register is successful
        if (registerResult != null) {
            if (registerResult.getMessage() == null) {
                DataCache dataCache = null;
                dataCache = DataCache.getDataCache();
                dataCache.setAuthToken(registerResult.getAuthtoken());

                boolean syncSucceeded = false;
                try {
                    //Get persons and events
                    DataSync dataSync = new DataSync(ipAddress, serverPort, dataCache.getAuthtoken());
                    syncSucceeded = dataSync.run();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (syncSucceeded) {
                    dataCache.structureData(registerResult.getPersonID());
                    Person user = dataCache.getUser();
                    sendMessage("Welcome, " + user.getFirstName() + " " + user.getLastName() + "! You've registered.");
                } else {
                    sendMessage("Register Failure: " + registerResult.getMessage());
                }
            } else {
                sendMessage(registerResult.getMessage());
            }
        }
    }

    private void sendMessage(String result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putString(REGISTER_RESULT_KEY, result);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
