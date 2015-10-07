package it.unitn.lode2.projector;

import java.util.List;
import java.util.Optional;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 13:57
 */
public interface Projector {

    void addSlides(Slide... slides);
    void addSlides(List<Slide> slides);

    Optional<Slide> shownSlide();
    Optional<Integer> shownSlideSeqNumber();
    Optional<Integer> preparedSlideSeqNumber();
    Optional<Slide> preparedSlide();
    Optional<Slide> slideNr(Integer n);
    Optional<Slide> slideDelta(Integer n);

    Optional<Integer> showSlideNumber(Slide s);

    /* Prepared slide controls */
    void first();
    void previous();
    void next();
    void last();
    void goTo(Integer n);
    Boolean isFirst();
    Boolean isLast();

    /* shows the prepared slide (that becomes shown) */
    void show();

}
