package it.unitn.lode2.cam.ipcam;

import it.unitn.lode2.cam.AbstractCamera;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:02
 */
public class CameraIPImpl extends AbstractCamera {

    private String zoomInUrl;
    private String zoomOutUrl;

    private String panLeftUrl;
    private String panRightUrl;

    @Override
    public void zoomIn() throws IOException {
        executeGET(zoomInUrl);
    }

    @Override
    public void zoomOut() throws IOException {
        executeGET(zoomOutUrl);
    }

    @Override
    public void panLeft() throws IOException {
        executeGET(panLeftUrl);
    }

    @Override
    public void panRight() throws IOException {
        executeGET(panRightUrl);
    }

    public void setZoomInUrl(String zoomInUrl) {
        this.zoomInUrl = zoomInUrl;
    }

    public void setZoomOutUrl(String zoomOutUrl) {
        this.zoomOutUrl = zoomOutUrl;
    }

    public void setPanLeftUrl(String panLeftUrl) {
        this.panLeftUrl = panLeftUrl;
    }

    public void setPanRightUrl(String panRightUrl) {
        this.panRightUrl = panRightUrl;
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
