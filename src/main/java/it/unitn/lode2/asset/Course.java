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
    Integer year();
    List<Lecture> lectures();
    List<String> teachers();
    Boolean isClosed();
    void setName(String name);
    void setYear(Integer year);
    void addLecture(Lecture lecture);
    void addTeacher(String teacher);
    void setClosed(Boolean closed);
    void save();

}
