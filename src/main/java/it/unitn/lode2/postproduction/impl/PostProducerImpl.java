package it.unitn.lode2.postproduction.impl;

import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.mapformat.MessageMapFormat;
import it.unitn.lode2.postproduction.PostProducer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tiziano on 26/05/15.
 */
public class PostProducerImpl implements PostProducer{

    private Process recordProcess=null;

    private String partialCommand;
    private String ffmpeg;
    private String commandTemplate;

    public PostProducerImpl(String commandTemplate, String ffmpeg) {
        this.ffmpeg = ffmpeg;
        this.commandTemplate = commandTemplate;

    }

    @Override
    public void convert(Lecture lecture) {
        MessageMapFormat mmp = new MessageMapFormat(commandTemplate);
        Map<String, Object> map = new HashMap();
        map.put("ffmpeg", ffmpeg);
        String input = lecture.path() + "/movie001.mov";
        map.put("input", "${input}");
        this.partialCommand = mmp.format(map);
        try {
            List<String> command = new ArrayList(Arrays.asList(partialCommand.split(" ")));
            command = command.stream().map(s -> !"${input}".equals(s) ? s : input).collect(Collectors.toList());
            command.add(lecture.path() + "/movie.mp4");
            System.out.println(command);
            recordProcess = new ProcessBuilder(command).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
