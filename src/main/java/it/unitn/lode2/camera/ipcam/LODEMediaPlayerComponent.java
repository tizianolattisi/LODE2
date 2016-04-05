package it.unitn.lode2.camera.ipcam;

import javafx.scene.canvas.Canvas;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;

/**
 * Created by tiziano on 05/04/16.
 */
public class LODEMediaPlayerComponent extends DirectMediaPlayerComponent {

    public LODEMediaPlayerComponent(Canvas canvas, Integer width, Integer height) {
        super(new PreviewBufferFormatCallback(canvas, width, height));
    }
}