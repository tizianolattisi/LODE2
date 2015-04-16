package it.unitn.lode2.recorder.ipcam;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;

import java.io.*;

/**
 * User: tiziano
 * Date: 09/03/15
 * Time: 11:03
 */
public class StreamGobbler extends Thread {

    InputStream inputStream;
    TextInputControl textInput = null;
    Boolean toTerminate = Boolean.FALSE;

    public StreamGobbler() {

    }

    public StreamGobbler stream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public StreamGobbler widget(TextInputControl textArea) {
        this.textInput = textArea;
        return this;
    }

    @Override
    public void run() {
        assert Platform.isFxApplicationThread()==false;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while( !toTerminate && (line = br.readLine()) != null ) {
                if( textInput != null ){
                    if( textInput instanceof TextArea) {
                        final String line2 = line;
                        Platform.runLater(() -> textInput.appendText(line2 + "\n"));
                    }
                } else {
                    System.out.println(line);
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void terminate() {
        toTerminate = Boolean.TRUE;
    }
}
