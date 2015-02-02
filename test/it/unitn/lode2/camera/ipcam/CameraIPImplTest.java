package it.unitn.lode2.camera.ipcam;

import it.unitn.lode2.camera.Camera;
import it.unitn.lode2.camera.Capability;
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