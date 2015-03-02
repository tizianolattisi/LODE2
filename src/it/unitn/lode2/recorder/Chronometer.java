package it.unitn.lode2.recorder;


/**
 * User: tiziano
 * Date: 30/01/15
 * Time: 17:33
 */
public class Chronometer {

    private enum Status {
        STOPPED, STARTED
    }

    private Long startTime=null;
    private Long endTime=null;
    private Status status=Status.STOPPED;



    public void start(){
        if( Status.STOPPED.equals(status) ){
            startTime = System.currentTimeMillis();
            status = Status.STARTED;
        }
    }

    public void stop(){
        if( Status.STARTED.equals(status) ){
            endTime = System.currentTimeMillis();
            status = Status.STOPPED;
        }

    }

    public void reset(){
        startTime=null;
        endTime=null;
        status=Status.STOPPED;
    }

    public Long elapsed(){
        if( Status.STARTED.equals(status) ){
            return System.currentTimeMillis() - startTime;
        }
        return endTime - startTime;
    }

}
