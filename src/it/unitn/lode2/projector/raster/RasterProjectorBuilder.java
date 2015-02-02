package it.unitn.lode2.projector.raster;

import it.unitn.lode2.projector.Slide;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class RasterProjectorBuilder {

    private List<RasterSlideImpl> slides = new ArrayList<>();


    public static RasterProjectorBuilder create(){
        return new RasterProjectorBuilder();
    }

    public RasterProjectorBuilder slide(RasterSlideImpl slide){
        slides.add(slide);
        return this;
    }

    public RasterProjectorImpl build(){
        RasterProjectorImpl projector = new RasterProjectorImpl();
        projector.addSlides(slides.stream().map(s -> (Slide) s).collect(Collectors.toList()));
        projector.first();
        return projector;
    }

}
