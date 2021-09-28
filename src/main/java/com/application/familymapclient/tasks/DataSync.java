package com.application.familymapclient.tasks;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.backend.ServerProxy;

import Results.EventResult;
import Results.PersonResult;


/**
 * This class isn't actually a task implementing runnable.  Instead, it is a normal class that contains methods
 * for getting the events and persns from the Server
 * Called by LoginTask and RegisterTask.
 */
public class DataSync {

    private final String ipAddress;
    private final String serverPort;

    private final String[] authtokens;

    public DataSync(String ipAddress, String serverPort, String... authtokens) {
        this.authtokens = authtokens;
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
    }

    public boolean run() {
        ServerProxy serverProxy = new ServerProxy();

        EventResult eventResult = null;
        PersonResult personResult = null;

        for (String authtoken : authtokens) {
            eventResult = serverProxy.getEvents(ipAddress, serverPort, authtoken);
            personResult = serverProxy.getPersons(ipAddress, serverPort, authtoken);
        }

        if (eventResult != null && personResult != null) {
            if (eventResult.getMessage() == null && personResult.getMessage() == null) {
                DataCache dataCache = null;
                dataCache = DataCache.getDataCache();
                dataCache.importData(personResult.getData(), eventResult.getData());

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
