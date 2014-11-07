package it.unitn.cam.ipcam;

import it.unitn.cam.AbstractCamera;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:02
 */
public class CameraIPImpl extends AbstractCamera {

    private String zoomInUrl;
    private String zoomOutUrl;

    @Override
    public void zoomIn() throws IOException {
        executeGET(zoomInUrl);
    }

    @Override
    public void zoomOut() throws IOException {
        executeGET(zoomOutUrl);
    }

    public void setZoomInUrl(String zoomInUrl) {
        this.zoomInUrl = zoomInUrl;
    }

    public void setZoomOutUrl(String zoomOutUrl) {
        this.zoomOutUrl = zoomOutUrl;
    }

    private void executeGET(String sUrl) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        InputStream inputStream = connection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
    }


}
