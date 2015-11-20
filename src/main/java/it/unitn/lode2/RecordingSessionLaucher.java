package it.unitn.lode2;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.LodePrefs;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Previewer;
import it.unitn.lode2.camera.ipcam.CameraIPBuilder;
import it.unitn.lode2.camera.ipcam.Cmds;
import it.unitn.lode2.camera.ipcam.PreviewerIPBuilder;
import it.unitn.lode2.camera.ipcam.connection.*;
import it.unitn.lode2.projector.Projector;
import it.unitn.lode2.projector.raster.RasterProjectorBuilder;
import it.unitn.lode2.projector.raster.RasterProjectorImpl;
import it.unitn.lode2.projector.raster.RasterSlideImpl;
import it.unitn.lode2.recorder.Recorder;
import it.unitn.lode2.recorder.VolumeChecker;
import it.unitn.lode2.recorder.ipcam.IPRecorderBuilder;
import it.unitn.lode2.recorder.ipcam.IPRecorderProtocol;
import it.unitn.lode2.recorder.ipcam.IPRecorderVolumeCheckerImpl;
import it.unitn.lode2.remote.Remote;
import it.unitn.lode2.remote.smartphone.SmartPhoneRemoteBuilder;
import it.unitn.lode2.ui.controllers.MainController;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.course.XMLCourse;
import it.unitn.lode2.xml.ipcam.XMLCameraIPConf;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by tiziano on 28/04/15.
 */
public class RecordingSessionLaucher {


    public static void launch(Stage stage, String courseFolder) throws IOException{
        XMLCourse xmlCourse = XMLHelper.build(XMLCourse.class).unmarshal(new File(courseFolder + "/COURSE.XML"));
        List<String> lectures = xmlCourse.getXMLCourseLectures().getLectures();
        String lectureFolderName = lectures.get(lectures.size() - 1);
        launch(stage, courseFolder, lectureFolderName);
    }

    public static void launch(Stage stage, String courseFolder, String lectureFolderName) throws IOException {


        String lectureFolder = courseFolder + "Acquisition/" + lectureFolderName;

        // LODE prefs
        LodePrefs lodePrefs = IOC.queryUtility(LodePrefs.class);

        // read ip camera configuration
        XMLCameraIPConf cameraIPConf = XMLHelper.build(XMLCameraIPConf.class).unmarshal(new File(Constants.CAMERA_CONF));

        // ConnectionProvider
        ConnectionProvider connProvider;
        if(AuthMode.BASIC.equals(cameraIPConf.getAuthMode()) ) {
            connProvider = new BasicConnectionProvider(lodePrefs.getUser(), lodePrefs.getPassword());
        } else if(AuthMode.QUERY.equals(cameraIPConf.getAuthMode()) ) {
            connProvider = new QueryConnectionProvider(lodePrefs.getUser(), lodePrefs.getPassword(), cameraIPConf.getAuthQuery());
        } else {
            connProvider = new NoAuthConnectionProvider();
        }
        IOC.registerUtility(connProvider, ConnectionProvider.class);


        // Camera
        CameraIPBuilder builder = CameraIPBuilder.create()
                .user(lodePrefs.getUser())
                .password(lodePrefs.getPassword())
                .host(lodePrefs.getHost())
                .port(cameraIPConf.getCgiPort());
        if( cameraIPConf.getZoomIn()!=null && cameraIPConf.getZoomOut()!=null ){
            builder = builder.template(Cmds.ZOOMIN, cameraIPConf.getZoomIn());
            builder = builder.template(Cmds.ZOOMOUT, cameraIPConf.getZoomOut());
        }
        if( cameraIPConf.getZoomStop()!=null ){
            builder = builder.template(Cmds.ZOOMSTOP, cameraIPConf.getZoomStop());
        }
        if( cameraIPConf.getPanLeft()!=null && cameraIPConf.getPanRight()!=null ){
            builder = builder.template(Cmds.PANLEFT, cameraIPConf.getPanLeft());
            builder = builder.template(Cmds.PANRIGHT, cameraIPConf.getPanRight());
        }
        if( cameraIPConf.getPanStop()!=null ){
            builder = builder.template(Cmds.PANSTOP, cameraIPConf.getPanStop());
        }
        if( cameraIPConf.getTiltUp()!=null && cameraIPConf.getTiltDown()!=null){
            builder = builder.template(Cmds.TILTUP, cameraIPConf.getTiltUp());
            builder = builder.template(Cmds.TILTDOWN, cameraIPConf.getTiltDown());
        }
        if( cameraIPConf.getTiltStop()!=null ){
            builder = builder.template(Cmds.TILTSTOP, cameraIPConf.getTiltStop());
        }
        if( cameraIPConf.getSnapshot()!=null ){
            builder = builder.template(Cmds.SNAPSHOT, cameraIPConf.getSnapshot());
        }
        if( cameraIPConf.getPreset()!=null ){
            builder = builder.template(Cmds.PRESET, cameraIPConf.getPreset());
        }
        if( cameraIPConf.getAddPreset()!=null ){
            builder = builder.template(Cmds.ADDPRESET, cameraIPConf.getAddPreset());
        }
        if( cameraIPConf.getDelPreset()!=null ){
            builder = builder.template(Cmds.DELPRESET, cameraIPConf.getDelPreset());
        }
        Camera camera = builder.build();
        IOC.registerUtility(camera, Camera.class);

        // Previewer
        Previewer previewer = PreviewerIPBuilder.create()
                .user(lodePrefs.getUser())
                .password(lodePrefs.getPassword())
                .host(lodePrefs.getHost())
                .port(cameraIPConf.getCgiPort())
                .snapshotUrl(cameraIPConf.getSnapshot())
                .build();
        IOC.registerUtility(previewer, Previewer.class);


        // Recorder
        IPRecorderBuilder recorderBuilder = IPRecorderBuilder.create();
        if( cameraIPConf.getStreamProtocol()!=null ){
            recorderBuilder = recorderBuilder.protocol(IPRecorderProtocol.valueOf(cameraIPConf.getStreamProtocol()));
        }
        if( cameraIPConf.getStreamPort()!=null ){
            recorderBuilder = recorderBuilder.port(cameraIPConf.getStreamPort());
        }
        Recorder recorder = recorderBuilder
                .host(lodePrefs.getHost())
                .url(cameraIPConf.getStreamUrl())
                .user(lodePrefs.getUser())
                .password(lodePrefs.getPassword())
                .recordCommand(cameraIPConf.getRecordCommand())
                        //.output(lectureFolder + "/movie0.mp4")
                .output(lectureFolder)
                .ffmpeg(lodePrefs.getFfmpegPath())
                .build();
        IOC.registerUtility(recorder, Recorder.class);

        // Volume checker
        VolumeChecker volumeChecker = new IPRecorderVolumeCheckerImpl();
        IOC.registerUtility(volumeChecker, VolumeChecker.class);

        // Remote
        Remote remote = SmartPhoneRemoteBuilder.create()
                .host("192.168.1.101")
                .port(2147)
                .build();
        IOC.registerUtility(remote, Remote.class);

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

        FXMLLoader loader = new FXMLLoader(RecordingSessionLaucher.class.getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        stage.setTitle("Cam controller");
        Scene scene = new Scene(root, 1024, 720);

        scene.getStylesheets().add("/css/flat.css");
        controller.keyBindings();
        stage.setScene(scene);
        stage.setOnCloseRequest(controller.handlerClose);
        stage.show();
    }
}
