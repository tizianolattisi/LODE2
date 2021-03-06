package it.unitn.lode2.ui.controllers;

import com.sun.jna.Memory;
import it.unitn.lode2.IOC;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
import it.unitn.lode2.camera.ipcam.LODEMediaPlayerComponent;
import it.unitn.lode2.projector.Slide;
import it.unitn.lode2.recorder.Chronometer;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.projector.Projector;
import it.unitn.lode2.recorder.VolumeChecker;
import it.unitn.lode2.remote.Remote;
import it.unitn.lode2.remote.RemoteCommand;
import it.unitn.lode2.ui.skin.AwesomeIcons;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * User: tiziano
 * Date: 07/11/14
 * Time: 09:39
 */
public class MainController implements Initializable, RecordController {

    private Boolean end=Boolean.FALSE;

    private Font fontAwesome;

    private final String RECORDING_COLOR = "red";
    private final String PAUSE_COLOR = "yellow";
    private final String IDLE_COLOR = "black";
    private final String RECORD_LABEL = "REC";
    private final String PAUSE_LABEL = "PAUSE";
    private final String IDLE_LABEL = "";

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;


    private Camera camera;
    //private Previewer previewer;
    private Recorder recorder=null;
    private Projector projector;
    private Remote remote;
    private Lecture lecture;
    private Chronometer chronometer = new Chronometer();

    @FXML private ProgressBar vuMeterProgressBar;
    @FXML private Label lufsLabel;

    @FXML private ImageView currentSlideImageView;
    @FXML private ImageView preparedSlideImageView;
    @FXML private ImageView next1SlideImageView;
    @FXML private ImageView next2SlideImageView;

    @FXML private Button firstSlideButton;
    @FXML private Button prevSlideButton;
    @FXML private Button showSlideButton;
    @FXML private Button nextSlideButton;
    @FXML private Button lastSlideButton;
    @FXML private Button goToSlideButton;
    @FXML private TextField goToSlideTextField;

    @FXML private Label currentSlideLabel;
    @FXML private Label preparedSlideLabel;
    @FXML private Label next1SlideLabel;
    @FXML private Label next2SlideLabel;

    @FXML private Pane previewPane;
    @FXML private ToggleButton previewToggleButton;

    @FXML private Button setupButton;

    @FXML private Button setupSceneButton;
    @FXML private ToggleButton preset1ToggleButton;
    @FXML private ToggleButton preset2ToggleButton;
    @FXML private ToggleButton preset3ToggleButton;
    @FXML private ToggleButton preset4ToggleButton;



    @FXML private ChoiceBox<DisplayMode> scene1ModeChoiceBox;
    @FXML private ChoiceBox<DisplayMode> scene2ModeChoiceBox;
    @FXML private ChoiceBox<DisplayMode> scene3ModeChoiceBox;
    @FXML private ChoiceBox<DisplayMode> scene4ModeChoiceBox;

    @FXML private CheckBox preset1zoomCheckBox;
    @FXML private CheckBox preset2zoomCheckBox;
    @FXML private CheckBox preset3zoomCheckBox;
    @FXML private CheckBox preset4zoomCheckBox;

    @FXML private ToggleButton recordToggleButton;
    @FXML private ToggleButton pauseToggleButton;
    @FXML private Button stopButton;

    private List<ToggleButton> presetsToggleButtons;
    private List<ChoiceBox> sceneModeChoiceBoxes;
    private List<CheckBox> presetsZoomCheckBoxes;
    private List<ImageView> nextImageViews;
    private List<Label> nextLabels;

    private Timeline timeline;

    @FXML private Label recordingLabel;

    private VolumeChecker volumeChecker;

    DisplayMode displayMode = DisplayMode.SLIDES;
    private SecondDisplay secondDisplay = null;
    private Rectangle2D secondScreeBounds;

    // SimpleBooleanProperty per gestire la disabilitazione dei pulsanti
    DoubleProperty lufs = new SimpleDoubleProperty();
    DoubleProperty vuMeter = new SimpleDoubleProperty();
    private Integer bindingCode;
    private String token;
    private Canvas canvas;
    private LODEMediaPlayerComponent mediaPlayerComponent;
    private PixelWriter pixelWriter;
    private WritablePixelFormat<ByteBuffer> byteBgraInstance;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Second screen (to move in Projector)
        ObservableList<Screen> screens = Screen.getScreens();
        if( screens.size()>1 ){
            Screen screen = screens.get(1);
            secondScreeBounds = screen.getBounds();
            secondDisplay = new SecondDisplay(secondScreeBounds);
            secondDisplay.switchMode(DisplayMode.SLIDES);
            secondDisplay.show();
        }

