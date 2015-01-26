package it.unitn.lode2.entities.slide;

import it.unitn.lode2.slide.raster.RasterSlideImpl;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;

public class TimedSlidesTest {

    @Test
    public void test() throws Exception {

        TimedSlides ts = new TimedSlides();
        ts.addSlide(new TimedSlide(14L, new RasterSlideImpl("img/1.jpg", "VCS, DVCS, Git-flow", "", new ArrayList<>())));
        ts.addSlide(new TimedSlide(21L, new RasterSlideImpl("img/2.jpg", "a cosa serve? (1)", "", new ArrayList<>())));

        JAXBContext context = JAXBContext.newInstance(TimedSlides.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


        marshaller.marshal(ts, System.out);

    }

}