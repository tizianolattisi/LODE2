package it.unitn.lode2.slide;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tiziano
 * Date: 26/01/15
 * Time: 16:57
 */
public abstract class AbstractSlide implements Slide {

    private String title="";
    private String description="";
    private List<String> tags = new ArrayList<>();

    public AbstractSlide(String title, String description, List<String> tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

}
