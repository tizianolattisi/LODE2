package it.unitn.lode2.slide.raster;

import it.unitn.lode2.slide.Slide;
import javafx.scene.image.Image;

import java.net.URL;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 15:19
 */
public class RasterSlideImpl implements Slide {

    private String url;

    public RasterSlideImpl(String url) {
        this.url = url;
    }

    @Override
    public Image createPreview(){
        return new Image(url);
    }
}
