package it.unitn.lode2.recorder;

import javafx.beans.property.DoubleProperty;

import java.io.InputStream;

/**
 * Created by Tiziano on 14/10/2015.
 */
public interface VolumeChecker {

    void setStream(InputStream stream);
    void setLufsProperty(DoubleProperty lufsProperty);
    void start();
    void terminate();

}
