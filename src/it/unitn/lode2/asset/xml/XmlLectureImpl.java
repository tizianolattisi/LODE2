package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.AbstractLecture;
import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.lecture.XMLLecture;

import java.io.File;
import java.io.StringWriter;
import java.util.Date;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 15:12
 */
public class XmlLectureImpl extends AbstractLecture implements Lecture {

    private String folderPath;
    private XMLLecture lecture;
    private Course course;

    public XmlLectureImpl(XmlCourseImpl course, String folderName) {
        folderPath = course.getFolderPath() + "Acquisition/" + folderName;
        lecture = XMLHelper.build(XMLLecture.class).unmarshal(new File(folderPath + "/LECTURE.XML"));
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
    public Long videoLength() {
        return null;
    }

    @Override
    public void setVideoLength(Long length) {

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
}
