package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.camera.Previewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 * Created by tiziano on 10/03/15.
 */
public class PreviewerIPImpl implements Previewer {

    private String snapshotUrl;


    @Override
    public Optional<InputStream> snapshot() throws IOException {
        InputStream inputStream = threadSafeSnapshot();
        if( inputStream==null ){
            return Optional.empty();
        }
        return Optional.of(inputStream);
    }

    private synchronized InputStream threadSafeSnapshot() throws IOException {
        URL url = new URL(snapshotUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(500);
        connection.setReadTimeout(500);
        try {
            return connection.getInputStream();
        } catch (SocketTimeoutException ex) {
            return null;
        }
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

}
