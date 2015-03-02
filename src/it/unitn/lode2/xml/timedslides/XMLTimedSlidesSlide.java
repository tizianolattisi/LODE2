package it.unitn.lode2.xml.timedslides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:41
 */
@XmlRootElement(name = "slide")
public class XMLTimedSlidesSlide {

    private Long time;
    private String title;
    private String path;

    public XMLTimedSlidesSlide() {
    }

    public XMLTimedSlidesSlide(Long time, String title, String path) {
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
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
