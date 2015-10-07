package it.unitn.lode2.remote.smartphone;

/**
 * Created by Tiziano on 07/10/2015.
 */
public class SmartPhoneRemoteBuilder {

    private String host="127.0.0.1";
    private Integer port=80;


    public static SmartPhoneRemoteBuilder create() {
        return new SmartPhoneRemoteBuilder();
    }

    public SmartPhoneRemoteBuilder host(String host) {
        this.host = host;
        return this;
    }

    public SmartPhoneRemoteBuilder port(Integer port) {
        this.port = port;
        return this;
    }


    public SmartPhoneRemoteImpl build(){
        SmartPhoneRemoteImpl remote = new SmartPhoneRemoteImpl(host, port);
        return remote;
    }

}
