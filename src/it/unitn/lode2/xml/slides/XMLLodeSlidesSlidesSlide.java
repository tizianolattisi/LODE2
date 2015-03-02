package it.unitn.lode2.xml.slides;

import javax.xml.bind.annotation.XmlElement;

/**
 * User: tiziano
 * Date: 27/01/15
 * Time: 10:48
 */
public class XMLLodeSlidesSlidesSlide {

    private String fileName;
    private Integer sequenceNumber;
    private String title;
    private String text;

    public XMLLodeSlidesSlidesSlide() {
    }

    public XMLLodeSlidesSlidesSlide(String fileName, Integer sequenceNumber, String title, String text) {
        this.fileName = fileName;
        this.sequenceNumber = sequenceNumber;
        this.title = title;
        this.text = text;
    }

    @XmlElement(name = "FILENAME")
    public String getFileName() {
        return fileName;
    }

    @XmlElement(name = "SEQ_NUM")
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @XmlElement(name = "TITLE")
    public String getTitle() {
        return title;
    }

    @XmlElement(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
