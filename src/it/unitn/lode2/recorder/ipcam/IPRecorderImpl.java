package it.unitn.lode2.recorder.ipcam;

import com.axiastudio.mapformat.MessageMapFormat;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.RecorderStatus;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private final String recordCommand;
    private final String output;

    private String url;
    private StreamGobbler standardStreamGobbler;
    private StreamGobbler errorStreamGobbler;


    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path, String user, String password, String recordTemplate, String output) {
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

        MessageMapFormat mmp = new MessageMapFormat(recordTemplate);
        Map<String, Object> map = new HashMap();
        map.put("ffmpeg", FFMPEG_COMMAND);
        map.put("input", url);
        map.put("output", output);
        this.recordCommand = mmp.format(map);
    }

    @Override
    public void record() throws IOException {
        recordProcess  = new ProcessBuilder(recordCommand.split(" ")).start();
        System.out.println("record");
        status = RecorderStatus.RECORDING;

        standardStreamGobbler = new StreamGobbler(recordProcess.getInputStream());
        errorStreamGobbler = new StreamGobbler(recordProcess.getErrorStream());
        standardStreamGobbler.start();
        errorStreamGobbler.start();

    }

    @Override
    public void stop() {
        if( recordProcess.isAlive() ) {
            BufferedWriter pi = new BufferedWriter(new OutputStreamWriter(recordProcess.getOutputStream()));
            try {
                pi.write("q");
                pi.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            standardStreamGobbler.terminate();
            errorStreamGobbler.terminate();
            while( standardStreamGobbler.isAlive() || errorStreamGobbler.isAlive());
            //recordProcess.destroy();
            recordProcess=null;
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

    @Override
    public Optional<InputStream> outputLog() {
        if( recordProcess.isAlive() ) {
            return Optional.of(recordProcess.getInputStream());
        }
        return Optional.empty();
    }

    @Override
    public Optional<InputStream> errorLog() {
        if( recordProcess.isAlive() ) {
            return Optional.of(recordProcess.getErrorStream());
        }
        return Optional.empty();

    }
}
