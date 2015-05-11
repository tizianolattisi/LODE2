package it.unitn.lode2.ui.controllers;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * User: tiziano
 * Date: 11/05/15
 * Time: 19:52
 */
public class SecondDisplay extends Stage {

    ImageView slideImageView = new ImageView();
    private final Pane pane;

    public SecondDisplay(Rectangle2D bounds) {
        setX(bounds.getMinX());
        setY(bounds.getMinY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());
        setFullScreen(true);
        toFront();

        pane = new Pane();
        Scene scene = new Scene(pane);
        setScene(scene);
    }

    public void switchMode(DisplayMode mode){
        if( DisplayMode.SLIDES.equals(mode) ){
            pane.getChildren().clear();
            pane.getChildren().add(slideImageView);
        } else if( DisplayMode.DESKTOP.equals(mode) ){

        } else if( DisplayMode.BROWSER.equals(mode) ){

        } else if( DisplayMode.PREVIEW.equals(mode) ){

        }
    }

    public void setImage(Image image){
        slideImageView.setImage(image);
    }


}
