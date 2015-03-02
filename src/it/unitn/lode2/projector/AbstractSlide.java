package it.unitn.lode2.projector;


/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:57
 */
public abstract class AbstractSlide implements Slide {

    private String title="";
    private String description="";

    public AbstractSlide(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
