package it.unitn.lode2.slide.raster;

import it.unitn.lode2.slide.AbstractSlide;
import it.unitn.lode2.slide.Slide;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 15:19
 */
public class RasterSlideImpl extends AbstractSlide {

    private String url;

    public RasterSlideImpl(String url) {
        this(url, "", "", new ArrayList<>());
    }

    public RasterSlideImpl(String url, String title, String description, List<String> tags) {
        super(title, description, tags);
        this.url = url;
    }

    @Override
    public Image createPreview(Double width, Double height){
        Image image = new Image(url, width, height, true, true);
        return image;
    }

    @Override
    public String getUrl() {
        return url;
    }


}
