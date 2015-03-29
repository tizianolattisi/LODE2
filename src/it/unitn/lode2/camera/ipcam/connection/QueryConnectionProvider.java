package it.unitn.lode2.camera.ipcam.connection;

/**
 * Created by tiziano on 29/03/15.
 */
public class QueryConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    public QueryConnectionProvider(String username, String password) {
        super(AuthMode.QUERY, username, password);
    }

    // TODO: to implement
}
