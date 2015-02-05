package it.unitn.lode2.xml.course;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 11:57
 */
public class Teachers {

    @XmlElement(name = "TEACHER_NAME")
    private List<String> teachers = new ArrayList<>();

    public List<String> getTeachers() {
        return teachers;
    }

    public void addTeacher(String teacher) {
        teachers.add(teacher);
    }
}
