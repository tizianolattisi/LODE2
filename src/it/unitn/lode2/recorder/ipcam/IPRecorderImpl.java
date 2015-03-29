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

    // ffmpeg -i "concat:movie001.ts|movie002.ts|movie003.ts|movie004.ts" -c copy -bsf:a aac_adtstoasc movie0.mp4

    private static final String FFMPEG = "/Applications/ffmpeg/bin/ffmpeg";
    //private static final String CONCAT_COMMAND = FFMPEG + " -i \"concat:${ts}\" -c copy -bsf:a aac_adtstoasc movie0.mp4";

    private Process recordProcess=null;
    RecorderStatus status = RecorderStatus.IDLE;

    private final IPRecorderProtocol protocol;
    private final String host;
    private final Integer port;
    private final String path;
    private final String user;
    private final String password;
    private final String recordPartialCommand;
    private final String output;

    private String url;
    private Integer numOfFragments=0;


    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path, String user, String password, String recordTemplate, String output) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        this.user = user;
        this.password = password;
        // TODO: uri to escape
        this.output = output;

        url = protocol.toString().toLowerCase() + "://";
        if( user!=null && password!=null ){
            url += user+ ":" + password + "@";
        }
        if( "".equals(port) || "80".equals(port) ) {
            url += host + ":" + port + path;
        } else {
            url += host + path;
        }

        MessageMapFormat mmp = new MessageMapFormat(recordTemplate);
        Map<String, Object> map = new HashMap();
        map.put("ffmpeg", FFMPEG);
        map.put("input", url);
        map.put("output", "${output}");
        this.recordPartialCommand = mmp.format(map);
    }

    @Override
    public void record() throws IOException {
        if( RecorderStatus.IDLE.equals(status) || RecorderStatus.PAUSED.equals(status) ) {
            startProcess();
            status = RecorderStatus.RECORDING;
        }
    }

    @Override
    public void stop() {
        if( RecorderStatus.RECORDING.equals(status) || RecorderStatus.PAUSED.equals(status) ) {
            stopProcess();
            status = RecorderStatus.IDLE;
        }
    }

    @Override
    public void pause() {
        if( RecorderStatus.RECORDING.equals(status) ) {
            stopProcess();
            status = RecorderStatus.PAUSED;
        }
    }


    @Override
    public void wakeup() throws IOException {
        if( RecorderStatus.PAUSED.equals(status) ) {
            startProcess();
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


    private void startProcess() throws IOException {
        // fragment file name
        numOfFragments++;
        String fileName = "movie" + String.format("%03d", numOfFragments) + ".ts";
        MessageMapFormat mmp = new MessageMapFormat(recordPartialCommand);
        Map<String, Object> map = new HashMap();
        map.put("output", output + "/" + fileName);
        String recordCommand = mmp.format(map);
        recordProcess = new ProcessBuilder(recordCommand.split(" ")).start();
    }


    private void stopProcess() {
        BufferedWriter pi = new BufferedWriter(new OutputStreamWriter(recordProcess.getOutputStream()));
        try {
            pi.write("q");
            pi.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //recordProcess.destroy();
        recordProcess=null;
    }


    /*
    private void finalizeMovie() {
        String ts=null;
        for( Integer i=1; i<=numOfFragments; i++ ){
            if( ts==null ){
                ts = "";
            } else {
                ts = ts + "|";
            }
            ts = ts + "movie" + String.format("%03d", i) + ".ts";
        }
        MessageMapFormat mmp = new MessageMapFormat(CONCAT_COMMAND);
        Map<String, Object> map = new HashMap();
        map.put("ts", ts);
        String command = mmp.format(map);
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
