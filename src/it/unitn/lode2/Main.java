package it.unitn.lode2;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.camera.ipcam.CameraIPBuilder;
import it.unitn.lode2.camera.ipcam.Cmds;
import it.unitn.lode2.camera.ipcam.PreviewerIPBuilder;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.projector.Projector;
import it.unitn.lode2.projector.raster.RasterProjectorBuilder;
import it.unitn.lode2.projector.raster.RasterProjectorImpl;
import it.unitn.lode2.projector.raster.RasterSlideImpl;
import it.unitn.lode2.ui.controllers.MainController;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.course.XMLCourse;
import it.unitn.lode2.xml.ipcam.XMLCameraIPConf;
import it.unitn.lode2.xml.prefs.XMLLodePrefs;
import it.unitn.lode2.xml.prefs.XMLProperty;
import it.unitn.lode2.xml.prefs.XMLSection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;

public class Main extends Application {

    //private final static String CAMERA_CONF = "/Users/tiziano/Projects/LODE2/confs/ipcamera/FOSCAM.XML";
    private final static String CAMERA_CONF = System.getProperty("user.home") + "/_LODE/FOSCAM.XML";
    private final static String LODE_PREFS = System.getProperty("user.home") + "/_LODE/.LODE_PREFS.XML";

    @Override
    public void start(Stage primaryStage) throws Exception{

        // From file dialog
        /*
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Lecture's configuration file (LECTURE.XML)","*.xml");
        fileChooser.getExtensionFilters().add(extension);
        File file = fileChooser.showOpenDialog(null);
        String lectureFileName = file.toURI().toURL().getFile();
        String lectureFolder = lectureFileName.substring(0, lectureFileName.lastIndexOf("/"));
        String courseFolder = lectureFolder.substring(0, lectureFolder.lastIndexOf("Acquisition"));
        */

        // Last used course
        String courseFolder = null;
        XMLLodePrefs prefs = XMLHelper.build(XMLLodePrefs.class).unmarshal(new File(LODE_PREFS));
        for( XMLSection section: prefs.getSections() ){
            if( "LAST USED COURSE".equals(section.getName()) ){
                for(XMLProperty property: section.getGroupOfProperties().getProperties() ){
                    if( "Last used course".equals(property.getName()) ){
                        courseFolder = property.getValue() + "/";
                        break;
                    }
                }
                break;
            }
        }
        XMLCourse xmlCourse = XMLHelper.build(XMLCourse.class).unmarshal(new File(courseFolder + "/COURSE.XML"));
        List<String> lectures = xmlCourse.getXMLCourseLectures().getLectures();
        String lectureFolderName = lectures.get(lectures.size() - 1);
        String lectureFolder = courseFolder + "Acquisition/" + lectureFolderName;

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
                //.template(Cmds.SNAPSHOT, cameraIPConf.getSnapshot())
                .template(Cmds.PRESET, cameraIPConf.getPreset())
                .template(Cmds.ADDPRESET, cameraIPConf.getAddPreset())
                .template(Cmds.DELPRESET, cameraIPConf.getDelPreset())
                .build();
        IOC.registerUtility(camera, Camera.class);

        // Previewer
        Previewer previewer = PreviewerIPBuilder.create()
                .user(cameraIPConf.getUser())
                .password(cameraIPConf.getPassword())
                .host(cameraIPConf.getHost())
                .port(cameraIPConf.getCgiPort())
                .snapshotUrl(cameraIPConf.getSnapshot())
                .build();
        IOC.registerUtility(previewer, Previewer.class);


        // Recorder
        Recorder recorder = IPRecorderBuilder.create()
                .protocol(IPRecorderProtocol.valueOf(cameraIPConf.getStreamProtocol()))
                .host(cameraIPConf.getHost())
                .port(cameraIPConf.getStreamPort())
                .url(cameraIPConf.getStreamUrl())
                .user(cameraIPConf.getUser())
                .password(cameraIPConf.getPassword())
                .recordCommand(cameraIPConf.getRecordCommand())
                //.output(lectureFolder + "/movie0.mp4")
                .output(lectureFolder)
                .build();
        IOC.registerUtility(recorder, Recorder.class);

        Course course = new XmlCourseImpl(courseFolder);
        Lecture lecture=null;
        int i = Integer.parseInt(lectureFolderName.substring(0, 2)); // XXX: bruttura
        for( Lecture l: course.lectures() ){
            if( l.number().equals(i) ) {
                lecture = l;
            }
        }
        IOC.registerUtility(lecture, Lecture.class);

        RasterProjectorBuilder projectorBuilder = RasterProjectorBuilder.create();
        for( Slide slide: lecture.slides() ){
            URL url = (new File(lectureFolder + "/" + slide.filename())).toURI().toURL();
            projectorBuilder = projectorBuilder.slide(new RasterSlideImpl(url, slide.title(), slide.text()));
        }
        RasterProjectorImpl projector = projectorBuilder.build();
        IOC.registerUtility(projector, Projector.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unitn/lode2/ui/views/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        primaryStage.setTitle("Cam controller");
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("/it/unitn/lode2/ui/skin/flat.css");
        controller.keyBindings();
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(controller.handlerClose);
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
