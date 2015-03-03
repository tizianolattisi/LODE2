package it.unitn.lode2.camera;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 18:39
 */
public interface Camera {

    public Boolean hasCapability(Capability capability);
    public void setCapabilities(Capability... capabilities);
    public void addCapability( Capability capability);

    public void zoomIn() throws IOException;
    public void zoomOut() throws IOException;
    public void zoomStop() throws IOException;

    public void panLeft() throws IOException;
    public void panRight() throws IOException;
    public void panStop() throws IOException;

    public void tiltUp() throws IOException;
    public void tiltDown() throws IOException;
    public void tiltStop() throws IOException;

    public void goToPreset(String preset) throws IOException;
    public void delPreset(String preset) throws IOException;
    public void addPreset(String preset) throws IOException;

    public Optional<InputStream> snapshot() throws IOException;
}
