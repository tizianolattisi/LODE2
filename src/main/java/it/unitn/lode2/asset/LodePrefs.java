package it.unitn.lode2.asset;

import java.util.List;

/**
 * Created by tiziano on 05/05/15.
 */
public interface LodePrefs {

    List<Course> lastUsedCourses();
    void setLastUsedCourse(Course course);
    void save();
    String getFfmpegPath();
    void setFfmpegPath(String path);
    String getUser();
    void setUser(String user);
    String getPassword();
    void setPassword(String password);
    String getHost();
    void setHost(String host);
    List<String> getIpCamPresets();
}
