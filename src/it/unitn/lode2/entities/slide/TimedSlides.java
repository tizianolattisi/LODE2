package it.unitn.lode2.entities.slide;

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
public class TimedSlides {

    @XmlElement
    private List<TimedSlide> slide = new ArrayList<>();

    public void addSlide(TimedSlide s){
        slide.add(s);
    }
}
