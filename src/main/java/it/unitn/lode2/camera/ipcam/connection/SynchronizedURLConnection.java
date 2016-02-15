package it.unitn.lode2.camera.ipcam.connection;

import it.unitn.lode2.IOC;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tiziano on 15/02/16.
 */
public class SynchronizedURLConnection {
    public static InputStream invoke(String sUrl) throws IOException {
        return invoke(sUrl, false);
    }
    public static synchronized InputStream invoke(String sUrl, Boolean getStream) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection connection = IOC.queryUtility(ConnectionProvider.class).createConnection(url);
        if( getStream ) {
            return connection.getInputStream();
        } else {
            connection.setRequestMethod("GET");
            connection.connect();
            connection.getContent();
            return null;
        }
    }
}
