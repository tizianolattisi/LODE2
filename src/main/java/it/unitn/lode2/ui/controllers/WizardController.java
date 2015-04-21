package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.asset.Course;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by tiziano on 21/04/15.
 */
public class WizardController implements Initializable {

    public class CourseFormatCell extends ListCell<Course> {
        @Override
        protected void updateItem(Course item, boolean empty) {
            super.updateItem(item, empty);
            if( empty || item==null ){
                setText(null);
            } else {
                setText(item.name());
                setGraphic(createGraphicNode("book"));
            }
        }
    }

    @FXML
    private ListView<Course> coursesListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coursesListView.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override public ListCell<Course> call(ListView<Course> list) {
                return new CourseFormatCell();
            }
        });
    }

    public void setCourses(List<Course> courses) {
        coursesListView.setItems(FXCollections.observableList(courses));
    }

    private Node createGraphicNode(String name){
        return new ImageView(new Image(CoursesController.class.getResourceAsStream("/images/" + name + ".png")));
    }


}
