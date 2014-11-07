package it.unitn.lode2.cam;

import java.io.IOException;

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

    public void panLeft() throws IOException;
    public void panRight() throws IOException;

}
