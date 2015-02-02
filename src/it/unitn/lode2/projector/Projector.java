package it.unitn.lode2.projector;

import java.util.List;
import java.util.Optional;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 13:57
 */
public interface Projector {

    public void addSlides(Slide... slides);
    public void addSlides(List<Slide> slides);

    public Optional<Slide> shownSlide();
    public Optional<Slide> preparedSlide();
    public Optional<Slide> slideNr(Integer n);
    public Optional<Slide> slideDelta(Integer n);

    /* Prepared slide controls */
    public void first();
    public void previous();
    public void next();
    public void last();
    public Boolean isFirst();
    public Boolean isLast();

    /* shows the prepared slide (that becomes shown) */
    public void show();

}
