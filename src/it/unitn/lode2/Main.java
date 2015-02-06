package it.unitn.lode2;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.ipcam.CameraIPBuilder;
import it.unitn.lode2.camera.ipcam.Cmds;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.projector.Projector;
import it.unitn.lode2.projector.raster.RasterProjectorBuilder;
import it.unitn.lode2.projector.raster.RasterProjectorImpl;
import it.unitn.lode2.projector.raster.RasterSlideImpl;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.ipcam.XMLCameraIPConf;
import it.unitn.lode2.xml.lecture.XMLLecture;
import it.unitn.lode2.xml.slides.XMLLodeSlidesSlidesSlide;
import it.unitn.lode2.xml.slides.XMLLodeSlides;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    private final static String CAMERA_CONF = "/Users/tiziano/Projects/LODE2/confs/ipcamera/FOSCAM.XML";

    @Override
    public void start(Stage primaryStage) throws Exception{

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Lecture's configuration file (LECTURE.XML)","*.xml");
        fileChooser.getExtensionFilters().add(extension);
        File file = fileChooser.showOpenDialog(null);
        String lectureFileName = file.toURI().toURL().getFile();
        String lectureFolder = lectureFileName.substring(0, lectureFileName.lastIndexOf("/"));
        String courseFolder = lectureFolder.substring(0, lectureFolder.lastIndexOf("Acquisition"));


        // read ip camera configuration
        XMLCameraIPConf cameraIPConf = XMLHelper.build(XMLCameraIPConf.class).unmarshal(new File(CAMERA_CONF));

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
                .output(lectureFolder + "/movie0.mp4")
                .build();
        IOC.registerUtility(recorder, Recorder.class);

        Course course = new XmlCourseImpl(courseFolder);
        Lecture lecture = course.lectures().get(0);
        RasterProjectorBuilder projectorBuilder = RasterProjectorBuilder.create();
        for( Slide slide: lecture.slides() ){
            URL url = (new File(lectureFolder + "/" + slide.filename())).toURI().toURL();
            projectorBuilder = projectorBuilder.slide(new RasterSlideImpl(url, slide.title(), slide.text()));
        }
        RasterProjectorImpl projector = projectorBuilder.build();
        IOC.registerUtility(projector, Projector.class);

        Parent root = FXMLLoader.load(getClass().getResource("/it/unitn/lode2/ui/camctrl.fxml"));
        primaryStage.setTitle("Cam controller");
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("/it/unitn/lode2/ui/skin/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
