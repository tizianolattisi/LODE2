package it.unitn.lode2.xml.course;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:57
 */
public class XMLCourseLectures {

    @XmlElement(name = "LECTURE")
    private List<String> lectures = new ArrayList<>();

    public List<String> getLectures() {
        return lectures;
    }

    public void addLecture(String lecture) {
        lectures.add(lecture);
    }
}
