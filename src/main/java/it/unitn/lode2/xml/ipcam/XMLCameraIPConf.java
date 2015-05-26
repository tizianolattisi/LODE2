package it.unitn.lode2.xml.ipcam;

import it.unitn.lode2.camera.ipcam.connection.AuthMode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 02/02/15
 * Time: 10:53
 */
@XmlRootElement(name = "CAMERA_IP")
public class XMLCameraIPConf {

    private AuthMode authMode;
    private String authQuery;

    private Integer cgiPort;
    private Integer streamPort;
    private String streamProtocol;
    private String streamUrl;

    private String zoomIn;
    private String zoomOut;
    private String zoomStop;

    private String panLeft;
    private String panRight;
    private String panStop;

    private String tiltUp;
    private String tiltDown;
    private String tiltStop;

    private String preset;
    private String delPreset;
    private String addPreset;

    private String snapshot;
    private String recordCommand;
    private String convCommand;

    @XmlElement(name = "AUTH")
    public AuthMode getAuthMode() {
        return authMode;
    }

    @XmlElement(name = "AUTHQUERY")
    public String getAuthQuery() {
        return authQuery;
    }

    @XmlElement(name = "CGI_PORT")
    public Integer getCgiPort() {
        return cgiPort;
    }

    @XmlElement(name = "STREAM_PORT")
    public Integer getStreamPort() {
        return streamPort;
    }

    @XmlElement(name = "STREAM_PROTOCOL")
    public String getStreamProtocol() {
        return streamProtocol;
    }

    @XmlElement(name = "STREAM_URL")
    public String getStreamUrl() {
        return streamUrl;
    }

    @XmlElement(name = "ZOOMIN")
    public String getZoomIn() {
        return zoomIn;
    }

    @XmlElement(name = "ZOOMOUT")
    public String getZoomOut() {
        return zoomOut;
    }

    @XmlElement(name = "ZOOMSTOP")
    public String getZoomStop() {
        return zoomStop;
    }

    @XmlElement(name = "PANLEFT")
    public String getPanLeft() {
        return panLeft;
    }

    @XmlElement(name = "PANRIGHT")
    public String getPanRight() {
        return panRight;
    }

    @XmlElement(name = "PANSTOP")
    public String getPanStop() {
        return panStop;
    }

    @XmlElement(name = "TILTUP")
    public String getTiltUp() {
        return tiltUp;
    }

    @XmlElement(name = "TILTDOWN")
    public String getTiltDown() {
        return tiltDown;
    }

    @XmlElement(name = "TILTSTOP")
    public String getTiltStop() {
        return tiltStop;
    }

    @XmlElement(name = "PRESET")
    public String getPreset() {
        return preset;
    }

    @XmlElement(name = "DELPRESET")
    public String getDelPreset() {
        return delPreset;
    }

    @XmlElement(name = "ADDPRESET")
    public String getAddPreset() {
        return addPreset;
    }

    @XmlElement(name = "SNAPSHOT")
    public String getSnapshot() {
        return snapshot;
    }

    @XmlElement(name = "RECORD_COMMAND")
    public String getRecordCommand() {
        return recordCommand;
    }

    @XmlElement(name = "CONV_COMMAND")
    public String getConvCommand() {
        return convCommand;
    }

    public void setConvCommand(String convCommand) {
        this.convCommand = convCommand;
    }

    public void setCgiPort(Integer cgiPort) {
        this.cgiPort = cgiPort;
    }

    public void setStreamPort(Integer streamPort) {
        this.streamPort = streamPort;
    }

    public void setStreamProtocol(String streamProtocol) {
        this.streamProtocol = streamProtocol;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public void setZoomIn(String zoomIn) {
        this.zoomIn = zoomIn;
    }

    public void setZoomOut(String zoomOut) {
        this.zoomOut = zoomOut;
    }

    public void setZoomStop(String zoomStop) {
        this.zoomStop = zoomStop;
    }

    public void setPanLeft(String panLeft) {
        this.panLeft = panLeft;
    }

    public void setPanRight(String panRight) {
        this.panRight = panRight;
    }

    public void setPanStop(String panStop) {
        this.panStop = panStop;
    }

    public void setTiltUp(String tiltUp) {
        this.tiltUp = tiltUp;
    }

    public void setTiltDown(String tiltDown) {
        this.tiltDown = tiltDown;
    }

    public void setTiltStop(String tiltStop) {
        this.tiltStop = tiltStop;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public void setDelPreset(String delPreset) {
        this.delPreset = delPreset;
    }

    public void setAddPreset(String addPreset) {
        this.addPreset = addPreset;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public void setRecordCommand(String recordCommand) {
        this.recordCommand = recordCommand;
    }

    public void setAuthMode(AuthMode authMode) {
        this.authMode = authMode;
    }

    public void setAuthQuery(String authQuery) {
        this.authQuery = authQuery;
    }
}
