package it.unitn.lode2;

import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.ui.controllers.WizardController;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.prefs.XMLLodePrefs;
import it.unitn.lode2.xml.prefs.XMLProperty;
import it.unitn.lode2.xml.prefs.XMLSection;
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
import java.util.List;
import java.util.Optional;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        // There's a lecture to record?
        String courseFolder = null;
        XMLLodePrefs prefs = XMLHelper.build(XMLLodePrefs.class).unmarshal(new File(Constants.LODE_PREFS));
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
        if( courseFolder!=null ){
            XmlCourseImpl course = new XmlCourseImpl(courseFolder);
            Lecture lecture = course.lectures().get(course.lectures().size() - 1);
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Lecture ready for the recording session.");
            String msg = "Do you want to start the recording session for the lecture:\n\n \""
                       + lecture.name()
                       + "\" ("
                       + course.name()
                       + ").";
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
                } else if( result.get()==buttonTypeWizard ){
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

            }
        }

    }


    public static void main(String[] args) {

        launch(args);
    }
}