        //
        currentSlideImageView.setOnMouseClicked(handleSlideClick);
        preparedSlideImageView.setOnMouseClicked(handleSlideClick);
        next1SlideImageView.setOnMouseClicked(handleSlideClick);
        next2SlideImageView.setOnMouseClicked(handleSlideClick);

        // IOC to inject the implementations of Camera, Recorder, and Projector
        camera = IOC.queryUtility(Camera.class);
        recorder = IOC.queryUtility(Recorder.class);
        recorder.setRecordController(this);
        projector = IOC.queryUtility(Projector.class);
        lecture = IOC.queryUtility(Lecture.class);

        presetsToggleButtons = Arrays.asList(preset1ToggleButton, preset2ToggleButton, preset3ToggleButton, preset4ToggleButton);
        sceneModeChoiceBoxes = Arrays.asList(scene1ModeChoiceBox, scene2ModeChoiceBox, scene3ModeChoiceBox, scene4ModeChoiceBox);
        presetsZoomCheckBoxes = Arrays.asList(preset1zoomCheckBox, preset2zoomCheckBox, preset3zoomCheckBox, preset4zoomCheckBox);
        nextImageViews = Arrays.asList(next1SlideImageView, next2SlideImageView);
        nextLabels = Arrays.asList(next1SlideLabel, next2SlideLabel);

        //
        ObservableList<DisplayMode> displayModeObservableList = FXCollections.observableArrayList(DisplayMode.SLIDES,
                DisplayMode.PREVIEW,
                DisplayMode.BROWSER,
                DisplayMode.DESKTOP);
        for( ChoiceBox<DisplayMode> choiceBox: sceneModeChoiceBoxes ){
            choiceBox.setItems(displayModeObservableList);
            choiceBox.getSelectionModel().select(0);
        }

        configHandlers();

