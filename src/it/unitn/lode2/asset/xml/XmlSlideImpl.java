package it.unitn.lode2.asset.xml;

import it.unitn.lode2.asset.AbstractSlide;
import it.unitn.lode2.asset.Slide;

/**
 * User: tiziano
 * Date: 06/02/15
 * Time: 09:15
 */
public class XmlSlideImpl extends AbstractSlide implements Slide{

    private final String filename;
    private final String title;
    private final String text;

    public XmlSlideImpl(String filename, String title, String text) {
        this.filename = filename;
        this.title = title;
        this.text = text;
    }

    @Override
    public String filename() {
        return filename;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String text() {
        return text;
    }
}
