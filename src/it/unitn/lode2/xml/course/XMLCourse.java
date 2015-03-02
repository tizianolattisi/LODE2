package it.unitn.lode2.xml.course;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:47
 */
@XmlRootElement(name = "COURSE")
public class XMLCourse {

    private String name;

    private Integer year;

    private String courseHome;

    private XMLCourseLectures XMLCourseLectures = new XMLCourseLectures();

    private XMLCourseTeachers XMLCourseTeachers = new XMLCourseTeachers();

    @XmlElement(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "YEAR")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void addLecture(String title){
        XMLCourseLectures.addLecture(title);
    }

    public void addTeacher(String teacher){
        XMLCourseTeachers.addTeacher(teacher);
    }

    @XmlElement(name = "LECTURES")
    public XMLCourseLectures getXMLCourseLectures() {
        return XMLCourseLectures;
    }

    public void setXMLCourseLectures(XMLCourseLectures XMLCourseLectures) {
        this.XMLCourseLectures = XMLCourseLectures;
    }

    @XmlElement(name = "TEACHERS")
    public XMLCourseTeachers getXMLCourseTeachers() {
        return XMLCourseTeachers;
    }

    public void setXMLCourseTeachers(XMLCourseTeachers XMLCourseTeachers) {
        this.XMLCourseTeachers = XMLCourseTeachers;
    }

    @XmlElement(name = "COURSE_HOME")
    public String getCourseHome() {
        return courseHome;
    }

    public void setCourseHome(String courseHome) {
        this.courseHome = courseHome;
    }
}
