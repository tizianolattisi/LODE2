package it.unitn.lode2.postproduction.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unitn.lode2.asset.Course;
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
            recordProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

        // sources
        File slidesFileSourceDir = new File(lecture.path() + "/Sources");
        File slidesFileDestDir = new File(distributionDir + "/content/sources");
        try {
            FileUtils.copyDirectory(slidesFileSourceDir, slidesFileDestDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createWebsite(Course course) {
        // template
        File srcDir = new File(PostProducer.class.getResource("/templateindexhtml").getFile());
        File destDir = new File(course.path() + "/Website");
        try {
            if( srcDir.exists() ) {
                FileUtils.deleteDirectory(destDir);
            }
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // lectures
        File lecturesDir = new File(course.path() + "/Website/lectures");
        lecturesDir.mkdir();
        for( Lecture lecture: course.lectures() ){
            File lectureSrcDir = new File(course.path() + "/Distribution/" + lecture.name());
            File lectureDestDir = new File(course.path() + "/Website/lectures/" + lecture.name());
            try {
                FileUtils.copyDirectory(lectureSrcDir, lectureDestDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // json
        List<Map<String, Object>> lectures = new ArrayList<>();
        for( Lecture lecture: course.lectures() ) {
            Map<String, Object> lectureData = new HashMap<>();
            lectureData.put("number", lecture.number().toString());
            lectureData.put("lenght", lecture.videoLength());
            lectureData.put("lecturer", lecture.lecturer());
            lectureData.put("title", lecture.name());
            lectureData.put("slides", "lectures/" + lecture.name() + "/sources/");
            lectureData.put("video", "lectures/" + lecture.name() + "/index.html");
            lectureData.put("zip", "downloads/" + lecture.name() + ".zip");
            lectureData.put("note", "...uhm... e le note da dove le prendo?");
            if (lecture.date() != null) {
                lectureData.put("date", lecture.date().toString());
            } else {
                lectureData.put("date", "-");
            }
            lectures.add(lectureData);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(course.path() + "/Website/lectures.json"), lectures);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
