package it.unitn.lode2.camera.ipcam;

import com.axiastudio.mapformat.MessageMapFormat;
import it.unitn.lode2.camera.Capability;
import it.unitn.lode2.camera.PreviewMode;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class PreviewerIPBuilder {

    private String host = "127.0.0.1";
    private Integer port = 80;
    private String user = "admin";
    private String password = "";
    private String snapshotUrl;
    private Map<Cmds, String> templates = new HashMap<>();


    public static PreviewerIPBuilder create(){
        return new PreviewerIPBuilder();
    }

    public PreviewerIPBuilder host(String host) {
        this.host= host;
        return this;
    }

    public PreviewerIPBuilder port(Integer port) {
        this.port= port;
        return this;
    }

    public PreviewerIPBuilder user(String user) {
        this.user = user;
        return this;
    }

    public PreviewerIPBuilder password(String password) {
        this.password = password;
        return this;
    }

    public PreviewerIPBuilder snapshotUrl(String url) {
        this.snapshotUrl = url;
        return this;
    }

    public PreviewerIPImpl build(){
        MessageMapFormat mmp = new MessageMapFormat(snapshotUrl);
        Map<String, Object> map = new HashMap();
        map.put("user", user);
        map.put("password", password);
        String relativeUrl = mmp.format(map);
        String url = "http://" + host + ":" + port + relativeUrl;
        PreviewerIPImpl previewerIP = new PreviewerIPImpl();
        previewerIP.setSnapshotUrl(url);
        previewerIP.setPreviewMode(PreviewMode.CONTINUOUS); // XXX: parametrize...

        return previewerIP;
    }
}
