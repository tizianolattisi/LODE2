package it.unitn.lode2.entities.slide;

import it.unitn.lode2.xml.slides.*;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;


public class LodeSlidesTest {

    private static final String OUTCHECK = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<LODE_SLIDES>\n" +
            "    <GROUPS>\n" +
            "        <slidesGroup>\n" +
            "            <fileName>/Users/tiziano/Desktop//vcs.ppt</fileName>\n" +
            "            <firstSlide>1</firstSlide>\n" +
            "            <lastSlide>13</lastSlide>\n" +
            "        </slidesGroup>\n" +
            "    </GROUPS>\n" +
            "    <SLIDES>\n" +
            "        <SLIDE>\n" +
            "            <FILENAME>1.jpg</FILENAME>\n" +
            "            <SEQ_NUM>1</SEQ_NUM>\n" +
            "            <TEXT>La gestione dei sorgenti e il controllo di versione...</TEXT>\n" +
            "            <TITLE>VCS, DVCS, Git-flow</TITLE>\n" +
            "        </SLIDE>\n" +
            "        <SLIDE>\n" +
            "            <FILENAME>2.jpg</FILENAME>\n" +
            "            <SEQ_NUM>2</SEQ_NUM>\n" +
            "            <TEXT>Backup incrementale di modifiche...</TEXT>\n" +
            "            <TITLE>a cosa serve? (1)</TITLE>\n" +
            "        </SLIDE>\n" +
            "    </SLIDES>\n" +
            "</LODE_SLIDES>\n";

    @Test
    public void test() throws Exception {

        Slides slides = new Slides();
        slides.addSlide(new LodeSlide("1.jpg", 1, "VCS, DVCS, Git-flow", "La gestione dei sorgenti e il controllo di versione..."));
        slides.addSlide(new LodeSlide("2.jpg", 2, "a cosa serve? (1)", "Backup incrementale di modifiche..."));
        Groups groups = new Groups();
        groups.addSlidesGroup(new SlidesGroup("/Users/tiziano/Desktop//vcs.ppt", 1, 13));
        LodeSlides ls = new LodeSlides(slides, groups);

        JAXBContext context = JAXBContext.newInstance(LodeSlides.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        marshaller.marshal(ls, sw);

        assert OUTCHECK.equals(sw.toString());
    }

}