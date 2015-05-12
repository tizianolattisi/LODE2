package it.unitn.lode2.camera;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Created by tiziano on 10/03/15.
 */
public interface Previewer {

    Optional<InputStream> snapshot() throws IOException;

    void setPreviewMode(PreviewMode mode);

    void start();

    void stop();

}
