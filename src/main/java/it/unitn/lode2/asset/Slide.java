package it.unitn.lode2.asset;

/**
 * User: tiziano
 * Date: 06/02/15
 * Time: 09:13
 */
public interface Slide {

    String filename();
    String title();
    void setTitle(String title);
    String text();
    void setText(String text);
}
