package it.unitn.lode2;

import it.unitn.lode2.asset.LodePrefs;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by tiziano on 19/05/15.
 */
public class Setup {

    public static void setup(Stage stage){
        ffmpeg(stage);
    }

    private static void ffmpeg(Stage stage){
        LodePrefs lodePrefs = IOC.queryUtility(LodePrefs.class);
        String ffmpegPath = lodePrefs.getFfmpegPath();
        File file = new File(ffmpegPath);
        if( !file.exists() ){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select ffmpeg path");
            if( false ) { // windows
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Exe Files", "*.exe"));
            }
            File ffmpegFile = fileChooser.showOpenDialog(stage);
            ffmpegPath = ffmpegFile.getAbsolutePath();
            lodePrefs.setFfmpegPath(ffmpegPath);
            lodePrefs.save();
        }
    }

}
