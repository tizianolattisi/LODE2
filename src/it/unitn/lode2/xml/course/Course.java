package it.unitn.lode2.xml.course;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:47
 */
@XmlRootElement(name = "COURSE")
public class Course {

    private String name;

    private Integer year;

    private Lectures lectures = new Lectures();

    private Teachers teachers = new Teachers();

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
        lectures.addLecture(title);
    }

    public void addTeacher(String teacher){
        teachers.addTeacher(teacher);
    }

    @XmlElement(name = "LECTURES")
    public Lectures getLectures() {
        return lectures;
    }

    @XmlElement(name = "TEACHERS")
    public Teachers getTeachers() {
        return teachers;
    }

}
