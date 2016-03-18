package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
import org.junit.Test;

public class CameraIPImplTest {

    @Test
    public void testZoomInAndOut() throws Exception {

        Camera cameraIP = CameraIPBuilder.create()
                .user("admin")
                .password("admin1")
                .host("192.168.1.2")
                .port(80)
                .template(Cmds.PANLEFT, "/cgi/ptdc.cgi?command=set_relative_pos&amp;posX=-5&amp;posY=0")
                .template(Cmds.PANRIGHT, "/cgi/ptdc.cgi?command=set_relative_pos&amp;posX=5&amp;posY=0")
                .build();

        assert cameraIP.hasCapability(Capability.PAN);
        // TODO: use ConnectionProvider
        //cameraIP.panLeft();
        //cameraIP.panRight();

    }

}