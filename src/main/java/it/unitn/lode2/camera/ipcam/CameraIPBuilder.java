package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.mapformat.MessageMapFormat;
import it.unitn.lode2.camera.Capability;
import it.unitn.lode2.camera.ipcam.connection.AuthMode;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class CameraIPBuilder {

    private String host = "127.0.0.1";
    private Integer port = 80;
    private String user = "admin";
    private String password = "";
    private AuthMode authMode = AuthMode.NONE;
    private Map<Cmds, String> templates = new HashMap<>();


    public static CameraIPBuilder create(){
        return new CameraIPBuilder();
    }

    public CameraIPBuilder host(String host) {
        this.host= host;
        return this;
    }

    public CameraIPBuilder port(Integer port) {
        this.port= port;
        return this;
    }

    public CameraIPBuilder user(String user) {
        this.user = user;
        return this;
    }

    public CameraIPBuilder password(String password) {
        this.password = password;
        return this;
    }

    public CameraIPBuilder authMode(AuthMode authMode) {
        this.authMode = authMode;
        return this;
    }

    public CameraIPBuilder template(Cmds cmds, String template) {
        templates.put(cmds, template);
        return this;
    }

    public CameraIPImpl build(){
        Map<Cmds, String> urls = new HashMap<>();
        for( Cmds cmd: templates.keySet() ) {
            String template = templates.get(cmd);
            MessageMapFormat mmp = new MessageMapFormat(template);
            Map<String, Object> map = new HashMap();
            map.put("user", user);
            map.put("password", password);
            map.put("preset", "${preset}");
            String relativeUrl = mmp.format(map);
            String url = "http://" + host + ":" + port + relativeUrl;
            urls.put(cmd, url);
        }
        CameraIPImpl cameraIP = new CameraIPImpl();
        // ZOOM
        if( urls.containsKey(Cmds.ZOOMIN) && urls.containsKey(Cmds.ZOOMOUT) ){
            cameraIP.setZoomInUrl(urls.get(Cmds.ZOOMIN));
            cameraIP.setZoomOutUrl(urls.get(Cmds.ZOOMOUT));
            cameraIP.addCapability(Capability.ZOOM);
        }
        if( urls.containsKey(Cmds.ZOOMSTOP) ){
            cameraIP.setZoomStopUrl(urls.get(Cmds.ZOOMSTOP));
            cameraIP.addCapability(Capability.ZOOMSTOP);
        }
        // PAN
        if( urls.containsKey(Cmds.PANLEFT) && urls.containsKey(Cmds.PANRIGHT) ){
            cameraIP.setPanLeftUrl(urls.get(Cmds.PANLEFT));
            cameraIP.setPanRightUrl(urls.get(Cmds.PANRIGHT));
            cameraIP.addCapability(Capability.PAN);
        }
        if( urls.containsKey(Cmds.PANSTOP) ){
            cameraIP.setPanStopUrl(urls.get(Cmds.PANSTOP));
            cameraIP.addCapability(Capability.PANSTOP);
        }
        // TILT
        if( urls.containsKey(Cmds.TILTUP) && urls.containsKey(Cmds.TILTDOWN) ){
            cameraIP.setTiltUpUrl(urls.get(Cmds.TILTUP));
            cameraIP.setTiltDownUrl(urls.get(Cmds.TILTDOWN));
            cameraIP.addCapability(Capability.TILT);
        }
        if( urls.containsKey(Cmds.TILTSTOP) ){
            cameraIP.setTiltStopUrl(urls.get(Cmds.TILTSTOP));
            cameraIP.addCapability(Capability.TILTSTOP);
        }
        // PRESETS
        if( urls.containsKey(Cmds.PRESET) ){
            cameraIP.setPresetUrl(urls.get(Cmds.PRESET));
            cameraIP.setPresetAddUrl(urls.get(Cmds.ADDPRESET));
            cameraIP.setPresetDelUrl(urls.get(Cmds.DELPRESET));
            cameraIP.addCapability(Capability.PRESETS);
        }
        return cameraIP;
    }
}
