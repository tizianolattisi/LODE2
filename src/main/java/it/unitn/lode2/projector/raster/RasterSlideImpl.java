package it.unitn.lode2.projector.raster;

import it.unitn.lode2.projector.AbstractSlide;
import javafx.scene.image.Image;

import java.net.URL;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 15:19
 */
public class RasterSlideImpl extends AbstractSlide {

    private URL url;

    public RasterSlideImpl(URL url) {
        this(url, "", "");
    }

    public RasterSlideImpl(URL url, String title, String description) {
        super(title, description);
        this.url = url;
    }

    @Override
    public Image createPreview(Double width, Double height){
        Image image = new Image("file://" + url.getPath(), width, height, true, true); // XXX: why not from URL?
        return image;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
