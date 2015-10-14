package it.unitn.lode2.recorder.ipcam;

import it.unitn.lode2.recorder.VolumeChecker;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: tiziano
 * Date: 09/03/15
 * Time: 11:03
 */
public class IPRecorderVolumeCheckerImpl extends Thread implements VolumeChecker {

    InputStream inputStream;
    DoubleProperty lufs;
    Boolean toTerminate = Boolean.FALSE;

    final static Logger logger = Logger.getLogger(IPRecorderVolumeCheckerImpl.class);

    public IPRecorderVolumeCheckerImpl() {

    }

    @Override
    public void setStream(InputStream stream) {
        inputStream = stream;
    }

    @Override
    public void setLufsProperty(DoubleProperty lufsProperty) {
        lufs = lufsProperty;
    }

    @Override
    public void run() {
        assert Platform.isFxApplicationThread()==false;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while( !toTerminate && (line = br.readLine()) != null ) {
                // [Parsed_ebur128_0 @ 0x7fe36bc1a140] t: 52.0867    M: -66.6 S: -63.5     I: -24.7 LUFS     LRA:  21.0 LU
                if( line.startsWith("[Parsed_ebur128") ){
                    try {
                        int start = line.indexOf("M:") + 2;
                        int end = line.indexOf("S:", start);
                        String substring = line.substring(start, end);
                        Double m = Double.parseDouble(substring);
                        Platform.runLater(() -> lufs.setValue(m));
                    } catch (StringIndexOutOfBoundsException ex) {
                        logger.error("Unable to parse ebur128 line.");
                    }
                    //System.out.println(line);
                } else if( line.startsWith("frame=") ) {
                    // do noting
                } else if( line.startsWith("[mpegts") ) {
                    // do noting
                } else {
                    logger.debug(line);
                }
                //final String fLine = line;
                //Platform.runLater(() -> System.out.println(fLine+"\n"));
            }
            logger.debug("Stream gobbler terminated.");
        }
        catch (IOException ioe) {
            logger.error("Unable to read from ffmpeg output.");
        }
    }

    @Override
    public void terminate() {
        toTerminate = Boolean.TRUE;
    }

}
