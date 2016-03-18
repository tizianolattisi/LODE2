package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 20:31
 */
@XmlRootElement(name = "LODE_SLIDES")
public class XMLLodeSlides {

    private XMLLodeSlidesSlides XMLLodeSlidesSlides;
    private XMLLodeSlidesGroups XMLLodeSlidesGroups;


    public XMLLodeSlides() {
    }

    public XMLLodeSlides(XMLLodeSlidesSlides XMLLodeSlidesSlides, XMLLodeSlidesGroups XMLLodeSlidesGroups) {
        this.XMLLodeSlidesSlides = XMLLodeSlidesSlides;
        this.XMLLodeSlidesGroups = XMLLodeSlidesGroups;
    }

    @XmlElement(name = "SLIDES")
    public XMLLodeSlidesSlides getXMLLodeSlidesSlides() {
        return XMLLodeSlidesSlides;
    }

    @XmlElement(name = "GROUPS")
    public XMLLodeSlidesGroups getXMLLodeSlidesGroups() {
        return XMLLodeSlidesGroups;
    }

    public void setXMLLodeSlidesSlides(XMLLodeSlidesSlides XMLLodeSlidesSlides) {
        this.XMLLodeSlidesSlides = XMLLodeSlidesSlides;
    }

    public void setXMLLodeSlidesGroups(XMLLodeSlidesGroups XMLLodeSlidesGroups) {
        this.XMLLodeSlidesGroups = XMLLodeSlidesGroups;
    }
}
