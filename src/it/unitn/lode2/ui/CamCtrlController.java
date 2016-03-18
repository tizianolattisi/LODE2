package it.unitn.lode2.ui;

import it.unitn.lode2.IOC;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.recorder.Chronometer;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.projector.Projector;
import it.unitn.lode2.recorder.ipcam.StreamGobbler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * User: tiziano
 * Date: 07/11/14
 * Time: 09:39
 */
public class CamCtrlController implements Initializable {

    private Camera camera;
    private Previewer previewer;
    private Recorder recorder=null;
    private Projector projector;
    private Lecture lecture;
    private ImageView slideImageView=null;
    private Rectangle2D slideScreenBounds;
    private Chronometer chronometer = new Chronometer();


    @FXML private ImageView currentSlideImageView;
    @FXML private ImageView preparedSlideImageView;
    @FXML private ImageView next1SlideImageView;
    @FXML private ImageView next2SlideImageView;
    @FXML private ImageView next3SlideImageView;

    @FXML private Button firstSlideButton;
    @FXML private Button prevSlideButton;
    @FXML private Button showSlideButton;
    @FXML private Button nextSlideButton;
    @FXML private Button lastSlideButton;

    @FXML private ImageView previewImageView;
    @FXML private ImageView offair;
    @FXML private ToggleButton previewToggleButton;

    @FXML private Button setupButton;

    @FXML private Button zoomOutButton;
    @FXML private Button zoomInButton;
    @FXML private Button panLeftButton;
    @FXML private Button panRightButton;
    @FXML private Button tiltUpButton;
    @FXML private Button tiltDownButton;

    @FXML private ToggleButton setPreset4ToggleButton;
    @FXML private ToggleButton preset1ToggleButton;
    @FXML private ToggleButton preset2ToggleButton;
    @FXML private ToggleButton preset3ToggleButton;
    @FXML private ToggleButton preset4ToggleButton;

    @FXML private ToggleButton recordToggleButton;
    @FXML private ToggleButton pauseToggleButton;
    @FXML private Button stopButton;

    private List<ToggleButton> toggleButtons;
    private List<ImageView> nextImageViews;

    private Timeline timeline;
    private final SimpleBooleanProperty setPresetMode = new SimpleBooleanProperty();
    private LogsController logsController;

    private StreamGobbler errorStreamGobbler = new StreamGobbler();
    private StreamGobbler standardStreamGobbler = new StreamGobbler();

