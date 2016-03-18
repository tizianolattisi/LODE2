package it.unitn.lode2.projector;

import javafx.scene.image.Image;

import java.net.URL;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 15:19
 */
public interface Slide {

    Image createPreview(Double width, Double height);

    String getTitle();

    String getDescription();

    URL getUrl();

}
