package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.IOC;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.ui.skin.AwesomeIcons;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by tiziano on 17/03/15.
 */
public class CameraController implements Initializable {

    private Font fontAwesome;

    @FXML private Button zoomOutButton;
    @FXML private Button zoomInButton;
    @FXML private Button panLeftButton;
    @FXML private Button panRightButton;
    @FXML private Button tiltUpButton;
    @FXML private Button tiltDownButton;

    @FXML private Button preset1ToggleButton;
    @FXML private Button preset2ToggleButton;
    @FXML private Button preset3ToggleButton;
    @FXML private Button preset4ToggleButton;

    @FXML private ToggleButton view1ToggleButton;
    @FXML private ToggleButton view2ToggleButton;
    @FXML private ToggleButton view3ToggleButton;
    @FXML private ToggleButton view4ToggleButton;

    private Camera camera;

    private List<Button> presetToggleButtons;
    private List<ToggleButton> viewToggleButtons;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        camera = IOC.queryUtility(Camera.class);
        presetToggleButtons = Arrays.asList(preset1ToggleButton, preset2ToggleButton, preset3ToggleButton, preset4ToggleButton);
        viewToggleButtons = Arrays.asList(view1ToggleButton, view2ToggleButton, view3ToggleButton, view4ToggleButton);
        configHandlers();
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
        preset1ToggleButton.setOnAction(handlerSetPreset);
        preset2ToggleButton.setOnAction(handlerSetPreset);
        preset3ToggleButton.setOnAction(handlerSetPreset);
        preset4ToggleButton.setOnAction(handlerSetPreset);

        view1ToggleButton.setOnAction(handlerViewPreset);
        view2ToggleButton.setOnAction(handlerViewPreset);
        view3ToggleButton.setOnAction(handlerViewPreset);
        view4ToggleButton.setOnAction(handlerViewPreset);

        fontAwesome = Font.loadFont(MainController.class.getResource("/fonts/FontAwesome.otf").
                toExternalForm(), 24);
        setFontAwesome(tiltUpButton, AwesomeIcons.ICON_ARROW_UP, "black");
        setFontAwesome(tiltDownButton, AwesomeIcons.ICON_ARROW_DOWN, "black");
        setFontAwesome(panLeftButton, AwesomeIcons.ICON_UNDO, "black");
        setFontAwesome(panRightButton, AwesomeIcons.ICON_REPEAT, "black");
        setFontAwesome(zoomInButton, AwesomeIcons.ICON_ZOOM_IN, "black");
        setFontAwesome(zoomOutButton, AwesomeIcons.ICON_ZOOM_OUT, "black");
        setFontAwesome(view1ToggleButton, AwesomeIcons.ICON_EYE_OPEN, "black");
        setFontAwesome(view2ToggleButton, AwesomeIcons.ICON_EYE_OPEN, "black");
        setFontAwesome(view3ToggleButton, AwesomeIcons.ICON_EYE_OPEN, "black");
        setFontAwesome(view4ToggleButton, AwesomeIcons.ICON_EYE_OPEN, "black");
        setFontAwesome(preset1ToggleButton, AwesomeIcons.ICON_SAVE, "black");
        setFontAwesome(preset2ToggleButton, AwesomeIcons.ICON_SAVE, "black");
        setFontAwesome(preset3ToggleButton, AwesomeIcons.ICON_SAVE, "black");
        setFontAwesome(preset4ToggleButton, AwesomeIcons.ICON_SAVE, "black");

    }

    private void setFontAwesome(ButtonBase button, String iconName, String color) {
        button.setText(iconName);
        button.setFont(fontAwesome);
        button.setStyle("-fx-padding: 0; -fx-text-fill: " + color + ";");
    }

    private void resetPresetButtons() {
        for( ToggleButton button: viewToggleButtons){
            button.setSelected(false);
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

    private EventHandler<ActionEvent> handlerSetPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Button pressedButton = (Button) event.getSource();
            Integer i = 0;
            for( Button button: presetToggleButtons){
                i++;
                if( button == pressedButton ) {
                    try {
                        camera.delPreset(i.toString());
                        camera.addPreset(i.toString());
                        viewToggleButtons.get(i-1).setSelected(true);
                        Previewer previewer = IOC.queryUtility(Previewer.class);
                        previewer.takeSnapshotPreview(i);
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            }
        }
    };

    private EventHandler<ActionEvent> handlerViewPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            ToggleButton pressedButton = (ToggleButton) event.getSource();
            Integer i = 0;
            for( ToggleButton button: viewToggleButtons){
                i++;
                button.setSelected(button == pressedButton);
                if( button == pressedButton ) {
                    try {
                        camera.goToPreset(i.toString());
                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            }
        }
    };


    /*
    private EventHandler<ActionEvent> handlerSetPreset = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            resetPresetButtons();
        }
    };
    */

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }
}
