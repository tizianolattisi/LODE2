package it.unitn.lode2.camera.ipcam.connection;

/**
 * Created by tiziano on 29/03/15.
 */
public class NoAuthConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    public NoAuthConnectionProvider() {
        super(AuthMode.NONE, null, null);
    }

    // Nothing to implement
}
