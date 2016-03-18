package it.unitn.lode2.ui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * User: tiziano
 * Date: 11/05/15
 * Time: 19:52
 */
public class SecondDisplay extends Stage {

    ImageView slideImageView = new ImageView();
    VBox browser = new VBox();
    ComboBox<String> addresses = new ComboBox<>();
    WebView browserWebView = new WebView();
    private final Pane pane;

    public SecondDisplay(Rectangle2D bounds) {
        setX(bounds.getMinX());
        setY(bounds.getMinY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());

        pane = new Pane();
        Scene scene = new Scene(pane);
        setScene(scene);

        addresses.setItems(FXCollections.observableArrayList("http://www.google.it", "http://www.oracle.com", "http://192.168.2.1"));
        addresses.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setURL(addresses.getSelectionModel().selectedItemProperty().get());
            }
        });
        browser.getChildren().add(addresses);
        browser.getChildren().add(browserWebView);

        switchMode(DisplayMode.PREVIEW);
    }

    public void switchMode(DisplayMode mode){
        pane.getChildren().clear();
        if( DisplayMode.SLIDES.equals(mode) || DisplayMode.PREVIEW.equals(mode) ){
            setFullScreen(true);
            toFront();
            pane.getChildren().add(slideImageView);
        } else if( DisplayMode.DESKTOP.equals(mode) ){
            setFullScreen(false);
            toBack();
        } else if( DisplayMode.BROWSER.equals(mode) ){
            setFullScreen(true);
            toFront();
            pane.getChildren().add(browser);
        }
    }

    public void setImage(Image image){
        slideImageView.setImage(image);
    }

    public void setURL(String url){
        browserWebView.getEngine().load(url);
    }
}
