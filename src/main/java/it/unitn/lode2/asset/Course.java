package it.unitn.lode2.asset;

import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:25
 */
public interface Course {

    String path();
    String name();
    void setName(String name);
    Integer year();
    void setYear(Integer year);
    List<Lecture> lectures();
    void addLecture(Lecture lecture);
    List<String> teachers();
    void addTeacher(String teacher);
    void save();

}
