package Models;

import java.util.UUID;

/**
 * Plain old java class modeling an AuthToken in the database
 * The authtoken String is randomly generating using UUID
 */
public class AuthToken {

    private String authToken;
    private String username;

    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public AuthToken(String username) {
        this.username = username;
        authToken = UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oAuthToken = (AuthToken) o;
            return oAuthToken.getAuthToken().equals(getAuthToken()) &&
                    oAuthToken.getUsername().equals(getUsername());
        } else {
            return false;
        }
    }
}
