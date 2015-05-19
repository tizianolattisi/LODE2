package it.unitn.lode2;

import it.unitn.lode2.asset.LodePrefs;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

/**
 * Created by tiziano on 19/05/15.
 */
public class Setup {

    private static void copyTemplate(String resource, OutputStream output) throws IOException {
        InputStream input=null;
        try {
            input = Setup.class.getResourceAsStream(resource);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            output.flush();
            input.close();
            output.close();
        }
    }

    public static void checkAndSetupLodeHome() throws IOException {
        File home = new File(Constants.LODE_HOME);
        if( !home.exists() ){
            home.mkdir();
            File courses = new File(Constants.LODE_COURSES);
            courses.mkdir();
            copyTemplate("/templates/LODE_PREFS.XML", new FileOutputStream(Constants.LODE_PREFS));
        }
    }

    public static void checkAndSetupFfmpeg(Stage stage){
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

    public static void checkAndSetupIpCam(Stage stage) throws IOException{
        LodePrefs prefs = IOC.queryUtility(LodePrefs.class);
        File home = new File(Constants.CAMERA_CONF);
        if( !home.exists() ){
            ChoiceDialog<String> dialog = new ChoiceDialog<>("FOSCAM", prefs.getIpCamPresets());
            dialog.setTitle("IPCAM configuration missing");

            dialog.setHeaderText("Your setup don't contains a IPCAM config.");
            Optional<String> result = dialog.showAndWait();
            if( result.isPresent() ){
                copyTemplate("/templates/ipcams/" + result.get() + ".XML", new FileOutputStream(Constants.CAMERA_CONF));
            }
        }
        String host = prefs.getHost();
        String user = prefs.getUser();
        String password = prefs.getPassword();

        TextInputDialog dialog = new TextInputDialog(host);
        dialog.setTitle("IPCAM ip address");
        dialog.setHeaderText("Insert the ip address of the cam.");
        dialog.setContentText("Ip address:");
        dialog.showAndWait().ifPresent(s -> prefs.setHost(s));

        dialog = new TextInputDialog(user);
        dialog.setTitle("IPCAM ip user");
        dialog.setHeaderText("Insert the user name.");
        dialog.setContentText("User:");
        dialog.showAndWait().ifPresent(s -> prefs.setUser(s));

        dialog = new TextInputDialog(password);
        dialog.setTitle("IPCAM ip password");
        dialog.setHeaderText("Insert the password.");
        dialog.setContentText("Password:");
        dialog.showAndWait().ifPresent(s -> prefs.setPassword(s));

        prefs.save();
    }

}
