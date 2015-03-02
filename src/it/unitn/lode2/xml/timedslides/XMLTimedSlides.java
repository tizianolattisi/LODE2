package it.unitn.lode2.xml.timedslides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:34
 */

@XmlRootElement(name = "TIMED_SLIDES")
public class XMLTimedSlides {

    @XmlElement(name = "slide")
    private List<XMLTimedSlidesSlide> slides = new ArrayList<>();

    public void addSlide(XMLTimedSlidesSlide s){
        slides.add(s);
    }
}
