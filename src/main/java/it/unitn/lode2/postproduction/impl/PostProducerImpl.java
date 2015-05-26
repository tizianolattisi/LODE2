package it.unitn.lode2.postproduction.impl;

import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.mapformat.MessageMapFormat;
import it.unitn.lode2.postproduction.PostProducer;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.distribution.XMLData;
import it.unitn.lode2.xml.distribution.XMLDataInfo;
import it.unitn.lode2.xml.distribution.XMLDataVideo;
import it.unitn.lode2.xml.timedslides.XMLTimedSlides;
import org.apache.commons.io.FileUtils;

import java.io.File;
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

    @Override
    public void createDistribution(Lecture lecture) {
        String[] split = lecture.path().split("/");
        String name = split[split.length - 1];
        String distributionDir = lecture.path() + "/../../Distribution/" + name;

        // template
        File srcDir = new File(PostProducer.class.getResource("/templatehtml").getFile());
        File destDir = new File(distributionDir);
        try {
            if( srcDir.exists() ) {
                FileUtils.deleteDirectory(destDir);
            }
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // movie
        File srcMovieFile = new File(lecture.path() + "/movie.mp4");
        File destMovieFile = new File(distributionDir + "/content/movie.mp4");
        try {
            FileUtils.copyFile(srcMovieFile, destMovieFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // slides
        if( lecture.slides().size()>0 ){
            File slidesSourceDir = new File(lecture.path() + "/Slides");
            File slidesDestDir = new File(distributionDir + "/content/img");
            try {
                FileUtils.copyDirectory(slidesSourceDir, slidesDestDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // data
        XMLData data = new XMLData();
        XMLHelper.build(XMLTimedSlides.class)
                .unmarshal(new File(lecture.path() + "/TIMED_SLIDES.XML"))
                .slides()
                .stream()
                .forEach(s -> data.addSlide(s));
        XMLDataVideo video = new XMLDataVideo();
        video.setNome("movie.mp4");
        video.setStartime(0L);
        video.setTotaltime(lecture.videoLength());
        data.setVideo(video);
        XMLDataInfo info = new XMLDataInfo();
        info.setCorso(lecture.course().name());
        info.setProfessore(lecture.lecturer());
        info.setTitolo(lecture.name());
        info.setDynamic_url("http://latemar.science.unitn.it/LODE");
        data.setInfo(info);
        XMLHelper.build(XMLData.class).marshall(data, new File(distributionDir + "/content/data.xml"));
    }
}
