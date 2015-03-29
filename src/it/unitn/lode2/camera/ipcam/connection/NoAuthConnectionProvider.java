package it.unitn.lode2.camera.ipcam.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tiziano on 29/03/15.
 */
public class NoAuthConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    public NoAuthConnectionProvider() {
        super(AuthMode.NONE);
    }


    @Override
    public HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }
}
