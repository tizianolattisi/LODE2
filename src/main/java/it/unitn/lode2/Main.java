package it.unitn.lode2;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.LodePrefs;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.asset.xml.XmlLodePrefsImpl;
import it.unitn.lode2.postproduction.PostProducer;
import it.unitn.lode2.postproduction.impl.PostProducerBuilder;
import it.unitn.lode2.ui.controllers.WizardController;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.ipcam.XMLCameraIPConf;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Setup.checkAndSetupLodeHome();

        // LODE prefs
        LodePrefs lodePrefs = new XmlLodePrefsImpl(Constants.LODE_PREFS);
        IOC.registerUtility(lodePrefs, LodePrefs.class);

        Setup.checkAndSetupFfmpeg(primaryStage);
        Setup.checkAndSetupIpCam(primaryStage);

        XMLCameraIPConf cameraIPConf = XMLHelper.build(XMLCameraIPConf.class).unmarshal(new File(Constants.CAMERA_CONF));

        // Post producer
        PostProducer postProducer = PostProducerBuilder.create()
                .command(cameraIPConf.getConvCommand())
                .ffmpeg(lodePrefs.getFfmpegPath())
                .build();
        IOC.registerUtility(postProducer, PostProducer.class);

        // There's a lecture to record?
        if( lodePrefs.lastUsedCourses().size()>0 ){
            Course lastUsedCourse = lodePrefs.lastUsedCourses().get(0);
            XmlCourseImpl course = new XmlCourseImpl(lastUsedCourse.path());
            Lecture lecture=null;
            String msg="";
            if( course.lectures().size()>0 ) {
                lecture = course.lectures().get(course.lectures().size() - 1);
                if( lecture.videoLength()==null ){
                    msg = "Do you want to start the recording session for the lecture:\n\n \""
                            + lecture.name()
                            + "\" ("
                            + course.name()
                            + ").";
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Lecture ready for the recording session.");

                    alert.setHeaderText(msg);
                    //alert.setContentText("Choose your option.");
                    ButtonType buttonTypeRecord = new ButtonType("Record");
                    ButtonType buttonTypeWizard = new ButtonType("Go to wizard");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeRecord, buttonTypeWizard, buttonTypeCancel);
                    Optional<ButtonType> result = alert.showAndWait();
                    if( result.isPresent() ){
                        if( result.get()==buttonTypeRecord) {
                            RecordingSessionLaucher.launch(new Stage(), course.getFolderPath());
                            return;
                        } else if( result.get()==buttonTypeCancel ){
                            return;
                        }
                    }
                }
            }
        }
        startWizard(primaryStage);
    }

    private void startWizard(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wizard.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WizardController controller = loader.getController();
        controller.setLodeCoursesPath(Constants.LODE_COURSES);
        primaryStage.setTitle("LODE2 Wizard");
        Scene scene = new Scene(root, 500, 345);

        primaryStage.setScene(scene);
        //primaryStage.setOnCloseRequest(controller.handlerClose);
        primaryStage.show();
    }


    public static void main(String[] args) {

        // check manifest
        URLClassLoader cl = (URLClassLoader) Main.class.getClassLoader();
        try {
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            Attributes attributes = manifest.getMainAttributes();
            String buildTime = attributes.getValue("Build-Time");
            IOC.registerUtility(buildTime, String.class, "Build-Time");
        } catch (IOException E) {

        }

        launch(args);
    }
}
