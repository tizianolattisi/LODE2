package it.unitn.lode2.ui.controllers;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * User: tiziano
 * Date: 11/05/15
 * Time: 19:52
 */
public class SecondDisplay extends Stage {

    ImageView slideImageView = new ImageView();
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
            pane.getChildren().add(browserWebView);
        }
    }

    public void setImage(Image image){
        slideImageView.setImage(image);
    }

    public void setURL(String url){
        browserWebView.getEngine().load(url);
    }
}
