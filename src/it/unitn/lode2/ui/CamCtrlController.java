package it.unitn.lode2.ui;

import it.unitn.lode2.cam.Camera;
import it.unitn.lode2.cam.ipcam.CameraIPBuilder;
import it.unitn.lode2.cam.ipcam.Cmds;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * User: tiziano
 * Date: 07/11/14
 * Time: 09:39
 */
public class CamCtrlController implements Initializable {

    Camera camera;

    @FXML
    private ImageView previewImageView;

    @FXML
    private Button zoomOutButton;

    @FXML
    private Button zoomInButton;

    @FXML
    private Button panLeftButton;

    @FXML
    private Button panRightButton;


    private static final String SNAPSHOTURL = "http://192.168.1.142:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=admin&pwd=admin";

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        camera = CameraIPBuilder.create()
                .user("admin")
                .password("admin")
                .host("192.168.1.142")
                .port(88)
                .template(Cmds.ZOOMIN, "/cgi-bin/CGIProxy.fcgi?cmd=zoomIn&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMOUT, "/cgi-bin/CGIProxy.fcgi?cmd=zoomOut&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=zoomStop&usr=${user}&pwd=${password}")
                .template(Cmds.PANLEFT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveLeft&usr=${user}&pwd=${password}")
                .template(Cmds.PANRIGHT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveRight&usr=${user}&pwd=${password}")
                .template(Cmds.PANSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzStopRun&usr=${user}&pwd=${password}")
                .build();

        zoomInButton.setOnMousePressed(handlerZoomIn);
        zoomInButton.setOnMouseReleased(handlerZoomStop);
        zoomOutButton.setOnMousePressed(handlerZoomOut);
        zoomOutButton.setOnMouseReleased(handlerZoomStop);

        panLeftButton.setOnMousePressed(handlerPanLeft);
        panLeftButton.setOnMouseReleased(handlerPanStop);
        panRightButton.setOnMousePressed(handlerPanRight);
        panRightButton.setOnMouseReleased(handlerPanStop);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshPreview();
            }
        }));
        timeline.play();
    }

    private void refreshPreview() {
        previewImageView.setImage(new Image(SNAPSHOTURL));
    }


    /* Event handling */

    private EventHandler<Event> handlerZoomIn = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.zoomIn();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };
    private EventHandler<Event> handlerZoomOut = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.zoomOut();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };
    private EventHandler<Event> handlerZoomStop = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.zoomStop();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };

    private EventHandler<Event> handlerPanLeft = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.panLeft();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };
    private EventHandler<Event> handlerPanRight = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.panRight();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };
    private EventHandler<Event> handlerPanStop = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            try {
                camera.panStop();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    };

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }


    @FXML
    void handleRefreshAction(ActionEvent event) {
        refreshPreview();
    }

}
