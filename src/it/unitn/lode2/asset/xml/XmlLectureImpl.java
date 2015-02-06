package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.*;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.lecture.XMLLecture;
import it.unitn.lode2.xml.slides.XMLLodeSlides;
import it.unitn.lode2.xml.timedslides.XMLTimedSlides;
import it.unitn.lode2.xml.timedslides.XMLTimedSlidesSlide;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 15:12
 */
public class XmlLectureImpl extends AbstractLecture implements Lecture {

    private String folderPath;
    private XMLLecture lecture;
    private Course course;
    private XMLLodeSlides slides;
    private List<TimedSlide> timedSlides = new ArrayList<>();

    public XmlLectureImpl(XmlCourseImpl course, String folderName) {
        folderPath = course.getFolderPath() + "Acquisition/" + folderName;
        lecture = XMLHelper.build(XMLLecture.class).unmarshal(new File(folderPath + "/LECTURE.XML"));
        slides = XMLHelper.build(XMLLodeSlides.class).unmarshal(new File(this.folderPath + "/SLIDES.XML"));
        this.course = course;
    }

    @Override
    public String name() {
        return lecture.getName();
    }

    @Override
    public void setName(String name) {
        lecture.setName(name);
    }

    @Override
    public Date date() {
        return lecture.getDate();
    }

    @Override
    public void setDate(Date date) {
        lecture.setDate(date);
    }

    @Override
    public Integer number() {
        return lecture.getSequenceNumber();
    }

    @Override
    public void setNumber(Integer number) {
        lecture.setSequenceNumber(number);
    }

    @Override
    public String lecturer() {
        return lecture.getLecturer();
    }

    @Override
    public void setLecturer(String lecturer) {
        if( !course().teachers().contains(lecturer) ){
            course().addTeacher(lecturer);
        }
        lecture.setLecturer(lecturer);
    }

    @Override
    public Course course() {
        return course;
    }

    @Override
    public List<Slide> slides() {
        return slides.getXMLLodeSlidesSlides().getSlides()
                .stream()
                .map(s -> (Slide) new XmlSlideImpl(s.getFileName(), s.getTitle(), s.getText()))
                .collect(Collectors.toList());
    }

    @Override
    public Slide slide(Integer seqNumber) {
        return slides.getXMLLodeSlidesSlides().getSlides()
                .stream()
                .filter(s -> s.getSequenceNumber().equals(seqNumber))
                .map(s -> (Slide) new XmlSlideImpl(s.getFileName(), s.getTitle(), s.getText()))
                .collect(Collectors.toList())
                .get(0);
    }

    @Override
    public void addSlide(Slide slide) {

    }

    @Override
    public void addTimedSlide(Slide slide, Long second) {
        timedSlides.add(new XmlTimedSlide(slide, second));
    }

    @Override
    public List<TimedSlide> timedSlides() {
        return timedSlides;
    }

    @Override
    public Long videoLength() {
        return lecture.getVideoLength();
    }

    @Override
    public void setVideoLength(Long length) {
        lecture.setVideoLength(length);
    }

    @Override
    public Boolean hasPostProcessing() {
        return null;
    }

    @Override
    public void setHasPostProcessing(Boolean b) {

    }

    @Override
    public Boolean hasPostProcessing4iTunesU() {
        return null;
    }

    @Override
    public void setHasPostProcessing4iTunesU(Boolean b) {

    }

    @Override
    public void save() {
        XMLHelper.build(XMLLecture.class).marshall(lecture, new File(folderPath + "/LECTURE.XML"));
        XMLHelper.build(XMLLodeSlides.class).marshall(slides, new File(folderPath + "/SLIDES.XML"));
        XMLTimedSlides xmlTimedSlides = new XMLTimedSlides();
        for( TimedSlide s: timedSlides() ) {
            XMLTimedSlidesSlide xmlTimedSlidesSlide = new XMLTimedSlidesSlide();
            xmlTimedSlidesSlide.setTitle(s.slide().title());
            xmlTimedSlidesSlide.setTime(s.time());
            String filename = s.slide().filename();
            String newFilename = "img" + filename.substring(filename.lastIndexOf("/"), filename.length());
            xmlTimedSlidesSlide.setPath(newFilename);
            xmlTimedSlides.addSlide(xmlTimedSlidesSlide);
        }
        XMLHelper.build(XMLTimedSlides.class).marshall(xmlTimedSlides, new File(folderPath + "/TIMED_SLIDES.XML"));
    }

}
