package it.unitn.lode2.camera;

import java.io.IOException;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 18:39
 */
public interface Camera {

    Boolean hasCapability(Capability capability);
    void setCapabilities(Capability... capabilities);
    void addCapability( Capability capability);

    void zoomIn() throws IOException;
    void zoomOut() throws IOException;
    void zoomStop() throws IOException;

    void panLeft() throws IOException;
    void panRight() throws IOException;
    void panStop() throws IOException;

    void tiltUp() throws IOException;
    void tiltDown() throws IOException;
    void tiltStop() throws IOException;

    void goToPreset(String preset) throws IOException;
    void delPreset(String preset) throws IOException;
    void addPreset(String preset) throws IOException;

}
