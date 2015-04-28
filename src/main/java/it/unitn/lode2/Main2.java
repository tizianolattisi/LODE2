package it.unitn.lode2;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.ui.controllers.CoursesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tiziano
 * Date: 16/04/15
 * Time: 11:37
 */
public class Main2 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        File lodeHome = new File(Constants.LODE_COURSES);
        List<Course> courses = Arrays.asList(lodeHome.list((dir, name) -> new File(dir, name).isDirectory())).stream().map(n -> new XmlCourseImpl(Constants.LODE_COURSES + n)).collect(Collectors.toList());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/courses.fxml"));
        Parent root = loader.load();
        CoursesController controller = loader.getController();
        controller.setCourses(courses);
        primaryStage.setTitle("Courses management");
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(controller.handlerClose);
        primaryStage.show();

    }

    public static void main(String[] args) {

        launch(args);
    }
}
