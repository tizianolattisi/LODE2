package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.IOC;
import it.unitn.lode2.camera.AbstractCamera;
import it.unitn.lode2.camera.ipcam.connection.ConnectionProvider;
import it.unitn.lode2.mapformat.MessageMapFormat;

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
    private String zoomStopUrl;

    private String panLeftUrl;
    private String panRightUrl;
    private String panStopUrl;

    private String tiltUpUrl;
    private String tiltDownUrl;
    private String tiltStopUrl;

    private String presetUrl;
    private String presetAddUrl;
    private String presetDelUrl;


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

    @Override
    public void zoomStop() throws IOException {
        executeGET(zoomStopUrl);
    }

    @Override
    public void panStop() throws IOException {
        executeGET(panStopUrl);
    }

    @Override
    public void tiltUp() throws IOException {
        executeGET(tiltUpUrl);
    }

    @Override
    public void tiltDown() throws IOException {
        executeGET(tiltDownUrl);
    }

    @Override
    public void tiltStop() throws IOException {
        executeGET(tiltStopUrl);
    }

    @Override
    public void goToPreset(String preset) throws IOException {
        MessageMapFormat mmp = new MessageMapFormat(presetUrl);
        Map<String, Object> map = new HashMap();
        map.put("preset", preset);
        String goToPreset = mmp.format(map);
        executeGET(goToPreset);
    }

    @Override
    public void delPreset(String preset) throws IOException {
        executeGET(presetDelUrl + preset);

    }

    @Override
    public void addPreset(String preset) throws IOException {
        executeGET(presetAddUrl + preset);
    }

    public void setZoomInUrl(String zoomInUrl) {
        this.zoomInUrl = zoomInUrl;
    }

    public void setZoomOutUrl(String zoomOutUrl) {
        this.zoomOutUrl = zoomOutUrl;
    }

    public void setZoomStopUrl(String zoomStopUrl) {
        this.zoomStopUrl = zoomStopUrl;
    }

    public void setPanLeftUrl(String panLeftUrl) {
        this.panLeftUrl = panLeftUrl;
    }

    public void setPanRightUrl(String panRightUrl) {
        this.panRightUrl = panRightUrl;
    }

    public void setPanStopUrl(String panStopUrl) {
        this.panStopUrl = panStopUrl;
    }

    public void setTiltUpUrl(String tiltUpUrl) {
        this.tiltUpUrl = tiltUpUrl;
    }

    public void setTiltDownUrl(String tiltDownUrl) {
        this.tiltDownUrl = tiltDownUrl;
    }

    public void setTiltStopUrl(String tiltStopUrl) {
        this.tiltStopUrl = tiltStopUrl;
    }

    public void setPresetUrl(String presetUrl) {
        this.presetUrl = presetUrl;
    }

    public void setPresetAddUrl(String presetAddUrl) {
        this.presetAddUrl = presetAddUrl;
    }

    public void setPresetDelUrl(String presetDelUrl) {
        this.presetDelUrl = presetDelUrl;
    }

    private void executeGET(String sUrl) throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection connection = IOC.queryUtility(ConnectionProvider.class).createConnection(url);
        connection.setRequestMethod("GET");
        connection.connect();
        connection.getContent();
    }


}
