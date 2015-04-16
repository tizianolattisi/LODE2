package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.Slide;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * User: tiziano
 * Date: 16/04/15
 * Time: 11:22
 */
public class CoursesController implements Initializable {

    private List<Course> courses;

    @FXML
    private TreeView<String> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
    }

    public EventHandler<WindowEvent> handlerClose = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm exit");
            alert.setHeaderText("Do you really want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if( ButtonType.OK.equals(result.get()) ) {
                // TODO: finalize
                System.out.println("ok, exit");
            } else {
                event.consume();
            }
        }
    };

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        refresh();
    }

    private void refresh() {

        TreeItem<String> root = new TreeItem<>("COURSES");
        root.setExpanded(true);
        root.setGraphic(createGraphicNode("house"));
        for( Course course: courses ){
            TreeItem<String> courseTreeItem = new TreeItem<>(course.name());
            courseTreeItem.setGraphic(createGraphicNode("book"));
            root.getChildren().add(courseTreeItem);
            for( Lecture lecture: course.lectures() ){
                TreeItem lectureTreeItem = new TreeItem(lecture.name());
                lectureTreeItem.setGraphic(createGraphicNode("report"));
                courseTreeItem.getChildren().add(lectureTreeItem);
                for( Slide slide: lecture.slides() ){
                    TreeItem slideTreeItem = new TreeItem(slide.title());
                    slideTreeItem.setGraphic(createGraphicNode("image"));
                    lectureTreeItem.getChildren().add(slideTreeItem);
                }
            }
        }
        treeView.setRoot(root);
    }


    private Node createGraphicNode(String name){
        return new ImageView(new Image(CoursesController.class.getResourceAsStream("/images/" + name + ".png")));
    }
}
