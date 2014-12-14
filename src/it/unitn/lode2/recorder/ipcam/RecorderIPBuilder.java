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

    public static RecorderIPBuilder create(){
        return new RecorderIPBuilder();
    }

    public RecorderIPImpl build(){
        RecorderIPImpl recorder = new RecorderIPImpl();
        return recorder;
    }
}
