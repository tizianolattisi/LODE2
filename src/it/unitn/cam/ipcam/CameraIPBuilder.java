package it.unitn.cam.ipcam;

import com.axiastudio.mapformat.MessageMapFormat;
import it.unitn.cam.Capability;

import java.util.GregorianCalendar;
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
    private Map<Cmds, String> urls = new HashMap<>();


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


    public CameraIPBuilder template(Cmds cmds, String template) {
        MessageMapFormat mmp = new MessageMapFormat(template);
        Map<String, Object> map = new HashMap();
        map.put("user", user);
        map.put("password", password);
        String cmd = mmp.format(map);
        String url = "http://" + host + ":" + port + cmd;
        urls.put(cmds, url);
        return this;
    }

    public CameraIPImpl build(){
        CameraIPImpl cameraIP = new CameraIPImpl();
        if( urls.containsKey(Cmds.ZOOMIN) && urls.containsKey(Cmds.ZOOMOUT) ){
            cameraIP.setZoomInUrl(urls.get(Cmds.ZOOMIN));
            cameraIP.setZoomOutUrl(urls.get(Cmds.ZOOMOUT));
            cameraIP.addCapability(Capability.ZOOM);
        }
        return cameraIP;
    }
}
