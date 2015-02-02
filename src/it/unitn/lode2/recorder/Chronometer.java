package it.unitn.lode2.recorder;

import java.util.List;

/**
 * User: tiziano
 * Date: 30/01/15
 * Time: 17:33
 */
public class Chronometer {

    private Long timeElapsed=0L;
    private Long fromTime;


    public void start(){
        if( fromTime == null ){
            fromTime = System.currentTimeMillis();
        }
    };

    public void stop(){
        timeElapsed += System.currentTimeMillis() - fromTime;
        fromTime = null;
    };

    public Long elapsed(){
        return (timeElapsed + System.currentTimeMillis() - fromTime)/1000;
    };

}
