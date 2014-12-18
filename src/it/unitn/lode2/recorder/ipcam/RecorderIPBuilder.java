package it.unitn.lode2.recorder.ipcam;

import com.axiastudio.mapformat.MessageMapFormat;
import it.unitn.lode2.cam.Capability;
import it.unitn.lode2.cam.ipcam.CameraIPImpl;
import it.unitn.lode2.cam.ipcam.Cmds;
import it.unitn.lode2.recorder.Recorder;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class RecorderIPBuilder {

    private String host="127.0.0.1";

    private Integer port=80;

    private IPRecorderProtocol protocol=IPRecorderProtocol.HTTP;

    public static RecorderIPBuilder create(){
        return new RecorderIPBuilder();
    }

    public RecorderIPBuilder host(String host) {
        this.host = host;
        return this;
    }

    public RecorderIPBuilder port(Integer port) {
        this.port = port;
        return this;
    }

    public RecorderIPBuilder protocol(IPRecorderProtocol protocol){
        this.protocol = protocol;
        return this;
    }

    public RecorderIPImpl build(){
        RecorderIPImpl recorder = new RecorderIPImpl();
        return recorder;
    }
}
