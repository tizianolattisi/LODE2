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
import it.unitn.lode2.xml.slides.LodeSlide;
import it.unitn.lode2.xml.slides.LodeSlides;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main extends Application {

    private final static String HOST = "192.168.2.3";
    private final static Integer PORT = 88;
    private final static String USER = "admin";
    private final static String PASSWORD = "admin";

    private final static String FOLDER = "/Users/tiziano/_LODE/COURSES/Test_2014/Acquisition/12_Test12_2014-12-31/";


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

        // Camera
        Camera camera = CameraIPBuilder.create()
                .user(USER)
                .password(PASSWORD)
                .host(HOST)
                .port(PORT)
                .template(Cmds.ZOOMIN, "/cgi-bin/CGIProxy.fcgi?cmd=zoomIn&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMOUT, "/cgi-bin/CGIProxy.fcgi?cmd=zoomOut&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=zoomStop&usr=${user}&pwd=${password}")
                .template(Cmds.PANLEFT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveLeft&usr=${user}&pwd=${password}")
                .template(Cmds.PANRIGHT, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveRight&usr=${user}&pwd=${password}")
                .template(Cmds.PANSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzStopRun&usr=${user}&pwd=${password}")
                .template(Cmds.TILTUP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveUp&usr=${user}&pwd=${password}")
                .template(Cmds.TILTDOWN, "/cgi-bin/CGIProxy.fcgi?cmd=ptzMoveDown&usr=${user}&pwd=${password}")
                .template(Cmds.TILTSTOP, "/cgi-bin/CGIProxy.fcgi?cmd=ptzStopRun&usr=${user}&pwd=${password}")
                .template(Cmds.SNAPSHOT, "/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=${user}&pwd=${password}")
                .build();
        IOC.registerUtility(camera, Camera.class);

        // Recorder
        Recorder recorder = IPRecorderBuilder.create()
                .protocol(IPRecorderProtocol.RTSP)
                .host(HOST)
                .port(PORT)
                .url("/videoMain")
                .user(USER)
                .password(PASSWORD)
                .output(FOLDER + "movie0.avi")
                .build();
        IOC.registerUtility(recorder, Recorder.class);

        // Slide
        LodeSlides lodeSlides = null;
        try {
            JAXBContext context = JAXBContext.newInstance(LodeSlides.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            lodeSlides = (LodeSlides) unmarshaller.unmarshal(new File(FOLDER + "SLIDES.XML"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        RasterProjectorBuilder projectorBuilder = RasterProjectorBuilder.create();
        for( LodeSlide slide: lodeSlides.getSlides().getSlides() ){
            URL url = (new File(FOLDER + slide.getFileName())).toURI().toURL();
            projectorBuilder = projectorBuilder.slide(new RasterSlideImpl(url));
        }
        RasterProjectorImpl projector = projectorBuilder.build();
        IOC.registerUtility(projector, Projector.class);

        launch(args);
    }
}
