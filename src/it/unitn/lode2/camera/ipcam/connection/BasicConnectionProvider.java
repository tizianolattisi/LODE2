package it.unitn.lode2.camera.ipcam.connection;

/**
 * Created by tiziano on 29/03/15.
 */
public class BasicConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    public BasicConnectionProvider(String username, String password) {
        super(AuthMode.BASIC, username, password);
    }
}
