package it.unitn.lode2.slide.raster;

import it.unitn.lode2.recorder.ipcam.IPRecorderImpl;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.slide.Projector;
import it.unitn.lode2.slide.Slide;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class RasterProjectorBuilder {

    private RasterProjectorBuilder projector;
    private String slidesPath=null;


    public static RasterProjectorBuilder create(){
        return new RasterProjectorBuilder();
    }

    public RasterProjectorBuilder slidesPath(String path) {
        slidesPath = path;
        return this;
    }


    public RasterProjectorImpl build(){
        RasterProjectorImpl projector = new RasterProjectorImpl();
        if( slidesPath!=null ){
            projector.addSlides(fileList(slidesPath).stream().map(s -> new RasterSlideImpl("file://"+s)).collect(Collectors.toList()));
        }
        projector.first();
        return projector;
    }

    private List<String> fileList(String directory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        } catch (IOException ex) {

        }
        return fileNames;
    }
}
