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
import it.unitn.lode2.asset.xml.XmlSlideImpl;
import it.unitn.lode2.postproduction.PostProducer;
import it.unitn.lode2.slidejuicer.Juicer;
import it.unitn.lode2.slidejuicer.pdf.PdfJuicerImpl;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private Button editSlideButton;

    @FXML
    private Button importSlideButton;

    @FXML
    private Button skipSlideButton;

    @FXML
    private Label movieFileLabel;

    @FXML
    private Button toPostProcessButton;

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
    private Button createWebsiteButton;

    @FXML
    private Button exitWizardButton;


    private IntConsumer refreshPane = (s) -> IntStream.range(0, tabPane.getTabs().size()).forEach(i -> tabPane.getTabs().get(i).setDisable(i>s));
    private Course course = null;
    private Lecture lecture = null;
    private Boolean lectureHasMovie = Boolean.FALSE;

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
            if (newCourse != null && newCourse != course) {
                setCurrentCourse(newCourse);
                refreshLectures(newCourse.lectures());
                tabPane.getSelectionModel().select(1);
                refreshPane.accept(1);
                courseNameLabel.setText(newCourse.name());
            }
        });
        lecturesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldLecture, newLecture) -> {
            if (newLecture != null && newLecture != lecture) {
                setCurrentLecture(newLecture);
                refreshSlides(newLecture.slides());
                if( newLecture.slides().size()>0 ) {
                    tabPane.getSelectionModel().select(2);
                    refreshPane.accept(3);
                } else {
                    tabPane.getSelectionModel().select(2);
                    refreshPane.accept(2);
                }
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
                setCurrentCourse(course);
                setLastCourse(course);
                refreshCourses();
            });
        });

        newLectureButton.setOnAction(event -> {
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
                lecture.setDate(new Date());
                lecture.save();
                setCurrentLecture(lecture);
                course.addLecture(lecture);
                course.save();
                setLastCourse(course);
                refreshLectures(course.lectures());
            });
        });

        skipSlideButton.setOnAction(event -> {
            refreshPane.accept(3);
            tabPane.getSelectionModel().select(3);
        });

        toPostProcessButton.setOnAction(event -> {
            refreshPane.accept(4);
            tabPane.getSelectionModel().select(4);
        });

        editSlideButton.setOnAction(event -> {
            int position = slidesListView.getSelectionModel().getSelectedIndex();
            XmlSlideImpl slide = (XmlSlideImpl) slidesListView.getSelectionModel().getSelectedItem();
            if( slide != null ){
                TextInputDialog dialog = new TextInputDialog(slide.title());
                dialog.setTitle("Edit slide title");
                dialog.setContentText("Title:");
                dialog.showAndWait().ifPresent(t -> {
                    slide.setTitle(t);
                    lecture.replaceSlide(position, slide);
                    refreshSlides(lecture.slides());
                    lecture.save();
                });
            }
        });

        importSlideButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("Select pdf file to import", "*.pdf");
            fileChooser.getExtensionFilters().add(extension);
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());

            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    Juicer juicer = PdfJuicerImpl.build();
                    Iterator<Slide> iterator = juicer.slide(file).output(lecture.path() + "/Slides/").iterator();
                    Integer n = juicer.size();
                    Integer done = 0;
                    while (iterator.hasNext()) {
                        lecture.addSlide(iterator.next());
                        updateProgress(++done, n);
                    }
                    updateProgress(n, n);
                    lecture.save();
                    return null;
                }

                @Override
                protected void succeeded() {
                    refreshSlides(lecture.slides());
                    refreshPane.accept(3);
                }
            };

            slideImportProgressBar.progressProperty().bind(task.progressProperty());
            Thread thread = new Thread(task);
            //thread.setDaemon(true);
            thread.start();

            lecture.save();
            File destFile = new File(lecture.path() + "/Sources/" + file.getName());
            try {
                FileUtils.copyFile(file, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setLastCourse(course);
            //refreshCourses();
            //refreshSlides(lecture.slides());
        });

        recordingSessionButton.setOnAction(event -> {
            if( lectureHasMovie ){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Open recording session");
                alert.setHeaderText("The movie in this lecture will be overwritten.");
                alert.setContentText("Are you ok with this?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK){
                    return;
                }
            }
            try {
                RecordingSessionLaucher.launch(new Stage(), course.path(), lecture.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        postProcessButton.setOnAction(event -> {
            PostProducer producer = IOC.queryUtility(PostProducer.class);
            producer.convert(lecture);
            producer.createDistribution(lecture);
        });

        createWebsiteButton.setOnAction(event -> {
            PostProducer producer = IOC.queryUtility(PostProducer.class);
            producer.createWebsite(course);
        });

        /* Disabled
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
        */

        onlyLastUsedCheckBox.setOnAction(event -> {
            refreshCourses();
        });

        // Tab setup
        refreshPane.accept(0);

    }

    private void setCurrentCourse(Course course){
        if( course != this.course) {
            this.course = course;
            lecture = null;
        }
    }

    private void setCurrentLecture(Lecture lecture){
        this.lecture = lecture;
        course = lecture.course();
        String moviePath = lecture.path() + "/movie001.mov";//XXX: se più spezzoni?
        File movieFile = new File(moviePath);
        String out = "No recording session present.";
        if( movieFile.exists() ){
            out = "Recording session:  " + readableFileSize(movieFile.length());
            lectureHasMovie = Boolean.TRUE;
        }
        movieFileLabel.setText(out);
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

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
