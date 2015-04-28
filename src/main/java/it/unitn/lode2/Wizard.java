package it.unitn.lode2;

import it.unitn.lode2.ui.controllers.WizardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * User: tiziano
 * Date: 16/04/15
 * Time: 11:37
 */
public class Wizard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wizard.fxml"));
        Parent root = loader.load();
        WizardController controller = loader.getController();
        controller.setLodeCoursesPath(Constants.LODE_COURSES);
        primaryStage.setTitle("LODE2 Wizard");
        Scene scene = new Scene(root, 500, 345);

        primaryStage.setScene(scene);
        //primaryStage.setOnCloseRequest(controller.handlerClose);
        primaryStage.show();

    }

    public static void main(String[] args) {

        launch(args);
    }
}
