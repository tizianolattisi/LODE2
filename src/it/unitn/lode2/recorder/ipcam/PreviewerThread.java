package it.unitn.lode2.recorder.ipcam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by tiziano on 10/03/15.
 */
public class PreviewerThread extends Thread {

    private Boolean toTerminate = Boolean.FALSE;
    private String snapshotUrl;
    private Integer frameRate=5;
    private Integer maxMillisecs=1000/frameRate;
    private byte[] cache;

    public PreviewerThread(String url) {
        this.snapshotUrl = url;
    }

    @Override
    public void run() {
        while( !toTerminate ){
            try {
                long initTime = System.currentTimeMillis();
                URL url = new URL(snapshotUrl);
                URLConnection connection = url.openConnection();
                cachePreview(connection.getInputStream());
                long toWait = maxMillisecs - (System.currentTimeMillis() - initTime);
                if( toWait>0 ) {
                    sleep(toWait);
                }
            } catch (IOException ex) {

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void cachePreview(InputStream is){
        int nRead;
        byte[] data = new byte[16384];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            while( (nRead = is.read(data, 0, data.length) ) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache = buffer.toByteArray();
    }

    public void terminate() {
        toTerminate = Boolean.TRUE;
    }

    public byte[] getCache() {
        return cache;
    }
}
