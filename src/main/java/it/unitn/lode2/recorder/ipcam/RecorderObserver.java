package it.unitn.lode2.recorder.ipcam;


/**
 * User: tiziano
 * Date: 29/03/15
 * Time: 17:09
 */
public class RecorderObserver extends Thread {

    private final Process process;
    private IPRecorderImpl recorder;

    public RecorderObserver(Process process, IPRecorderImpl recorder) {
        this.process = process;
        this.recorder = recorder;

    }

    @Override
    public void run() {
        try {
            process.waitFor();
            if( recorder.isRecording() ) {
                Thread.sleep(1000);
                recorder.timeoutHandle();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
