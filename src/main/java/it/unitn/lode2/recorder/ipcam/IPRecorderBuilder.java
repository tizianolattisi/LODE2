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
    private String url="/";
    private String user=null;
    private String password=null;
    private String recordCommand;
    private String output ="movie.mp4";
    private String ffmpeg=null;

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

    public IPRecorderBuilder url(String url) {
        this.url = url;
        return this;
    }

    public IPRecorderBuilder user(String user) {
        this.user = user;
        return this;
    }

    public IPRecorderBuilder password(String password) {
        this.password = password;
        return this;
    }

    public IPRecorderBuilder protocol(IPRecorderProtocol protocol){
        this.protocol = protocol;
        return this;
    }

    public IPRecorderBuilder recordCommand(String recordCommand) {
        this.recordCommand = recordCommand;
        return this;
    }

    public IPRecorderBuilder ffmpeg(String path) {
        this.ffmpeg = path;
        return this;
    }

    public IPRecorderBuilder output(String output) {
        this.output = output;
        return this;
    }

    public IPRecorderImpl build(){
        // TODO: per ora il builder si limita a creare l'istanza
        IPRecorderImpl recorder = new IPRecorderImpl(host, port, protocol, url, user, password, recordCommand, output, ffmpeg);
        return recorder;
    }
}