        // vu meter
        lufsLabel.textProperty().bind(lufs.asString());
        vuMeterProgressBar.progressProperty().bind(lufs.divide(74.0).add(1.0));
        lufsLabel.backgroundProperty().bind(
                Bindings.when(lufs.lessThan(-73.0))
                        .then(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)))
                        .otherwise(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY))));

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(40), event -> renderFrame()));

        //recordToggleButton.disableProperty().bind((new SimpleBooleanProperty(recorder.isIdle())).not());

        projector.first();
        refreshSlides();
        refreshRecorderButtons();

        // remote controller
         remote = IOC.queryUtility(Remote.class);

        // login
        remote.setCommandHandler(RemoteCommand.LOGIN, s -> {
            Integer code = Integer.parseInt(s);
            // TODO: verifica
            if( code.equals(bindingCode) ) {
                token = remote.initializeToken();
                return token + "\n";
            } else {
                return "NO\n";
            }
        });

        // slides
        remote.setCommandHandler(RemoteCommand.GETNSLIDES, () -> {
            return projector.getSlidesNumber().toString() + "\n";
        });
        remote.setCommandHandler(RemoteCommand.NEXT, () -> {
            Platform.runLater(() -> {
                handlerShowSlide.handle(null);
            });
            if( projector.preparedSlideSeqNumber().isPresent() ){
                return projector.preparedSlideSeqNumber().get().toString() + "\n";
            } else {
                return "NO\n";
            }
        });
        remote.setCommandHandler(RemoteCommand.PREVIOUS, () -> {
            Platform.runLater(() -> {
                handlerPrevSlide.handle(null);
                handlerPrevSlide.handle(null);
                handlerShowSlide.handle(null);
            });
            if (projector.preparedSlideSeqNumber().isPresent()) {
                Integer n = projector.preparedSlideSeqNumber().get();
                n -= 2;
                return (n.toString() + "\n");
            } else {
                return "NO\n";
            }
        });
        remote.setCommandHandler(RemoteCommand.SHOW, s -> {
            int n = Integer.parseInt(s);
            projector.goTo(n);
            Platform.runLater(() -> {
                handlerShowSlide.handle(null);
            });
            return s;
        });
        remote.setCommandByteHandler(RemoteCommand.GETSLIDE, s -> {
            int n = Integer.parseInt(s);
            Slide slide = projector.slideNr(n).get();
            Image image = slide.createPreview(800.0, 600.0);

            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(bImage, "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = baos.toByteArray();
            return bytes;
        });
        remote.setCommandByteHandler(RemoteCommand.GETPRESET, s -> {
            int n = Integer.parseInt(s);
            ImageView image = (ImageView) presetsToggleButtons.get(n-1).getGraphic();
            if( image != null ) {
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image.getImage(), null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    return baos.toByteArray();
                } catch (IOException e) {

                }
            }
            return "NO\n".getBytes();
        });

        // capabilities
        Function<Capability, String> f = c -> camera.hasCapability(c).toString().toUpperCase()+"\n";
        remote.setCommandHandler(RemoteCommand.CANPAN, () -> f.apply(Capability.PAN) );
        remote.setCommandHandler(RemoteCommand.CANTILT, () -> f.apply(Capability.TILT));
        remote.setCommandHandler(RemoteCommand.CANZOOM, () -> f.apply(Capability.ZOOM));
        remote.setCommandHandler(RemoteCommand.CANPRESET, () -> f.apply(Capability.PRESETS));

        // recorder
        remote.setCommandHandler(RemoteCommand.RECORD, handlerRecord);
        remote.setCommandHandler(RemoteCommand.PAUSE, handlerPause);
        remote.setCommandHandler(RemoteCommand.STOP, handlerStop);

        // camera
        remote.setCommandHandler(RemoteCommand.GETNPRESETS, () -> {
            return "4\n";
        });
        remote.setCommandHandler(RemoteCommand.PRESET, s -> {
            Integer preset = Integer.parseInt(s);
            ToggleButton button = presetsToggleButtons.get(preset - 1);
            button.fire();
            return "OK\n";
        });

        remote.start();

        fontAwesome = Font.loadFont(MainController.class.getResource("/fonts/FontAwesome.otf").
                toExternalForm(), 24);
        setFontAwesome(previewToggleButton, AwesomeIcons.ICON_VIDEO_CAMERA, "black");
        setFontAwesome(setupSceneButton, AwesomeIcons.ICON_COGS, "black");
        setFontAwesome(setupButton, AwesomeIcons.ICON_INFO_SIGN, "black");

        // recorder
        setFontAwesome(recordToggleButton, AwesomeIcons.ICON_CIRCLE, "red");
        setFontAwesome(pauseToggleButton, AwesomeIcons.ICON_PAUSE, "yellow");
        setFontAwesome(stopButton, AwesomeIcons.ICON_STOP, "blue");

        // slide
        setFontAwesome(firstSlideButton, AwesomeIcons.ICON_FAST_BACKWARD, "black");
        setFontAwesome(prevSlideButton, AwesomeIcons.ICON_BACKWARD, "black");
        setFontAwesome(nextSlideButton, AwesomeIcons.ICON_FORWARD, "black");
        setFontAwesome(lastSlideButton, AwesomeIcons.ICON_FAST_FORWARD, "black");

        setFontAwesome(showSlideButton, AwesomeIcons.ICON_CARET_UP, "black");
        setFontAwesome(goToSlideButton, AwesomeIcons.ICON_SIGNIN, "black");

        // code popup
        bindingCode = ThreadLocalRandom.current().nextInt(1111, 9999);
        Alert codeBox = new Alert(Alert.AlertType.INFORMATION);
        codeBox.setTitle("Remote control bind code");
        codeBox.setHeaderText("Remote control binding code: " + bindingCode);
        codeBox.setContentText("If you want to use a remote control, please use the above code in the binding request.");
        codeBox.showAndWait();

        // preview
        canvas = new Canvas();
        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        byteBgraInstance = PixelFormat.getByteBgraInstance();
        previewPane.getChildren().add(canvas);
        mediaPlayerComponent = new LODEMediaPlayerComponent(canvas, WIDTH, HEIGHT);
        mediaPlayerComponent.getMediaPlayer().playMedia(recorder.getUrl());
        mediaPlayerComponent.getMediaPlayer().setPosition(0.7f);

    }

    private void setFontAwesome(ButtonBase button, String iconName, String color) {
        button.setText(iconName);
        button.setFont(fontAwesome);
        button.setStyle("-fx-padding: 0; -fx-text-fill: " + color + ";");
    }

    private Boolean hasSecondDisplay(){
        return secondDisplay != null;
        //return slideImageView!=null;
    }

    public void keyBindings() {

        setKeyButtonBinding(KeyCode.UP, showSlideButton);
        setKeyButtonBinding(KeyCode.ENTER, showSlideButton);
        setKeyButtonBinding(KeyCode.LEFT, prevSlideButton);
        setKeyButtonBinding(KeyCode.RIGHT, nextSlideButton);

        // keypad
        setKeyButtonBinding(KeyCode.KP_UP, showSlideButton);
        setKeyButtonBinding(KeyCode.KP_LEFT, prevSlideButton);
        setKeyButtonBinding(KeyCode.KP_RIGHT, nextSlideButton);

        // asdw
        setKeyButtonBinding(KeyCode.W, showSlideButton);
        setKeyButtonBinding(KeyCode.A, prevSlideButton);
        setKeyButtonBinding(KeyCode.D, nextSlideButton);

        setKeyButtonBinding(KeyCode.DIGIT1, preset1ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT2, preset2ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT3, preset3ToggleButton);
        setKeyButtonBinding(KeyCode.DIGIT4, preset4ToggleButton);
    }

    private void setKeyButtonBinding(KeyCode code, ButtonBase button){
        button.getScene().getAccelerators().put(new KeyCodeCombination(code), () -> button.fire());
    }

    private void configHandlers() {

        previewToggleButton.setOnAction(handlerPreview);
        recordToggleButton.setOnAction(handlerRecord);
        pauseToggleButton.setOnAction(handlerPause);
        stopButton.setOnAction(handlerStop);
        preset1ToggleButton.setOnAction(handlerPreset);
        preset2ToggleButton.setOnAction(handlerPreset);
        preset3ToggleButton.setOnAction(handlerPreset);
        preset4ToggleButton.setOnAction(handlerPreset);
        setupSceneButton.setOnAction(handlerSetupScene);

        firstSlideButton.setOnAction(handlerFirstSlide);
        prevSlideButton.setOnAction(handlerPrevSlide);
        showSlideButton.setOnAction(handlerShowSlide);
        nextSlideButton.setOnAction(handlerNextSlide);
        lastSlideButton.setOnAction(handlerLastSlide);
        goToSlideButton.setOnAction(handlerGoToSlide);
    }


    private void playPreview() {
        timeline.play();
    }

    private void stopPreview() {
        timeline.stop();
    }

    private void resetPresetButtons() {
        for( ToggleButton button: presetsToggleButtons){
            button.setSelected(false);
        }
    }

    private void setSlideLabel(Label label, Integer n){
        if( n==-1 ){
            label.setText("--");
        } else {
            label.setText(n.toString());
        }
    }

    /* Slides */
    private void refreshSlides(){

        // XXX: questo è veramente brutto...
        currentSlideLabel.setText("--");
        preparedSlideLabel.setText("--");

        projector.shownSlide().ifPresent(s -> {
            currentSlideImageView.setImage(s.createPreview(400.0, 300.0));
            projector.showSlideNumber(s).ifPresent(n -> currentSlideLabel.setText(n.toString()));
        });
        projector.preparedSlide().ifPresent(s -> {preparedSlideImageView.setImage(s.createPreview(400.0, 300.0));
            projector.showSlideNumber(s).ifPresent(n -> preparedSlideLabel.setText(n.toString()));});
        for( Integer i=0; i<nextImageViews.size(); i++ ) {
            final Integer j=i;
            nextLabels.get(j).setText("--");
            projector.slideDelta(i+1).ifPresent(s -> {nextImageViews.get(j).setImage(s.createPreview(160.0, 120.0));
                projector.showSlideNumber(s).ifPresent(n -> nextLabels.get(j).setText(n.toString()));});
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

    private EventHandler<ActionEvent> handlerRecord = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isIdle() || recorder.isPaused() ) {
                try {
                    recorder.record();
                    chronometer.reset();
                    chronometer.start();

                    projector.shownSlideSeqNumber().ifPresent(n -> lecture.addTimedSlide(lecture.slide(n), 0L));

                    // ffmpeg volumeChecker
                    startVolumeChecker();

                    recordingLabel.setText(RECORD_LABEL);
                    recordingLabel.setTextFill(Paint.valueOf(RECORDING_COLOR));
                    // XXX: clear timed slides?
                } catch (IOException e) {
                    handleIOException(e);
                }
            }
            recordToggleButton.setSelected(true);
            refreshRecorderButtons();
        }
    };

    private void refreshRecorderButtons() {
        Recorder recorder = IOC.queryUtility(Recorder.class);
        recordToggleButton.setDisable(end || !recorder.isIdle());
        pauseToggleButton.setDisable(end || recorder.isIdle());
        stopButton.setDisable(end || recorder.isIdle());
    }

    private EventHandler<ActionEvent> handlerPause = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() ){
                recorder.pause();
                chronometer.stop();
                recordingLabel.setText(PAUSE_LABEL);
                recordingLabel.setTextFill(Paint.valueOf(PAUSE_COLOR));
            } else if( recorder.isPaused() ){
                try {
                    recorder.wakeup();
                    recordingLabel.setText(RECORD_LABEL);
                    recordingLabel.setTextFill(Paint.valueOf(RECORDING_COLOR));
                } catch (IOException e) {
                    handleIOException(e);
                }
                chronometer.start();
                startVolumeChecker();
            }
            else {
                pauseToggleButton.setSelected(false);
            }
            refreshRecorderButtons();
        }
    };

    private EventHandler<ActionEvent> handlerStop = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if( recorder.isRecording() || recorder.isPaused() ){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm stop");
                alert.setHeaderText("Do you really want to stop the recording session?");
                alert.setContentText("The current recording session will end, and the window will be closed.\n\n");
                Optional<ButtonType> result = alert.showAndWait();
                if( ButtonType.OK.equals(result.get()) ) {
                    recorder.stop();
                    chronometer.stop();
                    long length = chronometer.elapsed() / 1000;
                    lecture.setVideoLength(length);
                    lecture.save();
                    //terminateGobblers();
                    //logsController.stop();
                    recordingLabel.setText(IDLE_LABEL);
                    recordingLabel.setTextFill(Paint.valueOf(IDLE_COLOR));
                    recordToggleButton.setSelected(false);
                    pauseToggleButton.setSelected(false);
                    //volumeChecker.terminate();
                    end = Boolean.TRUE;
                }
            }
            refreshRecorderButtons();
        }
    };

    /*
    private void terminateGobblers() {
        if( errorStreamGobbler != null && errorStreamGobbler.isAlive() ) {
            errorStreamGobbler.terminate();
        }
        if( standardStreamGobbler != null && standardStreamGobbler.isAlive() ) {
            standardStreamGobbler.terminate();
        }
    }*/

    private EventHandler<ActionEvent> handlerPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            ToggleButton pressedButton = (ToggleButton) event.getSource();
            Integer i = 0;
            for( ToggleButton button: presetsToggleButtons ){
                i++;
                button.setSelected(button == pressedButton);
                if( button == pressedButton ) {
                    if( pressedButton.getText()!=null ) {
                        // TODO: catch preview image
                    }
                    try {
                        if( hasSecondDisplay() ) {
                            if (sceneModeChoiceBoxes.get(i - 1).getSelectionModel().selectedItemProperty().getValue().equals(DisplayMode.SLIDES)) {
                                displayMode = DisplayMode.SLIDES;
                                secondDisplay.switchMode(DisplayMode.SLIDES);
                                projector.shownSlide().ifPresent(s -> secondDisplay.setImage(s.createPreview(secondScreeBounds.getWidth(), secondScreeBounds.getHeight())));
                            } else if (sceneModeChoiceBoxes.get(i - 1).getSelectionModel().selectedItemProperty().getValue().equals(DisplayMode.PREVIEW)) {
                                displayMode = DisplayMode.PREVIEW;
                                secondDisplay.switchMode(DisplayMode.PREVIEW);
                            } else if (sceneModeChoiceBoxes.get(i - 1).getSelectionModel().selectedItemProperty().getValue().equals(DisplayMode.DESKTOP)) {
                                displayMode = DisplayMode.DESKTOP;
                                secondDisplay.switchMode(DisplayMode.DESKTOP);
                            } else if (sceneModeChoiceBoxes.get(i - 1).getSelectionModel().selectedItemProperty().getValue().equals(DisplayMode.BROWSER)) {
                                displayMode = DisplayMode.BROWSER;
                                secondDisplay.switchMode(DisplayMode.BROWSER);
                                secondDisplay.setURL("http://www.google.it");
                            }
                        }
                        camera.goToPreset(i.toString());
                        if( presetsZoomCheckBoxes.get(i-1).isSelected() ){
                            camera.zoomIn();
                        } else if( !presetsZoomCheckBoxes.get(i-1).isSelected() ){
                            camera.zoomOut();
                        } else if (presetsZoomCheckBoxes.get(i-1).isIndeterminate() ){
                            // NOP
                        }
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            }
        }
    };

    private EventHandler<ActionEvent> handlerSetupScene = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/camera.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 340, 560);
                Stage stage = new Stage();
                stage.setTitle("Camera controller");
                stage.setScene(scene);
                stage.setX(100);
                stage.setY(100);
                CameraController controller = loader.getController();
                stage.setOnCloseRequest(controller.handlerClose);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                if (secondDisplay != null) {
                    secondDisplay.setImage(s.createPreview(secondScreeBounds.getWidth(), secondScreeBounds.getHeight()));
                }
                if (recorder.isRecording()) {
                    projector.shownSlideSeqNumber().ifPresent(n -> lecture.addTimedSlide(lecture.slide(n), chronometer.elapsed() / 1000));
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
    private EventHandler<ActionEvent> handlerGoToSlide = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Integer i = Integer.parseInt(goToSlideTextField.getText());
            projector.goTo(i);
            refreshSlides();
        }
    };
    public EventHandler<WindowEvent> handlerClose = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm exit");
            alert.setHeaderText("Do you really want to exit?");
            if( recorder.isRecording() ) {
                alert.setContentText("The current recording session will end.\n\n");
            }
            Optional<ButtonType> result = alert.showAndWait();
            if( ButtonType.OK.equals(result.get()) ) {
                //terminateGobblers();
                remote.stop();
                mediaPlayerComponent.getMediaPlayer().stop();
                mediaPlayerComponent.getMediaPlayer().release();
                if( secondDisplay!=null ){
                    secondDisplay.dispose();
                }
                //previewer.stop();
                if( recorder.isRecording() ){
                    recorder.stop();
                    chronometer.stop();
                    lecture.setVideoLength(chronometer.elapsed() / 1000);
                    lecture.save();
                }
            } else {
                event.consume();
            }
        }
    };
    private EventHandler<MouseEvent> handleSlideClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            ImageView slideImageView = (ImageView) event.getSource();
            Optional<Slide> slide = Optional.empty();
            if( slideImageView.equals(currentSlideImageView) ){
                slide = projector.shownSlide();
            } else if( slideImageView.equals(preparedSlideImageView) ){
                slide = projector.preparedSlide();
            } else if( slideImageView.equals(next1SlideImageView) ){
                slide = projector.slideDelta(1);
            } else if( slideImageView.equals(next2SlideImageView) ){
                slide = projector.slideDelta(2);
            }
            slide.ifPresent(s -> {
                Image preview = s.createPreview(800.0, 600.0);
                ImageView magnifier = new ImageView(preview);
                Stage stage = new Stage();
                Scene scene = new Scene(new Pane(magnifier), 800, 600);
                stage.setScene(scene);
                stage.show();
            });
        }
    };


    private void handleIOException(IOException e) {
        // TODO
        e.printStackTrace();
    }

    @Override
    public void timeoutHandler() {
        try {
            chronometer.stop();
            recorder.stop();
            recorder.record();
            startVolumeChecker();
            chronometer.start();
        } catch (IOException e) {
            handleIOException(e);
            }
    }

    private void startVolumeChecker() {
        recorder.errorLog().ifPresent(s -> {
            volumeChecker = IOC.queryUtility(VolumeChecker.class);
            volumeChecker.setStream(s);
            volumeChecker.setLufsProperty(lufs);
            volumeChecker.start();
        });
    }


    protected final void renderFrame() {
        Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            // FIXME there may be more efficient ways to do this...
            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
            // invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), byteBgraInstance, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayerComponent.getMediaPlayer().unlock();
    }

}
