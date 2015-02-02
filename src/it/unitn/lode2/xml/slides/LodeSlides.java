package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 20:31
 */
@XmlRootElement(name = "LODE_SLIDES")
public class LodeSlides {

    private Slides slides;
    private Groups groups;


    public LodeSlides() {
    }

    public LodeSlides(Slides slides, Groups groups) {
        this.slides = slides;
        this.groups = groups;
    }

    @XmlElement(name = "SLIDES")
    public Slides getSlides() {
        return slides;
    }

    @XmlElement(name = "GROUPS")
    public Groups getGroups() {
        return groups;
    }

    public void setSlides(Slides slides) {
        this.slides = slides;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }
}
