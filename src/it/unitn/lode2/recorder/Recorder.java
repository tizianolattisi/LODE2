package it.unitn.lode2.recorder;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * User: tiziano
 * Date: 14/12/14
 * Time: 14:14
 */
public interface Recorder {

    public void record();

    public void stop();

    public void pause();

    public RecorderStatus status();

    public Boolean isIdle();

    public Boolean isRecording();

    public Boolean isPaused();
}
