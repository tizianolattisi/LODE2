package it.unitn.lode2.camera.ipcam.connection;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Base64;

/**
 * Created by tiziano on 29/03/15.
 */
public class AbstractConnectionProvider implements ConnectionProvider {

    private AuthMode mode;
    private String username;
    private String password;

    public AbstractConnectionProvider(AuthMode mode, String username, String password) {
        this.mode = mode;
        this.username = username;
        this.password = password;
    }

    @Override
    public HttpURLConnection createConnection(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if( AuthMode.BASIC.equals(mode) ){
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes());
            connection.setRequestProperty ("Authorization", basicAuth);
        } else if( AuthMode.DIGEST.equals(mode) ){
            // TODO: to implement...
        } else if( AuthMode.QUERY.equals(mode) ){
            // TODO: to implement...
        } else {
            // nothig to do
        }
        return connection;
    }
}
