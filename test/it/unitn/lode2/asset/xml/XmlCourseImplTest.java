package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmlCourseImplTest {

    private final static String courseFolderPath = "/Users/tiziano/_LODE/COURSES/Corso_Di_Test_Lode_(1_E_2)_2015/";

    @Test
    public void test() throws Exception {

        Course course = new XmlCourseImpl(courseFolderPath);

        System.out.println(course.name());

        for( Lecture lecture: course.lectures() ){
            System.out.println(" - " + lecture.name());
            System.out.println(" - " + lecture.course());
        }

    }


}