package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.asset.xml.XmlLectureImpl;
import it.unitn.lode2.slidejuicer.pdf.PdfJuicerImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by tiziano on 21/04/15.
 */
public class WizardController implements Initializable {

    private String lodePath=null;

    @FXML
    private VBox root;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<Course> coursesListView;

    @FXML
    private TextField courseYear;

    @FXML
    private TextField courseName;

    @FXML
    private Button newCourseButton;

    @FXML
    private ListView<Lecture> lecturesListView;

    @FXML
    private TextField lectureName;

    @FXML
    private TextField lectureLecturer;

    @FXML
    private Button newLectureButton;

    @FXML
    private Button importSlideButton;

    @FXML
    private ListView<Slide> slidesListView;

    @FXML
    private Button recordingSessionButton;



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

    public class LectureFormatCell extends ListCell<Lecture> {
        @Override
        protected void updateItem(Lecture item, boolean empty) {
            super.updateItem(item, empty);
            if( empty || item==null ){
                setText(null);
                setGraphic(null);
            } else {
                setText(item.name());
                setGraphic(createGraphicNode("report"));
            }
        }
    }

    public class SlideFormatCell extends ListCell<Slide> {
        @Override
        protected void updateItem(Slide item, boolean empty) {
            super.updateItem(item, empty);
            if( empty || item==null ){
                setText(null);
                setGraphic(null);
            } else {
                setText(item.title());
                setGraphic(createGraphicNode("image"));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coursesListView.setCellFactory(list -> new CourseFormatCell());
        lecturesListView.setCellFactory(list -> new LectureFormatCell());
        slidesListView.setCellFactory(list -> new SlideFormatCell());
        coursesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldCourse, newCourse) -> {
            if( newCourse != null) {
                refreshLectures(newCourse.lectures());
                tabPane.getSelectionModel().select(1);
            }
        });
        lecturesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldLecture, newLecture) -> {
            if( newLecture!= null ){
                refreshSlides(newLecture.slides());
                tabPane.getSelectionModel().select(2);
            }
        });

        newCourseButton.setOnAction(event -> {
            String name = courseName.getText();
            courseName.setText("");
            Integer year = Integer.parseInt(courseYear.getText());
            courseYear.setText("");
            Course course = new XmlCourseImpl(lodePath + name);
            course.setName(name);
            course.setYear(year);
            course.save();
            refreshCourses();
        });

        newLectureButton.setOnAction(event -> {
            int size = lecturesListView.getItems().size();
            Integer number = size+1;
            String name = String.format("%02d", number) + " " + lectureName.getText();
            lectureName.setText("");
            String lecturer = lectureLecturer.getText();
            lectureLecturer.setText("");
            Course course = coursesListView.getSelectionModel().selectedItemProperty().get();
            Lecture lecture = new XmlLectureImpl((XmlCourseImpl) course, name);
            lecture.setName(name);
            lecture.setLecturer(lecturer);
            lecture.setNumber(number);
            lecture.save();
            course.addLecture(lecture);
            course.save();
            refreshLectures(course.lectures());
        });

        importSlideButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Select pdf file to import","*.pdf");
            fileChooser.getExtensionFilters().add(extension);
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());
            Lecture lecture = lecturesListView.getSelectionModel().selectedItemProperty().get();
            PdfJuicerImpl.build().slide(file).output(lecture.path() + "/Slides/")
                    .extract().stream().forEach(s -> lecture.addSlide(s));
            lecture.save();
            refreshCourses();
        });

        recordingSessionButton.setOnAction(event -> {

        });
    }

    public void setLodeCoursesPath(String path){
        lodePath = path;
        refreshCourses();
    }

    private void refreshCourses() {
        File lodeHome = new File(lodePath);
        List<Course> courses = Arrays.asList(lodeHome.list((dir, name) -> new File(dir, name).isDirectory())).stream().map(n -> new XmlCourseImpl(lodePath + n)).collect(Collectors.toList());
        coursesListView.setItems(FXCollections.observableList(courses));
    }

    private void refreshLectures(List<Lecture> lectures){
        lecturesListView.setItems(FXCollections.observableArrayList(lectures));
    }

    private void refreshSlides(List<Slide> slides){
        slidesListView.setItems(FXCollections.observableArrayList(slides));
    }

    private Node createGraphicNode(String name){
        return new ImageView(new Image(CoursesController.class.getResourceAsStream("/images/" + name + ".png")));
    }


}
