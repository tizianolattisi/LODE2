package it.unitn.lode2.camera;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Created by tiziano on 10/03/15.
 */
public interface Previewer {

    Optional<InputStream> snapshot() throws IOException;

    void takeSnapshotPreview(Integer i) throws IOException;

    Optional<InputStream> getSnapshotPreview(Integer i);

    void setPreviewMode(PreviewMode mode);

    void start();

    void stop();

}
