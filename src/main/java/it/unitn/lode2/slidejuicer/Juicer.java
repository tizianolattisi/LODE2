package it.unitn.lode2.slidejuicer;

import it.unitn.lode2.asset.Slide;

import java.io.File;
import java.util.List;

/**
 * Created by tiziano on 21/04/15.
 */
public interface Juicer {

    Juicer slide(File file);
    Juicer output(String path);
    List<Slide> extract();

}
