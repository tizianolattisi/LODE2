package it.unitn.lode2.recorder.ipcam;

import it.unitn.lode2.mapformat.MessageMapFormat;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.RecorderStatus;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * User: tiziano
 * Date: 14/12/14
 * Time: 14:18
 */
public class IPRecorderImpl implements Recorder, EventListener {

    // ffmpeg -i "concat:movie001.ts|movie002.ts|movie003.ts|movie004.ts" -c copy -bsf:a aac_adtstoasc movie0.mp4

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

    final static Logger logger = Logger.getLogger(IPRecorderImpl.class);


    public IPRecorderImpl(String host, Integer port, IPRecorderProtocol protocol, String path, String user, String password, String recordTemplate, String output, String ffmpeg, String isight) {
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
            url += host + path;
        } else {
            url += host + ":" + port + path;
        }

        MessageMapFormat mmp = new MessageMapFormat(recordTemplate);
        Map<String, Object> map = new HashMap();
        map.put("ffmpeg", ffmpeg);
        map.put("isight", isight);
        map.put("input", url);
        this.recordPartialCommand = mmp.format(map);
    }

    @Override
    public void record() throws IOException {
        if( RecorderStatus.IDLE.equals(status) || RecorderStatus.PAUSED.equals(status) ) {
            startProcess();
            status = RecorderStatus.RECORDING;
            logger.debug("Start recording.");
        }
    }

    @Override
    public void stop() {
        if( RecorderStatus.RECORDING.equals(status) || RecorderStatus.PAUSED.equals(status) ) {
            status = RecorderStatus.IDLE;
            stopProcess();
            logger.debug("Stop recording.");
        }
    }

    @Override
    public void pause() {
        if( RecorderStatus.RECORDING.equals(status) ) {
            stopProcess();
            status = RecorderStatus.PAUSED;
            logger.debug("Pause recording.");
        }
    }


    @Override
    public void wakeup() throws IOException {
        if( RecorderStatus.PAUSED.equals(status) ) {
            startProcess();
            status = RecorderStatus.RECORDING;
            logger.debug("Wakeup recording.");
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
        String fileName = "movie" + String.format("%03d", numOfFragments) + ".mov";

        ArrayList<String> recordCommand = new ArrayList();
        Boolean odd = Boolean.TRUE;
        for( String token: recordPartialCommand.split("'") ){
            if( odd ) {
                // outside double quote
                token.split(" ");
                recordCommand.addAll(Arrays.asList(token.split(" ")));
            } else {
                // inside double quote
                recordCommand.add("'" + token +"'");
            }
            odd = !odd;
        }

        //ArrayList<String> recordCommand = new ArrayList(Arrays.asList(recordPartialCommand.split(" ")));
        recordCommand.add(output + "/" + fileName);
        recordProcess = new ProcessBuilder(recordCommand).start();
        (new RecorderObserver(recordProcess, this)).start();
    }


    private void stopProcess() {
        if( recordProcess == null ){
            return;
        }
        BufferedWriter pi = new BufferedWriter(new OutputStreamWriter(recordProcess.getOutputStream()));
        try {
            pi.write("q\n");
            pi.flush();
        } catch (IOException e) {
            logger.error("Unable to quit from ffmpeg process.", e);
        }
        //recordProcess.destroy();
        recordProcess=null;
    }

    public void timeoutHandle() {
        logger.warn("");
        if( RecorderStatus.RECORDING.equals(status) ){
            try {
                status = RecorderStatus.PAUSED;
                record();
            } catch (IOException e) {
                logger.error("Something wrong in restarting record process.", e);
            }
        }
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
