package it.unitn.lode2.camera.ipcam;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/**
 * Created by tiziano on 05/04/16.
 */
public class PreviewBufferFormatCallback implements BufferFormatCallback {

    private final Integer width;
    private final Integer height;
    private final Canvas canvas;

    public PreviewBufferFormatCallback(Canvas canvas, Integer width, Integer height) {
        this.canvas = canvas;
        this.width = width;
        this.height = height;
    }

    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        Platform.runLater(() -> {
            canvas.setWidth(width);
            canvas.setHeight(height);
        });
        return new RV32BufferFormat(width, height);
    }
}