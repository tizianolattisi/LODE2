package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.IOC;
import it.unitn.lode2.camera.PreviewMode;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.camera.ipcam.connection.ConnectionProvider;
import it.unitn.lode2.recorder.ipcam.PreviewerThread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by tiziano on 10/03/15.
 */
public class PreviewerIPImpl implements Previewer {

    private String snapshotUrl;
    private PreviewMode mode=PreviewMode.ONDEMAND;
    private PreviewerThread previewerThread;
    private Map<Integer, InputStream> snapshotPreviews = new HashMap<>();


    @Override
    public Optional<InputStream> snapshot() throws IOException {
        InputStream inputStream=null;
        if( PreviewMode.ONDEMAND.equals(mode) ) {
            inputStream = threadSafeSnapshot();
            if (inputStream == null) {
                return Optional.empty();
            }
        } else {
            if( previewerThread == null ){
                return Optional.empty();
            }
            byte[] cache = previewerThread.getCache();
            if( cache == null ) {
                return Optional.empty();
            }
            inputStream = new ByteArrayInputStream(cache);
        }
        return Optional.of(inputStream);
    }

    @Override
    public void setPreviewMode(PreviewMode mode) {
        this.mode = mode;
    }

    @Override
    public void start() {
        if( PreviewMode.CONTINUOUS.equals(mode) && (previewerThread==null || !previewerThread.isAlive()) ) {
            previewerThread = new PreviewerThread(snapshotUrl);
            previewerThread.start();
        }
    }

    @Override
    public void stop() {
        if( previewerThread != null && previewerThread.isAlive() ){
            previewerThread.terminate();
            previewerThread = null;
        }
    }

    private synchronized InputStream threadSafeSnapshot() throws IOException {
        URL url = new URL(snapshotUrl);
        URLConnection connection = IOC.queryUtility(ConnectionProvider.class).createConnection(url);
        connection.setConnectTimeout(500);
        connection.setReadTimeout(500);
        try {
            InputStream inputStream = connection.getInputStream();
            return inputStream;
        } catch (SocketTimeoutException ex) {
            return null;
        }
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    @Override
    public void takeSnapshotPreview(Integer i) throws IOException {
        snapshotPreviews.put(i, threadSafeSnapshot());
    }

    @Override
    public Optional<InputStream> getSnapshotPreview(Integer i) {
        InputStream stream = snapshotPreviews.getOrDefault(i, null);
        if( stream == null ){
            return Optional.empty();
        }
        return Optional.of(stream);
    }
}
