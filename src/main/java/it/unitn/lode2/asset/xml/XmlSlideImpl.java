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
    private String title;
    private String text;

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
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }
}
