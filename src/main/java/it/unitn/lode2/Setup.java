package it.unitn.lode2;

import it.unitn.lode2.asset.LodePrefs;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
        if( checkIfMacAppBundle() ){
            lodePrefs.setFfmpegPath("./recorders/ffmpeg");
            lodePrefs.save();
            return;
        }
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

    public static void checkAndSetupISightRecorder(Stage stage){
        LodePrefs lodePrefs = IOC.queryUtility(LodePrefs.class);
        if( checkIfMacAppBundle() ){
            lodePrefs.setISightRecorderPath("./recorders/MacRecorder");
            lodePrefs.save();
            return;
        }
        String iSightRecorderPath = lodePrefs.getISightRecorderPath();
        File file = new File(iSightRecorderPath);
        if( !file.exists() ){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select iSightRecorder path");
            if( false ) { // windows
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Exe Files", "*.exe"));
            }
            File iSightRecorderFile = fileChooser.showOpenDialog(stage);
            iSightRecorderPath = iSightRecorderFile.getAbsolutePath();
            lodePrefs.setISightRecorderPath(iSightRecorderPath);
            lodePrefs.save();
        }
    }

    public static void checkAndSetupIpCam(Stage stage) throws IOException{
        checkAndSetupIpCam(stage, false);
    }

    public static void checkAndSetupIpCam(Stage stage, Boolean forceSetup) throws IOException{
        LodePrefs prefs = IOC.queryUtility(LodePrefs.class);
        File home = new File(Constants.CAMERA_CONF);
        Boolean isISight = Boolean.FALSE;
        if( forceSetup || !home.exists() ){
            ChoiceDialog<String> dialog = new ChoiceDialog<>("FOSCAM", prefs.getIpCamPresets());
            dialog.setTitle("IPCAM configuration missing");

            dialog.setHeaderText("Your setup don't contains a IPCAM config.");
            Optional<String> result = dialog.showAndWait();
            if( result.isPresent() ){
                isISight = "ISIGHT".equals(result.get());
                copyTemplate("/templates/ipcams/" + result.get() + ".XML", new FileOutputStream(Constants.CAMERA_CONF));
            }

        }

        String host = prefs.getHost();
        String user = prefs.getUser();
        String password = prefs.getPassword();
        if( !forceSetup && (host!=null && host!="") ){
            return;
        }

        if( isISight ){
            final String[] iSightPars = {""}; //" -v 'FaceTime Camera' -a 'Built-in Microphone'";
            prefs.setHost("127.0.0.1");
            prefs.setUser("");
            prefs.setPassword("");
            List<String> videoDevices = listDevices("v");
            List<String> audioDevices = listDevices("a");
            List<String> mutexDevices = listDevices("m");
            if( videoDevices.size()>0 ) {
                ChoiceDialog<String> dialog = new ChoiceDialog<>(videoDevices.get(0), videoDevices);
                dialog.setTitle("Select video device");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> {
                    iSightPars[0] += " -v '" + s + "'";
                });
            }
            if( audioDevices.size()>0 ) {
                ChoiceDialog<String> dialog = new ChoiceDialog<>(audioDevices.get(0), audioDevices);
                dialog.setTitle("Select audio device");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> {
                    iSightPars[0] += " -a '" + s + "'";
                });
            }
            if( mutexDevices.size()>0 ) {
                ChoiceDialog<String> dialog = new ChoiceDialog<>(mutexDevices.get(0), mutexDevices);
                dialog.setTitle("Select mutex device");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> {
                    iSightPars[0] += " -m '" + s + "'";
                });
            }
            prefs.setISightRecorderPath(prefs.getISightRecorderPath() + iSightPars[0]);
        } else {
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
        }

        prefs.save();
    }

    private static List<String> listDevices(String deviceType) throws IOException {
        ArrayList<String> command = new ArrayList();
        command.add("./recorders/MacRecorder");
        command.add("-l");
        command.add(deviceType);
        Process process = new ProcessBuilder(command).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<String> devices = new ArrayList<>();
        while ( (line = reader.readLine()) != null ) {
            devices.add(line);
        }
        return devices;
    }

    private static Boolean checkIfMacAppBundle(){
        if( System.getProperty("os.name").toLowerCase().indexOf("mac")>=0 ){
            try {
                if( new File(".").getCanonicalPath().endsWith("Contents/Resources") ){
                    return Boolean.TRUE;
                }
            } catch (IOException e) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

}
