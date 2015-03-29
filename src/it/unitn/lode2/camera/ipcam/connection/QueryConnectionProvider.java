package it.unitn.lode2.camera.ipcam.connection;

import com.axiastudio.mapformat.MessageMapFormat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tiziano on 29/03/15.
 */
public class QueryConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    private final String authString;

    public QueryConnectionProvider(String user, String password, String template) {
        super(AuthMode.QUERY);
        MessageMapFormat mmp = new MessageMapFormat(template);
        Map<String, Object> map = new HashMap();
        map.put("user", user);
        map.put("password", password);
        authString = mmp.format(map);
    }

    @Override
    public HttpURLConnection createConnection(URL url) throws IOException {
        Integer port = url.getDefaultPort();
        if( url.getPort() != -1 )
            port = url.getPort();
        String sUrl = url.getProtocol() + "//" + url.getHost() + ":" + port + url.getPath() + "?" + authString + "&amp;" + url.getQuery();
        url = new URL(sUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }
}
