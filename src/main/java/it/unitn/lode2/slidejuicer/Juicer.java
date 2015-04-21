package it.unitn.lode2.slidejuicer;

import java.io.File;

/**
 * Created by tiziano on 21/04/15.
 */
public interface Juicer {

    Juicer slide(File file);
    Juicer output(String path);
    void extract();

}
