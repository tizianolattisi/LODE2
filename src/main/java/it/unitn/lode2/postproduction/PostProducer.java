package it.unitn.lode2.postproduction;

import it.unitn.lode2.asset.Course;
import it.unitn.lode2.asset.Lecture;


/**
 * Created by tiziano on 26/05/15.
 */
public interface PostProducer {

    void convert(Lecture lecture);

    void createDistribution(Lecture lecture);

    void createWebsite(Course course);

}
