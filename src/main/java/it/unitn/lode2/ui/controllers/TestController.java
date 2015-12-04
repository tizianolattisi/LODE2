package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.Constants;
import it.unitn.lode2.IOC;
import it.unitn.lode2.asset.LodePrefs;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.camera.ipcam.PreviewerIPBuilder;
import it.unitn.lode2.camera.ipcam.connection.*;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.VolumeChecker;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.recorder.ipcam.IPRecorderVolumeCheckerImpl;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.ipcam.XMLCameraIPConf;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tiziano on 04/12/15.
 */
public class TestController implements Initializable {

    private Previewer previewer;
    private Timeline timeline;
    private Recorder recorder;
    private VolumeChecker volumeChecker;
    DoubleProperty lufs = new SimpleDoubleProperty();

    @FXML
    private ProgressBar vuMeterProgressBar;

    @FXML private ImageView previewImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Creo un previewer.
        // Nota: non posso richiederlo con IOC.queryUtility(Previewer.class), perché non esiste ancora
        LodePrefs lodePrefs = IOC.queryUtility(LodePrefs.class);
        XMLCameraIPConf cameraIPConf = XMLHelper.build(XMLCameraIPConf.class).unmarshal(new File(Constants.CAMERA_CONF));
        ConnectionProvider connProvider;
        if(AuthMode.BASIC.equals(cameraIPConf.getAuthMode()) ) {
            connProvider = new BasicConnectionProvider(lodePrefs.getUser(), lodePrefs.getPassword());
        } else if(AuthMode.QUERY.equals(cameraIPConf.getAuthMode()) ) {
            connProvider = new QueryConnectionProvider(lodePrefs.getUser(), lodePrefs.getPassword(), cameraIPConf.getAuthQuery());
        } else {
            connProvider = new NoAuthConnectionProvider();
        }
        IOC.registerUtility(connProvider, ConnectionProvider.class);
        IPRecorderBuilder recorderBuilder = IPRecorderBuilder.create();
        if( cameraIPConf.getStreamProtocol()!=null ){
            recorderBuilder = recorderBuilder.protocol(IPRecorderProtocol.valueOf(cameraIPConf.getStreamProtocol()));
        }
        if( cameraIPConf.getStreamPort()!=null ){
            recorderBuilder = recorderBuilder.port(cameraIPConf.getStreamPort());
        }
        //.output(lectureFolder + "/movie0.mp4")
        recorder = recorderBuilder
                .host(lodePrefs.getHost())
                .url(cameraIPConf.getStreamUrl())
                .user(lodePrefs.getUser())
                .password(lodePrefs.getPassword())
                .recordCommand(cameraIPConf.getRecordCommand())
                //.output(lectureFolder + "/movie0.mp4")
                .output("/Users/tiziano") // XXX
                .isight(lodePrefs.getISightRecorderPath())
                .ffmpeg(lodePrefs.getFfmpegPath())
                .build();
        previewer = PreviewerIPBuilder.create()
                .user(lodePrefs.getUser())
                .password(lodePrefs.getPassword())
                .host(lodePrefs.getHost())
                .port(cameraIPConf.getCgiPort())
                .snapshotUrl(cameraIPConf.getSnapshot())
                .build();

        vuMeterProgressBar.progressProperty().bind(lufs.divide(74.0).add(1.0));

        // costruisco una timeline che faccia un refresh ogni 40 millisecondi
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(40), event -> refreshPreview()));

        // avvio preview e timeline
        try {
            recorder.record();
        } catch (IOException e) {
            // XXX: close?
        }
        recorder.errorLog().ifPresent(s -> {
            volumeChecker = new IPRecorderVolumeCheckerImpl();
            volumeChecker.setStream(s);
            volumeChecker.setLufsProperty(lufs);
            volumeChecker.start();
        });
        previewer.start();
        timeline.play();

    }

    private void refreshPreview() {
        try {
            previewer.snapshot().ifPresent(s -> previewImageView.setImage(new Image(s)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EventHandler<WindowEvent> handlerClose = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            volumeChecker.terminate();
            previewer.stop();
            recorder.stop();
        }
    };

}
