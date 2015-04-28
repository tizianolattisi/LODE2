package it.unitn.lode2;

import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.prefs.XMLLodePrefs;
import it.unitn.lode2.xml.prefs.XMLProperty;
import it.unitn.lode2.xml.prefs.XMLSection;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    //private final static String CAMERA_CONF = "/Users/tiziano/Projects/LODE2/confs/ipcamera/FOSCAM.XML";
    private final static String CAMERA_CONF = System.getProperty("user.home") + "/_LODE/IPCAM.XML";
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
        RecordingSessionLaucher.launch(new Stage(), courseFolder);

    }


    public static void main(String[] args) {

        launch(args);
    }
}
