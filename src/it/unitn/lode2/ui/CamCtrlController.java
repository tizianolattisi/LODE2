package it.unitn.lode2.ui;

import it.unitn.lode2.cam.Camera;
import it.unitn.lode2.cam.Capability;
import it.unitn.lode2.cam.ipcam.CameraIPBuilder;
import it.unitn.lode2.cam.ipcam.Cmds;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
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

    // parametri da passare in configurazione
    private final String HOST = "192.168.1.143";
    private final int PORT = 88;

    Camera camera;

    Recorder recorder=null;

    @FXML
    private ImageView previewImageView;

    @FXML
    private ImageView vumeterImageView;

    @FXML
    private ToggleButton previewToggleButton;

    @FXML
    private Button setupButton;

    @FXML
    private Button zoomOutButton;

    @FXML
    private Button zoomInButton;

    @FXML
    private Button panLeftButton;

    @FXML
    private Button panRightButton;

    @FXML
    private Button tiltUpButton;

    @FXML
    private Button tiltDownButton;

    @FXML
    private Button recordButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    private Timeline timeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // TODO: use IOC to inject the implementations of Camera and Recorder

        camera = CameraIPBuilder.create()
                .user("admin")
                .password("admin")
                .host(HOST)
                .port(PORT)

                .template(Cmds.ZOOMIN, "/cgi-bin/CGIProxy.fcgi?cmd=zoomIn&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMOUT, "/cgi-bin/CGIProxy.fcgi?cmd=zoomOut&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=zoomStop&usr=${user}&pwd=${password}")

                .template(Cmds.PANLEFT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveLeft&usr=${user}&pwd=${password}")
                .template(Cmds.PANRIGHT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveRight&usr=${user}&pwd=${password}")
                .template(Cmds.PANSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzStopRun&usr=${user}&pwd=${password}")

                .template(Cmds.TILTUP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveUp&usr=${user}&pwd=${password}")
                .template(Cmds.TILTDOWN, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveDown&usr=${user}&pwd=${password}")
                .template(Cmds.TILTSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzStopRun&usr=${user}&pwd=${password}")

                .template(Cmds.SNAPSHOT, "/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=${user}&pwd=${password}")

                .build();

        recorder = IPRecorderBuilder.create()
                .protocol(IPRecorderProtocol.RTSP)
                .host(HOST)
                .port(PORT)
                .build();


        configHandlers();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshPreview();
            }
        }));
        //timeline.play();
    }

    private void configHandlers() {
        if( camera.hasCapability(Capability.ZOOM) ){
            if( camera.hasCapability(Capability.ZOOMSTOP) ){
                zoomInButton.setOnMousePressed(handlerZoomIn);
                zoomOutButton.setOnMousePressed(handlerZoomOut);
                zoomInButton.setOnMouseReleased(handlerZoomStop);
                zoomOutButton.setOnMouseReleased(handlerZoomStop);
            } else {
                zoomInButton.setOnAction(handlerZoomInAction);
                zoomOutButton.setOnAction(handlerZoomOutAction);
            }
        }
        if( camera.hasCapability(Capability.PAN) ){
            if( camera.hasCapability(Capability.PANSTOP) ){
                panLeftButton.setOnMousePressed(handlerPanLeft);
                panLeftButton.setOnMouseReleased(handlerPanStop);
                panRightButton.setOnMousePressed(handlerPanRight);
                panRightButton.setOnMouseReleased(handlerPanStop);
            } else {
                panLeftButton.setOnAction(handlerPanLeftAction);
                panRightButton.setOnAction(handlerPanRightAction);
            }
        }
        if( camera.hasCapability(Capability.TILT) ){
            if( camera.hasCapability(Capability.TILTSTOP) ){
                tiltUpButton.setOnMousePressed(handlerTiltUp);
                tiltUpButton.setOnMouseReleased(handlerTiltStop);
                tiltDownButton.setOnMousePressed(handlerTiltDown);
                tiltDownButton.setOnMouseReleased(handlerTiltStop);
            } else {
                tiltUpButton.setOnAction(handlerTiltUpAction);
                tiltDownButton.setOnAction(handlerTiltDownAction);
            }
        }
        previewToggleButton.setOnAction(handlerPreview);
        setupButton.setOnAction(handlerSetup);
        recordButton.setOnAction(handlerRecord);
        pauseButton.setOnAction(handlerPause);
        stopButton.setOnAction(handlerStop);
    }

    private void refreshPreview() {
        try {
            previewImageView.setImage(new Image(camera.snapshot()));
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void playPreview() {
        timeline.play();
    }

    private void stopPreview() {
        timeline.stop();
        previewImageView.setImage(null);
    }


    /* Event handling */

    private EventHandler<ActionEvent> handlerZoomInAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerZoomIn.handle(event);
        }
    };
    private EventHandler<Event> handlerZoomIn = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.zoomIn();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };
    private EventHandler<ActionEvent> handlerZoomOutAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerZoomOut.handle(event);
        }
    };
    private EventHandler<Event> handlerZoomOut = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.zoomOut();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };
    private EventHandler<Event> handlerZoomStop = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.zoomStop();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };

    private EventHandler<ActionEvent> handlerPanLeftAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerPanLeft.handle(event);
        }
    };
    private EventHandler<Event> handlerPanLeft = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.panLeft();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };
    private EventHandler<ActionEvent> handlerPanRightAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerPanRight.handle(event);
        }
    };
    private EventHandler<Event> handlerPanRight = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.panRight();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };
    private EventHandler<Event> handlerPanStop = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.panStop();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };

    private EventHandler<ActionEvent> handlerTiltUpAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerTiltUp.handle(event);
        }
    };
    private EventHandler<Event> handlerTiltUp = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.tiltUp();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();

        }
    };
    private EventHandler<ActionEvent> handlerTiltDownAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            handlerTiltDown.handle(event);
        }
    };
    private EventHandler<Event> handlerTiltDown = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera.tiltDown();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
        }
    };
    private EventHandler<Event> handlerTiltStop = new EventHandler<Event>() {
        @Override
        public void handle(Event event){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    camera.tiltStop();
                } catch (IOException e) {
                    handleIOException(e);
                }
            }
        });
        thread.start();
        }
    };

    private EventHandler<ActionEvent> handlerPreview = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( previewToggleButton.isSelected() ){
                playPreview();
            } else {
                stopPreview();
            }
        }
    };

    private EventHandler<ActionEvent> handlerSetup = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/it/unitn/lode2/ui/camsetup.fxml"));
                Scene scene = new Scene(root, 600, 700);
                Stage stage = new Stage();
                stage.setTitle("Camera config");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private EventHandler<ActionEvent> handlerRecord = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isIdle() || recorder.isPaused() ) {
                try {
                    recorder.record();
                } catch (IOException e) {
                    handleIOException(e);
                }
            }
        }
    };

    private EventHandler<ActionEvent> handlerPause = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() ){
                recorder.pause();
            }
        }
    };

    private EventHandler<ActionEvent> handlerStop = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() ){
                recorder.stop();
            }
        }
    };

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }

}
