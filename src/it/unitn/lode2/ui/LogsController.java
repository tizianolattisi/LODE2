package it.unitn.lode2.ui;

import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.ipcam.StreamGobbler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * User: tiziano
 * Date: 09/03/15
 * Time: 11:12
 */
public class LogsController implements Initializable {

    @FXML
    private TextArea stdoutTextArea;

    @FXML
    private TextArea stderrorTextArea;

    private StreamGobbler errorStreamGobbler = new StreamGobbler();
    private StreamGobbler standardStreamGobbler = new StreamGobbler();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void logRecorder(Recorder recorder){
        recorder.errorLog().ifPresent(s -> Platform.runLater(errorStreamGobbler.stream(s)));
        recorder.outputLog().ifPresent(s -> Platform.runLater(standardStreamGobbler.stream(s)));

    }

    public void stop(){
        if( errorStreamGobbler != null && errorStreamGobbler.isAlive() ) {
            errorStreamGobbler.terminate();
        }
        if( standardStreamGobbler != null && standardStreamGobbler.isAlive() ) {
            standardStreamGobbler.terminate();
        }
    }

    public TextArea getStdoutTextArea() {
        return stdoutTextArea;
    }

    public TextArea getStderrorTextArea() {
        return stderrorTextArea;
    }
}
