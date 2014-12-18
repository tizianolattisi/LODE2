package it.unitn.lode2.recorder.ipcam;

/**
 * User: tiziano
 * Date: 06/11/14
 * Time: 19:32
 */
public class IPRecorderBuilder {

    private String host="127.0.0.1";

    private Integer port=80;

    private IPRecorderProtocol protocol=IPRecorderProtocol.HTTP;

    public static IPRecorderBuilder create(){
        return new IPRecorderBuilder();
    }

    public IPRecorderBuilder host(String host) {
        this.host = host;
        return this;
    }

    public IPRecorderBuilder port(Integer port) {
        this.port = port;
        return this;
    }

    public IPRecorderBuilder protocol(IPRecorderProtocol protocol){
        this.protocol = protocol;
        return this;
    }

    public IPRecorderImpl build(){
        IPRecorderImpl recorder = new IPRecorderImpl();
        return recorder;
    }
}
