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

    private String base;
    private String user;
    private String password;
    private Map<Cmds, String> urls = new HashMap<>();


    public static CameraIPBuilder create(){
        return new CameraIPBuilder();
    }

    public CameraIPBuilder base(String base) {
        this.base = base;
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
        String url = mmp.format(map);
        urls.put(cmds, base+url);
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
