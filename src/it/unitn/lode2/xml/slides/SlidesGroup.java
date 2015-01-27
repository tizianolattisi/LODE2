package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 20:33
 */
@XmlRootElement(name = "LODE_SLIDES")
public class SlidesGroup {

    private String fileName;
    private Integer firstSlide;
    private Integer lastSlide;

    public SlidesGroup() {
    }

    public SlidesGroup(String fileName, Integer firstSlide, Integer lastSlide) {
        this.fileName = fileName;
        this.firstSlide = firstSlide;
        this.lastSlide = lastSlide;
    }

    @XmlElement(name = "fileName")
    public String getFileName() {
        return fileName;
    }

    @XmlElement(name = "firstSlide")
    public Integer getFirstSlide() {
        return firstSlide;
    }

    @XmlElement(name = "lastSlide")
    public Integer getLastSlide() {
        return lastSlide;
    }
}
