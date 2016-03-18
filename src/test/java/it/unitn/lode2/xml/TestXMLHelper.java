package it.unitn.lode2.xml;

import it.unitn.lode2.xml.course.XMLCourse;
import it.unitn.lode2.xml.slides.*;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: tiziano
 * Date: 02/02/15
 * Time: 10:29
 */
public class TestXMLHelper {

    private String XML;

    @Test
    public void test() throws Exception {

        XML = producer();

        consumer();

    }

    private String producer() throws Exception {

        XMLLodeSlidesSlides XMLLodeSlidesSlides = new XMLLodeSlidesSlides();
        XMLLodeSlidesSlides.addSlide(new XMLLodeSlidesSlidesSlide("1.jpg", 1, "VCS, DVCS, Git-flow", "La gestione dei sorgenti e il controllo di versione..."));
        XMLLodeSlidesSlides.addSlide(new XMLLodeSlidesSlidesSlide("2.jpg", 2, "a cosa serve? (1)", "Backup incrementale di modifiche..."));
        XMLLodeSlidesGroups XMLLodeSlidesGroups = new XMLLodeSlidesGroups();
        XMLLodeSlidesGroups.addSlidesGroup(new XMLLodeSlidesGroupsGroup("/Users/tiziano/Desktop//vcs.ppt", 1, 13));
        XMLLodeSlides ls = new XMLLodeSlides(XMLLodeSlidesSlides, XMLLodeSlidesGroups);

        StringWriter sw = new StringWriter();
        XMLHelper.build(XMLLodeSlides.class).marshall(ls, sw);

       return sw.toString();

    }

    private void consumer() throws Exception {

        XMLLodeSlides lodeSlides = XMLHelper.build(XMLLodeSlides.class).unmarshal(new StringReader(XML));

        System.out.println(lodeSlides);

    }

    @Test
    public void testCourse() throws Exception {

        XMLCourse XMLCourse = new XMLCourse();
        XMLCourse.setName("Corso di prova");
        XMLCourse.setYear(2015);
        XMLCourse.addLecture("Primo test");
        XMLCourse.addLecture("Secondo test");
        XMLCourse.addTeacher("Tiziano");

        StringWriter sw = new StringWriter();
        XMLHelper.build(XMLCourse.class).marshall(XMLCourse, sw);

        System.out.println(sw.toString());
    }

}
