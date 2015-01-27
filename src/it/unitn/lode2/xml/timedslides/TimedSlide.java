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

    private Integer time;
    private String title;
    private String path;

    public TimedSlide() {
    }

    public TimedSlide(Integer time, String title, String path) {
        this.time = time;
        this.title = title;
        this.path = path;
    }

    @XmlElement(name = "titolo")
    public String getTitle(){
        return title;
    }

    @XmlElement(name = "immagine")
    public String getPath(){
        return path;
    }

    @XmlElement(name = "tempo")
    public Integer getTime() {
        return time;
    }
}
