package it.unitn.lode2.ui.controllers;

import com.sun.jna.Memory;
import it.unitn.lode2.camera.ipcam.LODEMediaPlayerComponent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;

import java.nio.ByteBuffer;

/**
 * User: tiziano
 * Date: 11/05/15
 * Time: 19:52
 */
public class SecondDisplay extends Stage {

    ImageView slideImageView = new ImageView();
    Canvas previewCanvas = new Canvas();
    VBox browser = new VBox();
    ComboBox<String> addresses = new ComboBox<>();
    WebView browserWebView = new WebView();
    private final Pane pane;

    private LODEMediaPlayerComponent mediaPlayerComponent;
    private PixelWriter pixelWriter;
    private WritablePixelFormat<ByteBuffer> byteBgraInstance;

    private Timeline timeline;


    public SecondDisplay(Rectangle2D bounds) {
        setX(bounds.getMinX());
        setY(bounds.getMinY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());

        pane = new Pane();
        Scene scene = new Scene(pane);
        setScene(scene);

        addresses.setItems(FXCollections.observableArrayList("http://www.google.it", "http://www.oracle.com", "http://192.168.2.1"));
        addresses.setOnAction(event -> setURL(addresses.getSelectionModel().selectedItemProperty().get()));
        browser.getChildren().add(addresses);
        browser.getChildren().add(browserWebView);


        pixelWriter = previewCanvas.getGraphicsContext2D().getPixelWriter();
        byteBgraInstance = PixelFormat.getByteBgraInstance();
        mediaPlayerComponent = new LODEMediaPlayerComponent(previewCanvas, 800, 600);
        mediaPlayerComponent.getMediaPlayer().playMedia("rtsp://admin:admin@192.168.102.50:88/videoMain");
        mediaPlayerComponent.getMediaPlayer().setPosition(0.7f);

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(40), event -> renderFrame()));

        switchMode(DisplayMode.PREVIEW);

    }

    public void switchMode(DisplayMode mode){
        pane.getChildren().clear();
        timeline.stop();
        if( DisplayMode.SLIDES.equals(mode) ){
            setFullScreen(true);
            toFront();
            pane.getChildren().add(slideImageView);
        } else if( DisplayMode.PREVIEW.equals(mode) ){
            setFullScreen(true);
            toFront();
            pane.getChildren().add(previewCanvas);
            timeline.play();
        } else if( DisplayMode.DESKTOP.equals(mode) ){
            setFullScreen(false);
            toBack();
        } else if( DisplayMode.BROWSER.equals(mode) ){
            setFullScreen(true);
            toFront();
            pane.getChildren().add(browser);
        }
    }

    public void setImage(Image image){
        slideImageView.setImage(image);
    }

    public void setURL(String url){
        browserWebView.getEngine().load(url);
    }

    protected final void renderFrame() {
        Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            // FIXME there may be more efficient ways to do this...
            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
            // invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), byteBgraInstance, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayerComponent.getMediaPlayer().unlock();
    }

    protected void dispose() {
        if( mediaPlayerComponent.getMediaPlayer().isPlaying() ){
            mediaPlayerComponent.getMediaPlayer().stop();
        }
        mediaPlayerComponent.getMediaPlayer().release();
    }
}
