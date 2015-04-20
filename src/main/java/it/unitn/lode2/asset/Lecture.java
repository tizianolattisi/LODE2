package it.unitn.lode2.asset;

import java.util.Date;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 14:58
 */
public interface Lecture {

    String path();
    String name();
    void setName(String name);

    Date date();
    void setDate(Date date);

    Integer number();
    void setNumber(Integer number);

    String lecturer();
    void setLecturer(String lecturer);

    Course course();

    Long videoLength();
    void setVideoLength(Long length);

    Boolean hasPostProcessing();
    void setHasPostProcessing(Boolean b);
    Boolean hasPostProcessing4iTunesU();
    void setHasPostProcessing4iTunesU(Boolean b);

    List<Slide> slides();
    void addSlide(Slide slide);
    Slide slide(Integer seqNumber);

    List<TimedSlide> timedSlides();
    void addTimedSlide(Slide slide, Long second);

    void save();

}
