package it.unitn.lode2.camera.ipcam.connection;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * Created by tiziano on 29/03/15.
 */
public interface ConnectionProvider {

    HttpURLConnection createConnection(URL url) throws IOException;

}
