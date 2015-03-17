package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.IOC;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by tiziano on 17/03/15.
 */
public class CameraController implements Initializable {

    @FXML private Button zoomOutButton;
    @FXML private Button zoomInButton;
    @FXML private Button panLeftButton;
    @FXML private Button panRightButton;
    @FXML private Button tiltUpButton;
    @FXML private Button tiltDownButton;

    @FXML private ToggleButton setPresetToggleButton;
    @FXML private ToggleButton preset1ToggleButton;
    @FXML private ToggleButton preset2ToggleButton;
    @FXML private ToggleButton preset3ToggleButton;
    @FXML private ToggleButton preset4ToggleButton;

    private Camera camera;

    private final SimpleBooleanProperty setPresetMode = new SimpleBooleanProperty();
    private List<ToggleButton> toggleButtons;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        camera = IOC.queryUtility(Camera.class);
        setPresetMode.bindBidirectional(setPresetToggleButton.selectedProperty());
        toggleButtons = Arrays.asList(preset1ToggleButton, preset2ToggleButton, preset3ToggleButton, preset4ToggleButton);
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
        preset1ToggleButton.setOnAction(handlerPreset);
        preset2ToggleButton.setOnAction(handlerPreset);
        preset3ToggleButton.setOnAction(handlerPreset);
        preset4ToggleButton.setOnAction(handlerPreset);
        setPresetToggleButton.setOnAction(handlerSetPreset);

    }

    private void resetPresetButtons() {
        for( ToggleButton button: toggleButtons ){
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
                    if( setPresetMode.getValue() ){
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
                            setPresetMode.setValue(Boolean.FALSE);
                        } else {
                            camera.goToPreset(i.toString());
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
        }
    };

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }
}
