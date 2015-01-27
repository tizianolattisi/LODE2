package it.unitn.lode2.xml.timedslides;

import it.unitn.lode2.slide.Slide;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:41
 */
@XmlRootElement(name = "slide")
public class TimedSlide {

    private Slide slide;

    private Long time;

    public TimedSlide() {
    }

    public TimedSlide(Long time, Slide slide) {
        this.time = time;
        this.slide = slide;
    }

    @XmlElement(name = "titolo")
    public String getTitle(){
        return slide.getTitle();
    }

    @XmlElement(name = "immagine")
    public String getUrl(){
        return slide.getUrl();
    }

    @XmlElement(name = "tempo")
    public Long getTime() {
        return time;
    }
}