    private enum ZoomMode {
        NONE, WIDE, NARROW
    }
    private ZoomMode presetZoomMode = ZoomMode.NONE;
    private List<ZoomMode> presetsZoomMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Second screen (to move in Projector)
        ObservableList<Screen> screens = Screen.getScreens();
        if( screens.size()>1 ){
            Screen screen = screens.get(1);
            slideScreenBounds = screen.getBounds();
            slideImageView = new ImageView();
            Pane slidePane = new Pane();
            slidePane.getChildren().add(slideImageView);
            Scene scene = new Scene(slidePane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setX(slideScreenBounds.getMinX());
            stage.setY(slideScreenBounds.getMinY());
            stage.setWidth(slideScreenBounds.getWidth());
            stage.setHeight(slideScreenBounds.getHeight());
            stage.setFullScreen(true);
            stage.toFront();
            stage.show();
        }

        // IOC to inject the implementations of Camera, Recorder, and Projector
        camera = IOC.queryUtility(Camera.class);
        previewer = IOC.queryUtility(Previewer.class);
        recorder = IOC.queryUtility(Recorder.class);
        projector = IOC.queryUtility(Projector.class);
        lecture = IOC.queryUtility(Lecture.class);

        setPresetMode.bindBidirectional(setPreset4ToggleButton.selectedProperty());

        toggleButtons = Arrays.asList(preset1ToggleButton, preset2ToggleButton, preset3ToggleButton, preset4ToggleButton);
        presetsZoomMode = Arrays.asList(ZoomMode.NONE, ZoomMode.NONE, ZoomMode.NONE, ZoomMode.NONE);
        nextImageViews = Arrays.asList(next1SlideImageView, next2SlideImageView, next3SlideImageView);

        configHandlers();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(40), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshPreview();
            }
        }));

        projector.first();
        refreshSlides();
    }

    public void keyBindings() {
        setKeyButtonBinding(KeyCode.UP, showSlideButton);
        setKeyButtonBinding(KeyCode.ENTER, showSlideButton);
        setKeyButtonBinding(KeyCode.LEFT, prevSlideButton);
        setKeyButtonBinding(KeyCode.RIGHT, nextSlideButton);
        setKeyButtonBinding(KeyCode.DIGIT1, preset1ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT2, preset2ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT3, preset3ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT4, preset4ToggleButton);
    }

    private void setKeyButtonBinding(KeyCode code, ButtonBase button){
        button.getScene().getAccelerators().put(new KeyCodeCombination(code), () -> button.fire());
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
        recordToggleButton.setOnAction(handlerRecord);
        pauseToggleButton.setOnAction(handlerPause);
        stopButton.setOnAction(handlerStop);
        preset1ToggleButton.setOnAction(handlerPreset);
        preset2ToggleButton.setOnAction(handlerPreset);
        preset3ToggleButton.setOnAction(handlerPreset);
        preset4ToggleButton.setOnAction(handlerPreset);
        setPreset4ToggleButton.setOnAction(handlerSetPreset);

        firstSlideButton.setOnAction(handlerFirstSlide);
        prevSlideButton.setOnAction(handlerPrevSlide);
        showSlideButton.setOnAction(handlerShowSlide);
        nextSlideButton.setOnAction(handlerNextSlide);
        lastSlideButton.setOnAction(handlerLastSlide);
    }

    /* Preview */
    private void refreshPreview() {
        try {
            previewer.snapshot().ifPresent(s -> previewImageView.setImage(new Image(s)));
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void playPreview() {
        previewer.start();
        timeline.play();
    }

    private void stopPreview() {
        timeline.stop();
        previewer.stop();
        previewImageView.setImage(null);
    }

    private void resetPresetButtons() {
        for( ToggleButton button: toggleButtons ){
            button.setSelected(false);
        }
    }

    /* Slides */
    private void refreshSlides(){
        projector.shownSlide().ifPresent(s -> currentSlideImageView.setImage(s.createPreview(320.0, 200.0)));
        projector.preparedSlide().ifPresent(s -> preparedSlideImageView.setImage(s.createPreview(320.0, 200.0)));
        for( Integer i=0; i<nextImageViews.size(); i++ ) {
            final Integer j=i;
            projector.slideDelta(i+1).ifPresent(s -> nextImageViews.get(j).setImage(s.createPreview(160.0, 100.0)));
            if (!projector.slideDelta(i+1).isPresent()) {
                nextImageViews.get(i).setImage(null);
            }
        }
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
                    if( setPresetMode.getValue() ){
                        presetZoomMode = ZoomMode.NARROW;
                        return;
                    }
                    try {
                        camera.zoomIn();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
            resetPresetButtons();
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
                    if( setPresetMode.getValue() ){
                        presetZoomMode = ZoomMode.WIDE;
                        return;
                    }
                    try {
                        camera.zoomOut();
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            });
            thread.start();
            resetPresetButtons();
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
            resetPresetButtons();
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
            resetPresetButtons();
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
            resetPresetButtons();
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
            resetPresetButtons();
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

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unitn/lode2/ui/logs.fxml"));
                Parent root = loader.load();
                logsController = loader.getController();
                Scene scene = new Scene(root, 600, 700);
                Stage stage = new Stage();
                stage.setTitle("Acquisition logs");
                stage.setScene(scene);
                stage.show();
                //logsController.logRecorder(recorder);
                recorder.errorLog().ifPresent(s -> errorStreamGobbler.stream(s).widget(logsController.getStderrorTextArea()).start());
                recorder.outputLog().ifPresent(s -> standardStreamGobbler.stream(s).widget(logsController.getStdoutTextArea()).start());
                if( recorder.isRecording() ) {
                    //controller.logRecorder(recorder);
                }
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
                    chronometer.reset();
                    chronometer.start();
                    offair.setId("onair");
                    // XXX: clear timed slides?
                } catch (IOException e) {
                    handleIOException(e);
                }
            }
            recordToggleButton.setSelected(true);
        }
    };

    private EventHandler<ActionEvent> handlerPause = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() ){
                recorder.pause();
                chronometer.stop();
                offair.setId("offair");
            } else if( recorder.isPaused() ){
                recorder.wakeup();
                chronometer.start();
                offair.setId("onair");
            }
            else {
                pauseToggleButton.setSelected(false);
            }
        }
    };

    private EventHandler<ActionEvent> handlerStop = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() || recorder.isPaused() ){
                recorder.stop();
                chronometer.stop();
                lecture.setVideoLength(chronometer.elapsed() / 1000);
                lecture.save();
                terminateGobblers();
                //logsController.stop();
                offair.setId("offair");
                recordToggleButton.setSelected(false);
                pauseToggleButton.setSelected(false);
            }
        }
    };

    private void terminateGobblers() {
        if( errorStreamGobbler != null && errorStreamGobbler.isAlive() ) {
            errorStreamGobbler.terminate();
        }
        if( standardStreamGobbler != null && standardStreamGobbler.isAlive() ) {
            standardStreamGobbler.terminate();
        }
    }

    private EventHandler<ActionEvent> handlerPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            ToggleButton pressedButton = (ToggleButton) event.getSource();
            Integer i = 0;
            for( ToggleButton button: toggleButtons ){
                i++;
                button.setSelected(button == pressedButton);
                if( button == pressedButton ) {
                    try {
                        if( setPresetMode.getValue() ){
                            camera.delPreset(i.toString());
                            camera.addPreset(i.toString());
                            presetsZoomMode.set(i-1, presetZoomMode);
                            setPresetMode.setValue(Boolean.FALSE);
                            presetZoomMode = ZoomMode.NONE;
                        } else {
                            camera.goToPreset(i.toString());
                            if( ZoomMode.NARROW.equals(presetsZoomMode.get(i-1)) ){
                                camera.zoomIn();
                            } else if( ZoomMode.WIDE.equals(presetsZoomMode.get(i-1)) ){
                                camera.zoomOut();
                            }
                        }
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            }
        }
    };

    private EventHandler<ActionEvent> handlerSetPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            resetPresetButtons();
            presetZoomMode = ZoomMode.NONE;
        }
    };

    private EventHandler<ActionEvent> handlerFirstSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( !projector.isFirst() ){
                projector.first();
                refreshSlides();
            }
        }
    };
    private EventHandler<ActionEvent> handlerPrevSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            projector.previous();
            refreshSlides();
        }
    };
    private EventHandler<ActionEvent> handlerShowSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            projector.show();
            projector.shownSlide().ifPresent(s -> {
                if( slideImageView != null ) {
                    slideImageView.setImage(s.createPreview(slideScreenBounds.getWidth(), slideScreenBounds.getHeight()));
                }
                if( recorder.isRecording() ) {
                    projector.shownSlideSeqNumber().ifPresent(n -> lecture.addTimedSlide(lecture.slide(n), chronometer.elapsed()/1000));
                }
            });
            refreshSlides();
        }
    };
    private EventHandler<ActionEvent> handlerNextSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            projector.next();
            refreshSlides();
        }
    };
    private EventHandler<ActionEvent> handlerLastSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            projector.last();
            refreshSlides();
        }
    };
    public EventHandler<WindowEvent> handlerClose = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            terminateGobblers();
            previewer.stop();
            recorder.stop();
        }
    };

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }

}
