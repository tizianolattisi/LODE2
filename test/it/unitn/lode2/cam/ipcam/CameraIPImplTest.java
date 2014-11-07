package it.unitn.lode2.cam.ipcam;

import it.unitn.lode2.cam.Camera;
import it.unitn.lode2.cam.Capability;
import org.junit.Test;

public class CameraIPImplTest {

    @Test
    public void testZoomInAndOut() throws Exception {

        Camera cameraIP = CameraIPBuilder.create()
                .user("admin")
                .password("admin")
                .host("192.168.1.142")
                .port(88)
                .template(Cmds.ZOOMIN, "/cgi-bin/CGIProxy.fcgi?cmd=zoomIn&usr=${user}&pwd=${password}")
                .template(Cmds.ZOOMOUT, "/cgi-bin/CGIProxy.fcgi?cmd=zoomOut&usr=${user}&pwd=${password}")
                .build();

        assert cameraIP.hasCapability(Capability.ZOOM);
        cameraIP.zoomIn();
        cameraIP.zoomOut();

    }
}