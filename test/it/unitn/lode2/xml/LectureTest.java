package it.unitn.lode2.xml;

import it.unitn.lode2.xml.lecture.Lecture;
import it.unitn.lode2.xml.slides.LodeSlides;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class LectureTest {

    private static final String INCHECK = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<LECTURE>\n" +
            "   <NAME>TEST_A</NAME>\n" +
            "   <DATE>2014-10-08 20:12:41.579 CEST</DATE>\n" +
            "   <SEQUENCE_NUMBER>1</SEQUENCE_NUMBER>\n" +
            "   <COURSE_HOME>Test_2014</COURSE_HOME>\n" +
            "   <LECTURE_HOME>01_Test_a_2014-10-08</LECTURE_HOME>\n" +
            "   <LECTURER>Tiziano</LECTURER>\n" +
            "   <HAS_POST_PROCESSING>true</HAS_POST_PROCESSING>\n" +
            "   <HAS_POST_PROCESSING_4_ITUNESU>false</HAS_POST_PROCESSING_4_ITUNESU>\n" +
            "</LECTURE>\n";

    private static final String OUTCHECK = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<LECTURE>\n" +
            "   <NAME>TEST_A</NAME>\n" +
            "   <DATE>2014-10-08 20:12:41.579 CEST</DATE>\n" +
            "   <SEQUENCE_NUMBER>1</SEQUENCE_NUMBER>\n" +
            "   <COURSE_HOME>Test_2014</COURSE_HOME>\n" +
            "   <LECTURE_HOME>01_Test_a_2014-10-08</LECTURE_HOME>\n" +
            "   <LECTURER>Tiziano</LECTURER>\n" +
            "   <VIDEO_LENGTH>49</VIDEO_LENGTH>\n" +
            "   <HAS_POST_PROCESSING>true</HAS_POST_PROCESSING>\n" +
            "   <HAS_POST_PROCESSING_4_ITUNESU>false</HAS_POST_PROCESSING_4_ITUNESU>\n" +
            "</LECTURE>\n";

    @Test
    public void consumer() throws Exception {

        JAXBContext context = JAXBContext.newInstance(Lecture.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Reader reader = new StringReader(INCHECK);
        Lecture lecture = (Lecture) unmarshaller.unmarshal(reader);

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        marshaller.marshal(lecture, sw);

        System.out.println(sw.toString());

    }

}