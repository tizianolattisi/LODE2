package it.unitn.lode2.asset;

import java.util.List;

/**
 * Created by tiziano on 05/05/15.
 */
public interface LodePrefs {

    List<Course> lastUsedCourses();
    void setLastUsedCourse(Course course);
    void save();
}
