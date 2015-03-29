package it.unitn.lode2.camera.ipcam.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Created by tiziano on 29/03/15.
 */
public class BasicConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    private String basicAuth;

    public BasicConnectionProvider(String username, String password) {
        super(AuthMode.BASIC);
        String userpass = username + ":" + password;
        basicAuth = "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes());
    }

    @Override
    public HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty ("Authorization", basicAuth);
        return connection;
    }
}
