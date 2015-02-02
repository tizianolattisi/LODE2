package it.unitn.lode2.xml;

import it.unitn.lode2.xml.slides.*;
import org.junit.Test;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
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

        Slides slides = new Slides();
        slides.addSlide(new LodeSlide("1.jpg", 1, "VCS, DVCS, Git-flow", "La gestione dei sorgenti e il controllo di versione..."));
        slides.addSlide(new LodeSlide("2.jpg", 2, "a cosa serve? (1)", "Backup incrementale di modifiche..."));
        Groups groups = new Groups();
        groups.addSlidesGroup(new SlidesGroup("/Users/tiziano/Desktop//vcs.ppt", 1, 13));
        LodeSlides ls = new LodeSlides(slides, groups);

        StringWriter sw = new StringWriter();
        XMLHelper.build(LodeSlides.class).marshall(ls, sw);

       return sw.toString();

    }

    private void consumer() throws Exception {

        LodeSlides lodeSlides = XMLHelper.build(LodeSlides.class).unmarshal(new StringReader(XML));

        System.out.println(lodeSlides);

    }

}
