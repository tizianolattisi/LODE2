package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.AbstractTimedSlide;
import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.TimedSlide;

/**
 * User: tiziano
 * Date: 06/02/15
 * Time: 12:38
 */
public class XmlTimedSlide extends AbstractTimedSlide implements TimedSlide {

    private Slide slide;
    private Long second;

    public XmlTimedSlide(Slide slide, Long second) {
        this.slide = slide;
        this.second = second;
    }

    @Override
    public Slide slide() {
        return slide;
    }

    @Override
    public Long time() {
        return second;
    }
}
