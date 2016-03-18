package it.unitn.lode2.recorder;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.unitn.lode2.ui.controllers.RecordController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * User: tiziano
 * Date: 14/12/14
 * Time: 14:14
 */
public interface Recorder {

    void record() throws IOException;

    void stop();

    void pause();

    void wakeup() throws IOException;

    void setRecordController(RecordController recordController);

    RecorderStatus status();

    Boolean isIdle();

    Boolean isRecording();

    Boolean isPaused();

    Optional<InputStream> outputLog();

    Optional<InputStream> errorLog();
}
