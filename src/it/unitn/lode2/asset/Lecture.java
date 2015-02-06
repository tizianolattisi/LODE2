package it.unitn.lode2.asset;

import java.util.Date;
import java.util.List;

/**
 * User: tiziano
 * Date: 05/02/15
 * Time: 14:58
 */
public interface Lecture {

    public String name();
    public void setName(String name);

    public Date date();
    public void setDate(Date date);

    public Integer number();
    public void setNumber(Integer number);

    public String lecturer();
    public void setLecturer(String lecturer);

    public Course course();

    public Long videoLength();
    public void setVideoLength(Long length);

    public Boolean hasPostProcessing();
    public void setHasPostProcessing(Boolean b);
    public Boolean hasPostProcessing4iTunesU();
    public void setHasPostProcessing4iTunesU(Boolean b);

    public List<Slide> slides();
    public void addSlide(Slide slide);
    public Slide slide(Integer seqNumber);

    public List<TimedSlide> timedSlides();
    public void addTimedSlide(Slide slide, Long second);

}
