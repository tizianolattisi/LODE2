package it.unitn.lode2.xml.distribution;

import it.unitn.lode2.xml.timedslides.XMLTimedSlidesSlide;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:34
 */

@XmlRootElement(name = "lezione")
public class XMLData {

    private XMLDataVideo video = new XMLDataVideo();

    private XMLDataInfo info = new XMLDataInfo();

    public XMLData() {
    }

    @XmlElement(name = "slide")
    private List<XMLTimedSlidesSlide> slides = new ArrayList<>();

    public void addSlide(XMLTimedSlidesSlide s){
        slides.add(s);
    }

    @XmlElement(name = "video")
    public XMLDataVideo getVideo() {
        return video;
    }

    @XmlElement(name = "info")
    public XMLDataInfo getInfo() {
        return info;
    }

    public void setVideo(XMLDataVideo video) {
        this.video = video;
    }

    public void setInfo(XMLDataInfo info) {
        this.info = info;
    }
}
