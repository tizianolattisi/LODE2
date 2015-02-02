package it.unitn.lode2.xml;

import it.unitn.lode2.xml.timedslides.TimedSlide;
import it.unitn.lode2.xml.timedslides.TimedSlides;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class TimedSlidesTest {

    private static final String OUTCHECK = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<TIMED_SLIDES>\n" +
            "    <slide>\n" +
            "        <immagine>img/1.jpg</immagine>\n" +
            "        <tempo>14</tempo>\n" +
            "        <titolo>VCS, DVCS, Git-flow</titolo>\n" +
            "    </slide>\n" +
            "    <slide>\n" +
            "        <immagine>img/2.jpg</immagine>\n" +
            "        <tempo>21</tempo>\n" +
            "        <titolo>a cosa serve? (1)</titolo>\n" +
            "    </slide>\n" +
            "</TIMED_SLIDES>\n";

    @Test
    public void producer() throws Exception {

        TimedSlides ts = new TimedSlides();
        ts.addSlide(new TimedSlide(14L, "VCS, DVCS, Git-flow", "img/1.jpg"));
        ts.addSlide(new TimedSlide(21L, "a cosa serve? (1)", "img/2.jpg"));

        JAXBContext context = JAXBContext.newInstance(TimedSlides.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        marshaller.marshal(ts, sw);

        assert OUTCHECK.equals(sw.toString());

    }

}