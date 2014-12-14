package it.unitn.lode2.recorder.ipcam;

import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.RecorderStatus;

/**
 * User: tiziano
 * Date: 14/12/14
 * Time: 14:18
 */
public class RecorderIPImpl implements Recorder {

    RecorderStatus status = RecorderStatus.IDLE;

    @Override
    public void record() {
        status = RecorderStatus.RECORDING;
    }

    @Override
    public void stop() {
        status = RecorderStatus.IDLE;
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
