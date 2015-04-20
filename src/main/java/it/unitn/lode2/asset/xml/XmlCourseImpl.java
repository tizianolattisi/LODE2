package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.AbstractCourse;
import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import it.unitn.lode2.xml.XMLHelper;
import it.unitn.lode2.xml.course.XMLCourse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:39
 */
public class XmlCourseImpl extends AbstractCourse implements Course {

    private String folderPath;
    private XMLCourse course;

    public XmlCourseImpl(String folderPath) {
        if( !folderPath.endsWith("/") ){
            folderPath += "/";
        }
        this.folderPath = folderPath;
        File f = new File(this.folderPath + "COURSE.XML");
        if( f.exists() ) {
            course = XMLHelper.build(XMLCourse.class).unmarshal(f);
        } else {
            course = new XMLCourse();
        }
    }

    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public String path() {
        return folderPath;
    }

    @Override
    public String name() {
        return course.getName();
    }

    @Override
    public void setName(String name) {
        course.setName(name);
    }

    @Override
    public Integer year() {
        return course.getYear();
    }

    @Override
    public void setYear(Integer year) {
        course.setYear(year);
    }

    @Override
    public List<Lecture> lectures() {
        List<Lecture> lectures = new ArrayList<>();
        for( String lectureName: course.getXMLCourseLectures().getLectures() ){
            Lecture lecture = new XmlLectureImpl(this, lectureName);
            lectures.add(lecture);
        }
        return lectures;
    }

    @Override
    public void addLecture(Lecture lecture) {

    }

    @Override
    public List<String> teachers() {
        return course.getXMLCourseTeachers().getTeachers();
    }

    @Override
    public void addTeacher(String teacher) {
        course.getXMLCourseTeachers().addTeacher(teacher);
    }

    @Override
    public void save() {
        XMLHelper.build(XMLCourse.class).marshall(course, new File(folderPath + "/COURSE.XML"));
    }
}
