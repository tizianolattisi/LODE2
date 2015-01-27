package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 27/01/15
 * Time: 10:48
 */
@XmlRootElement(name = "SLIDES")
public class Slides {

    private List<LodeSlide> slides = new ArrayList<>();

    public void addSlide(LodeSlide s){
        slides.add(s);
    }

    @XmlElement(name = "SLIDE")
    public List<LodeSlide> getSlides() {
        return slides;
    }

    public void setSlides(List<LodeSlide> slides) {
        this.slides = slides;
    }
}
