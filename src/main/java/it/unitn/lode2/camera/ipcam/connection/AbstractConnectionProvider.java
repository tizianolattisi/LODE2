package it.unitn.lode2.camera.ipcam.connection;


/**
 * Created by tiziano on 29/03/15.
 */
public abstract class AbstractConnectionProvider implements ConnectionProvider {

    protected final AuthMode mode;

    public AbstractConnectionProvider(AuthMode mode) {
        this.mode = mode;
    }

}
