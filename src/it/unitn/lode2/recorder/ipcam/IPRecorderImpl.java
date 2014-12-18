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

    public IPRecorderImpl() {
    }

    @Override
    public void record() throws IOException {
        recordProcess  = new ProcessBuilder(FFMPEG_COMMAND,
                "-i",
                "rtsp://admin:admin@192.168.1.143:88/videoMain",
                "-strict",
                "-2",
                "-acodec", "aac",
                "-vcodec", "copy",
                "-b:a", "32k",
                "movie0.mp4").start();
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
