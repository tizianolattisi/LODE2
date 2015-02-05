package it.unitn.lode2.asset;

import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:25
 */
public interface Course {

    public String name();
    public void setName(String name);
    public Integer year();
    public void setYear(Integer year);
    public List<Lecture> lectures();
    public void addLecture(Lecture lecture);
    public List<String> teachers();
    public void addTeacher(String teacher);

}
