package it.unitn.lode2;

import it.unitn.lode2.cam.Camera;
import it.unitn.lode2.cam.ipcam.CameraIPBuilder;
import it.unitn.lode2.cam.ipcam.Cmds;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.slide.Projector;
import it.unitn.lode2.slide.raster.RasterProjectorBuilder;
import it.unitn.lode2.slide.raster.RasterProjectorImpl;
import it.unitn.lode2.slide.raster.RasterSlideImpl;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.ipcam.CameraIPConf;
import it.unitn.lode2.xml.lecture.Lecture;
import it.unitn.lode2.xml.slides.LodeSlide;
import it.unitn.lode2.xml.slides.LodeSlides;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main extends Application {

    private final static String LECTURE_FOLDER = "/Users/tiziano/_LODE/COURSES/Test_2014/Acquisition/12_Test12_2014-12-31/";
    private final static String FOSCAM_CONF = "/Users/tiziano/Projects/LODE2/confs/ipcamera/FOSCAM.XML";


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/it/unitn/lode2/ui/camctrl.fxml"));
        primaryStage.setTitle("Cam controller");
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("/it/unitn/lode2/ui/skin/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) throws MalformedURLException {

        // read ip camera configuration
        CameraIPConf cameraIPConf = XMLHelper.build(CameraIPConf.class).unmarshal(new File(FOSCAM_CONF));

        // Camera
        Camera camera = CameraIPBuilder.create()
                .user(cameraIPConf.getUser())
                .password(cameraIPConf.getPassword())
                .host(cameraIPConf.getHost())
                .port(cameraIPConf.getCgiPort())
                .template(Cmds.ZOOMIN, cameraIPConf.getZoomIn())
                .template(Cmds.ZOOMOUT, cameraIPConf.getZoomOut())
                .template(Cmds.ZOOMSTOP, cameraIPConf.getZoomStop())
                .template(Cmds.PANLEFT, cameraIPConf.getPanLeft())
                .template(Cmds.PANRIGHT, cameraIPConf.getPanRight())
                .template(Cmds.PANSTOP, cameraIPConf.getPanStop())
                .template(Cmds.TILTUP, cameraIPConf.getTiltUp())
                .template(Cmds.TILTDOWN, cameraIPConf.getTiltDown())
                .template(Cmds.TILTSTOP, cameraIPConf.getTiltStop())
                .template(Cmds.SNAPSHOT, cameraIPConf.getSnapshot())
                .template(Cmds.PRESET, cameraIPConf.getPreset())
                .build();
        IOC.registerUtility(camera, Camera.class);

        // Recorder
        Recorder recorder = IPRecorderBuilder.create()
                .protocol(IPRecorderProtocol.valueOf(cameraIPConf.getStreamProtocol()))
                .host(cameraIPConf.getHost())
                .port(cameraIPConf.getStreamPort())
                .url(cameraIPConf.getStreamUrl())
                .user(cameraIPConf.getUser())
                .password(cameraIPConf.getPassword())
                .output(LECTURE_FOLDER + "movie0.avi")
                .build();
        IOC.registerUtility(recorder, Recorder.class);

        // Lecture and Slide configuration
        Lecture lecture = XMLHelper.build(Lecture.class).unmarshal(new File(LECTURE_FOLDER + "LECTURE.XML"));
        LodeSlides lodeSlides = XMLHelper.build(LodeSlides.class).unmarshal(new File(LECTURE_FOLDER + "SLIDES.XML"));

        RasterProjectorBuilder projectorBuilder = RasterProjectorBuilder.create();
        for( LodeSlide slide: lodeSlides.getSlides().getSlides() ){
            URL url = (new File(LECTURE_FOLDER + slide.getFileName())).toURI().toURL();
            projectorBuilder = projectorBuilder.slide(new RasterSlideImpl(url, slide.getTitle(), ""));
        }
        RasterProjectorImpl projector = projectorBuilder.build();
        IOC.registerUtility(projector, Projector.class);

        launch(args);
    }
}
