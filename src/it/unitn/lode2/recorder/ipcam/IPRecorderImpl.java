package it.unitn.lode2.recorder.ipcam;

import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.RecorderStatus;

import java.io.IOException;

/**
 * User: tiziano
 * Date: 14/12/14
 * Time: 14:18
 */
public class IPRecorderImpl implements Recorder {

    private static final String FFMPEG_COMMAND = "/Applications/ffmpeg/bin/ffmpeg";

    private Process recordProcess=null;
    RecorderStatus status = RecorderStatus.IDLE;

    private final IPRecorderProtocol protocol;
    private final String host;
    private final Integer port;
    private final String path;
    private final String user;
    private final String password;
    private final String output;

    private String url;

    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path, String user, String password, String output) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        this.user = user;
        this.password = password;
        this.output = output;

        url = protocol.toString().toLowerCase() + "://";
        if( user!=null && password!=null ){
            url += user+ ":" + password + "@";
        }
        url += host + ":" + port + path;
    }
    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path, String user, String password) {
        this(host, port, protocol, path, user, password, null);
    }
    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path) {
        this(host, port, protocol, path, null, null, null);
    }
    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol) {
        this(host, port, protocol, null, null, null, null);
    }
    public IPRecorderImpl(String host, Integer port) {
        this(host, port, null, null, null, null, null);
    }

    @Override
    public void record() throws IOException {
        recordProcess  = new ProcessBuilder(FFMPEG_COMMAND,
                "-i",
                url,
                "-strict",
                "-2",
                "-acodec", "aac",
                "-vcodec", "copy",
                "-b:a", "32k",
                output).start();
        System.out.println("record");
        status = RecorderStatus.RECORDING;
    }

    @Override
    public void stop() {
        if( recordProcess.isAlive() ) {
            recordProcess.destroy();
            recordProcess=null;
            System.out.println("stop");
            status = RecorderStatus.IDLE;
        }
    }

    @Override
    public void pause() {
        status = RecorderStatus.PAUSED;
    }

    @Override
    public void wakeup() {
        if( RecorderStatus.PAUSED.equals(status) ) {
            status = RecorderStatus.RECORDING;
        }
    }

    @Override
    public RecorderStatus status() {
        return status;
    }

    @Override
    public Boolean isIdle() {
        return RecorderStatus.IDLE.equals(status);
    }

    @Override
    public Boolean isRecording() {
        return RecorderStatus.RECORDING.equals(status);
    }

    @Override
    public Boolean isPaused() {
        return RecorderStatus.PAUSED.equals(status);
    }
}
