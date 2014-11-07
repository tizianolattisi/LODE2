package it.unitn.lode2.ui;

import it.unitn.lode2.cam.Camera;
import it.unitn.lode2.cam.ipcam.CameraIPBuilder;
import it.unitn.lode2.cam.ipcam.Cmds;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
                .template(Cmds.PANLEFT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveLeft&usr=${user}&pwd=${password}")
                .template(Cmds.PANRIGHT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveRight&usr=${user}&pwd=${password}")
                .build();

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


    /* Buttons hadling */

    @FXML
    void handleZoomInAction(ActionEvent event) throws IOException {
        camera.zoomIn();
    }

    @FXML
    void handleZoomOutAction(ActionEvent event) throws IOException {
        camera.zoomOut();
    }

    @FXML
    void handlePanLeftAction(ActionEvent event) throws IOException {
        camera.panLeft();
    }

    @FXML
    void handlePanRightAtcion(ActionEvent event) throws IOException {
        camera.panRight();
    }

    @FXML
    void handleRefreshAction(ActionEvent event) {
        refreshPreview();
    }

}
