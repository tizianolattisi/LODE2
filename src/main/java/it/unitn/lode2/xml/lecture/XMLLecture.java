package it.unitn.lode2.xml.lecture;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * User: tiziano
 * Date: 02/02/15
 * Time: 08:51
 */
@XmlRootElement(name = "LECTURE")
public class XMLLecture {

    private String name;
    private Date date;
    private Integer sequenceNumber;
    private String courseHome;
    private String lecturer;
    private Long videoLength;
    private Boolean hasPostProcessing;
    private Boolean hasPostProcessing4iTunesU;

    @XmlElement(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "DATE")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement(name = "SEQUENCE_NUMBER")
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @XmlElement(name = "COURSE_HOME")
    public String getCourseHome() {
        return courseHome;
    }

    public void setCourseHome(String courseHome) {
        this.courseHome = courseHome;
    }

    @XmlElement(name = "LECTURER")
    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    @XmlElement(name = "VIDEO_LENGTH")
    public Long getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(Long videoLength) {
        this.videoLength = videoLength;
    }

    @XmlElement(name = "HAS_POST_PROCESSING")
    public Boolean getHasPostProcessing() {
        return hasPostProcessing;
    }

    public void setHasPostProcessing(Boolean hasPostProcessing) {
        this.hasPostProcessing = hasPostProcessing;
    }

    @XmlElement(name = "HAS_POST_PROCESSING_4_ITUNESU")
    public Boolean getHasPostProcessing4iTunesU() {
        return hasPostProcessing4iTunesU;
    }

    public void setHasPostProcessing4iTunesU(Boolean hasPostProcessing4iTunesU) {
        this.hasPostProcessing4iTunesU = hasPostProcessing4iTunesU;
    }
}
