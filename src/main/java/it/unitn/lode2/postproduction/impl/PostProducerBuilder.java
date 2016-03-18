package it.unitn.lode2.postproduction.impl;

/**
 * Created by tiziano on 26/05/15.
 */
public class PostProducerBuilder {

    private String convCommand;
    private String ffmpeg;

    public static PostProducerBuilder create() {
        return new PostProducerBuilder();
    }

    public PostProducerBuilder command(String command){
        convCommand = command;
        return this;
    }

    public PostProducerBuilder ffmpeg(String path) {
        this.ffmpeg = path;
        return this;
    }

    public PostProducerImpl build(){
        PostProducerImpl postProducer = new PostProducerImpl(convCommand, ffmpeg);
        return postProducer;
    }
}
