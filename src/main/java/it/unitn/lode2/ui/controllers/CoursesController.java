package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.asset.xml.XmlLectureImpl;
import it.unitn.lode2.slidejuicer.pdf.PdfJuicerImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * User: tiziano
 * Date: 16/04/15
 * Time: 11:22
 */
public class CoursesController implements Initializable {

    private final static String LODE_HOME = System.getProperty("user.home") + "/_LODE/"; // XXX: to move outside...
    private final static String LODE_CURSES = LODE_HOME + "/COURSES/";

    private Course selectedCourse=null;

    private Lecture selectedLecture=null;

    private List<Course> courses;

    private Map<TreeItem, Object> items = new HashMap<>();

    @FXML
    private VBox root;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField numberTextField;

    @FXML
    private ImageView previewImageView;

    @FXML
    private TextField fileNameTextField;

    @FXML
    private TextArea textTextArea;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button newCourseButton;

    @FXML
    private Button delCourseButton;

    @FXML
    private TextField courseNameTextField;

    @FXML
    private TextField courseYearTextField;

    @FXML
    private TextField courseHomeTextField;

    @FXML
    private Button newLectureButton;

    @FXML
    private Button delLectureButton;

    @FXML
    private TextField lectureNameTextField;

    @FXML
    private Button importSlidesButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        treeView.getSelectionModel().selectedItemProperty().addListener(selectionListener);

        newCourseButton.setOnAction(newCourseHandler);
        newLectureButton.setOnAction(newLectureHandler);
        importSlidesButton.setOnAction(importSlidesHandler);
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

        treeView.setRoot(null);

        TreeItem<String> root = new TreeItem<>("COURSES");
        root.setExpanded(true);
        root.setGraphic(createGraphicNode("house"));
        for( Course course: courses ){
            TreeItem<String> courseTreeItem = new TreeItem<>(course.name());
            courseTreeItem.setGraphic(createGraphicNode("book"));
            root.getChildren().add(courseTreeItem);
            items.put(courseTreeItem, course);
            for( Lecture lecture: course.lectures() ){
                TreeItem lectureTreeItem = new TreeItem(lecture.name());
                lectureTreeItem.setGraphic(createGraphicNode("report"));
                courseTreeItem.getChildren().add(lectureTreeItem);
                items.put(lectureTreeItem, lecture);
                TreeItem<String> slidesTreeItem = new TreeItem<>("slides");
                slidesTreeItem.setGraphic(createGraphicNode("folder_image"));
                lectureTreeItem.getChildren().add(slidesTreeItem);
                for( Slide slide: lecture.slides() ){
                    TreeItem slideTreeItem = new TreeItem(slide.title());
                    slideTreeItem.setGraphic(createGraphicNode("image"));
                    slidesTreeItem.getChildren().add(slideTreeItem);
                    items.put(slideTreeItem, slide);
                }
            }
            TreeItem<String> teachersTreeItem = new TreeItem<>("teachers");
            teachersTreeItem.setGraphic(createGraphicNode("group"));
            courseTreeItem.getChildren().add(teachersTreeItem);
            for( String teacher: course.teachers() ){
                TreeItem teacherTreeItem = new TreeItem(teacher);
                teacherTreeItem.setGraphic(createGraphicNode("user_gray"));
                teachersTreeItem.getChildren().add(teacherTreeItem);
                items.put(teacherTreeItem, teacher);
            }
        }
        treeView.setRoot(root);
    }


    private Node createGraphicNode(String name){
        return new ImageView(new Image(CoursesController.class.getResourceAsStream("/images/" + name + ".png")));
    }

    ChangeListener selectionListener = new ChangeListener(){

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            TreeItem treeItem = (TreeItem) newValue;
            Object item = items.get(treeItem);
            if( item instanceof Slide ){
                tabPane.getSelectionModel().select(2);
                Slide slide = (Slide) item;
                titleTextField.setText(slide.title());
                textTextArea.setText(slide.text());
                TreeItem lectureTreeItem = treeItem.getParent().getParent();
                Lecture lecture = (Lecture) items.get(lectureTreeItem);
                TreeItem courseTreeItem = lectureTreeItem.getParent();
                fileNameTextField.setText(slide.filename());
                previewImageView.setImage(new Image("file://" + lecture.path() + "/" + slide.filename()));
                selectedLecture = lecture;
                selectedCourse = lecture.course();
            } else if( item instanceof Course ){
                tabPane.getSelectionModel().select(0);
                Course course = (Course) item;
                courseNameTextField.setText(course.name());
                courseHomeTextField.setText(course.path());
                courseYearTextField.setText(course.year().toString());
                selectedCourse = course;
                selectedLecture = null;
            } else if( item instanceof Lecture ){
                tabPane.getSelectionModel().select(1);
                Lecture lecture = (Lecture) item;
                lectureNameTextField.setText(lecture.name());
                selectedLecture = lecture;
                selectedCourse = lecture.course();
            }
        }
    };

    EventHandler<ActionEvent> newCourseHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog("New course name");
            dialog.setTitle("New course creation");
            dialog.setHeaderText("Insert the name of the new course");
            dialog.setContentText("Course name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String courseFolderName = LODE_CURSES + name; // XXX: check name...
                if( new File(courseFolderName).mkdir() ) {
                    new File(courseFolderName + "/Acquisition").mkdir();
                    new File(courseFolderName + "/Distribution").mkdir();
                    XmlCourseImpl course = new XmlCourseImpl(courseFolderName);
                    course.setName(name);
                    course.setYear(2015); // XXX
                    course.save();
                    courses.add(course);
                    refresh();
                }
            });
        }
    };

    EventHandler<ActionEvent> newLectureHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog("New lecture name");
            dialog.setTitle("New lecture creation");
            dialog.setHeaderText("Insert the name of the new lecture for the '" + selectedCourse.name() + "' course.");
            dialog.setContentText("Lecture name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String folderName = "01_" + name;
                String lectureFolder = selectedCourse.path() + "/Acquisition/" + folderName;
                if( new File(lectureFolder).mkdir() ) {
                    new File(lectureFolder + "/Slides").mkdir();
                    XmlLectureImpl lecture = new XmlLectureImpl((XmlCourseImpl) selectedCourse, folderName);
                    lecture.setName(name);
                    lecture.save();
                    selectedCourse.addLecture(lecture);
                    selectedCourse.save();
                    refresh();
                }
            });
        }
    };

    EventHandler<ActionEvent> importSlidesHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Select pdf file to import","*.pdf");
            fileChooser.getExtensionFilters().add(extension);
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());
            PdfJuicerImpl.build().slide(file).output(selectedLecture.path() + "/Slides/")
                    .extract().stream().forEach(s -> selectedLecture.addSlide(s));
            selectedLecture.save();
            refresh();
        }
    };

}
