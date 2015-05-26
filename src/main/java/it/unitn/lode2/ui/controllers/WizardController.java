package it.unitn.lode2.ui.controllers;

import it.unitn.lode2.Constants;
import it.unitn.lode2.IOC;
import it.unitn.lode2.RecordingSessionLaucher;
import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.asset.LodePrefs;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlCourseImpl;
import it.unitn.lode2.asset.xml.XmlLectureImpl;
import it.unitn.lode2.postproduction.PostProducer;
import it.unitn.lode2.slidejuicer.Juicer;
import it.unitn.lode2.slidejuicer.pdf.PdfJuicerImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CheckBox onlyLastUsedCheckBox;

    @FXML
    private Button newCourseButton;

    @FXML
    private ListView<Lecture> lecturesListView;

    @FXML
    private Label courseNameLabel;

    @FXML
    private Button newLectureButton;

    @FXML
    private Button importSlideButton;

    @FXML
    private ListView<Slide> slidesListView;

    @FXML
    private Label courseAndLectureName;

    @FXML
    private ProgressBar slideImportProgressBar;

    @FXML
    private Button recordingSessionButton;

    @FXML
    private Button postProcessButton;

    @FXML
    private Button exitWizardButton;




    public class CourseFormatCell extends ListCell<Course> {
        @Override
        protected void updateItem(Course item, boolean empty) {
            super.updateItem(item, empty);
            if( empty || item==null ){
                setText(null);
                setGraphic(null);
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
            if (newCourse != null) {
                refreshLectures(newCourse.lectures());
                tabPane.getSelectionModel().select(1);
                courseNameLabel.setText(newCourse.name());
            }
        });
        lecturesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldLecture, newLecture) -> {
            if (newLecture != null) {
                refreshSlides(newLecture.slides());
                tabPane.getSelectionModel().select(2);
                courseAndLectureName.setText(newLecture.name());
            }
        });

        newCourseButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("new course name");
            dialog.setTitle("New course creation");
            dialog.setHeaderText("Insert the name of the new course.");
            dialog.setContentText("Course name:");
            dialog.showAndWait().ifPresent(name -> {
                Course course = new XmlCourseImpl(lodePath + name);
                course.setName(name);
                course.setYear(Calendar.getInstance().get(Calendar.YEAR));
                course.save();
                setLastCourse(course);
                refreshCourses();
            });
        });

        newLectureButton.setOnAction(event -> {
            Course course = coursesListView.getSelectionModel().selectedItemProperty().get();
            TextInputDialog dialog = new TextInputDialog("new lecture title");
            dialog.setTitle("New lecture creation");
            dialog.setHeaderText("Insert the title of the new lecture of the '" + course.name() + "' course.");
            dialog.setContentText("Title:");
            dialog.showAndWait().ifPresent(title -> {
                int size = lecturesListView.getItems().size();
                Integer number = size + 1;
                String name = String.format("%02d", number) + " " + title;
                String lecturer = "";
                Lecture lecture = new XmlLectureImpl((XmlCourseImpl) course, name);
                lecture.setName(name);
                lecture.setLecturer(lecturer);
                lecture.setNumber(number);
                lecture.save();
                course.addLecture(lecture);
                course.save();
                setLastCourse(course);
                refreshLectures(course.lectures());

            });
        });

        importSlideButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Select pdf file to import", "*.pdf");
            fileChooser.getExtensionFilters().add(extension);
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());
            Lecture lecture = lecturesListView.getSelectionModel().selectedItemProperty().get();
            SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
            slideImportProgressBar.progressProperty().bind(progress);
            /*
            PdfJuicerImpl.build().slide(file).output(lecture.path() + "/Slides/")
                    .extract().stream().forEach(s -> lecture.addSlide(s));
                    */
            Juicer juicer = PdfJuicerImpl.build();
            Iterator<Slide> iterator = juicer.slide(file).output(lecture.path() + "/Slides/").iterator();
            double d = 1.0 / juicer.size();
            while (iterator.hasNext()) {
                lecture.addSlide(iterator.next());
                Platform.runLater(() -> progress.set(progress.getValue() + d));
            }
            progress.set(1.0);
            lecture.save();
            Course course = coursesListView.getSelectionModel().selectedItemProperty().get();
            setLastCourse(course);
            refreshCourses();
        });

        recordingSessionButton.setOnAction(event -> {
            Course course = coursesListView.getSelectionModel().selectedItemProperty().get();
            try {
                RecordingSessionLaucher.launch(new Stage(), course.path());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        postProcessButton.setOnAction(event -> {
            Lecture lecture = lecturesListView.getSelectionModel().selectedItemProperty().get();
            PostProducer producer = IOC.queryUtility(PostProducer.class);
            producer.convert(lecture);
            producer.createDistribution(lecture);
        });

        exitWizardButton.setOnAction(event -> {
            File lodeHome = new File(Constants.LODE_COURSES);
            List<Course> courses = Arrays.asList(lodeHome.list((dir, name) -> new File(dir, name).isDirectory())).stream().map(n -> new XmlCourseImpl(Constants.LODE_COURSES + n)).collect(Collectors.toList());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/courses.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CoursesController controller = loader.getController();
            controller.setCourses(courses);
            Stage stage = new Stage();
            stage.setTitle("Courses management");
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setOnCloseRequest(controller.handlerClose);
            stage.show();
            //((Stage) root.getScene().getWindow()).close();
        });

        onlyLastUsedCheckBox.setOnAction(event -> {
            refreshCourses();
        });

    }

    private void refreshPane(){
        tabPane.getTabs().get(1).setDisable(true);
        tabPane.getTabs().get(2).setDisable(true);
        tabPane.getTabs().get(2).setDisable(true);
        tabPane.getTabs().get(2).setDisable(true);
    }

    public void setLodeCoursesPath(String path){
        lodePath = path;
        refreshCourses();
    }

    private void refreshCourses() {
        LodePrefs prefs = IOC.queryUtility(LodePrefs.class);
        List<String> lastUsedPaths = prefs.lastUsedCourses().stream().map(c -> c.path()).collect(Collectors.toList());
        File lodeHome = new File(lodePath);
        Stream<XmlCourseImpl> stream = Arrays.asList(lodeHome.list((dir, name) -> new File(dir, name).isDirectory()))
                .stream()
                .map(n -> new XmlCourseImpl(lodePath + n));
        if( onlyLastUsedCheckBox.isSelected() ){
            stream = stream.filter(c -> lastUsedPaths.contains(c.getFolderPath()));
        }
        List<Course> courses = stream.collect(Collectors.toList());
        coursesListView.setItems(FXCollections.observableList(courses));
    }

    private void refreshLectures(List<Lecture> lectures){
        Collections.reverse(lectures);
        lecturesListView.setItems(FXCollections.observableList(lectures));
    }

    private void refreshSlides(List<Slide> slides){
        slidesListView.setItems(FXCollections.observableList(slides));
    }

    private Node createGraphicNode(String name){
        return new ImageView(new Image(CoursesController.class.getResourceAsStream("/images/" + name + ".png")));
    }

    private void setLastCourse(Course course){
        LodePrefs lodePrefs = IOC.queryUtility(LodePrefs.class);
        lodePrefs.setLastUsedCourse(course);
        lodePrefs.save();
    }
}
