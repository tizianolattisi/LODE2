package it.unitn.lode2.projector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: tiziano
 * Date: 01/01/15
 * Time: 15:29
 */
public abstract class AbstractProjector implements Projector {

    private ArrayList<Slide> slides = new ArrayList<>();
    private Integer preparedIndex=null;
    private Integer shownIndex=null;

    @Override
    public void addSlides(Slide... slides) {
        for( Slide slide: slides ) {
            this.slides.add(slide);
        }
    }

    @Override
    public void addSlides(List<Slide> slides) {
        for( Slide slide: slides ) {
            this.slides.add(slide);
        }
    }

    @Override
    public Optional<Slide> shownSlide() {
        if( shownIndex != null ) {
            return Optional.of(slides.get(shownIndex));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> shownSlideSeqNumber() {
        if( shownIndex != null ) {
            return Optional.of(shownIndex+1);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> showSlideNumber(Slide s) {
        Integer n = slides.indexOf(s);
        if( n != null ){
            return Optional.of(n+1);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Slide> preparedSlide() {
        if( preparedIndex != null ) {
            return Optional.of(slides.get(preparedIndex));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Slide> slideNr(Integer n) {
        if( n < slides.size() ) {
            return Optional.of(slides.get(n));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Slide> slideDelta(Integer delta) {
        int nr = preparedIndex + delta;
        if( nr>0 && nr<slides.size() ) {
            return Optional.of(slides.get(nr));
        }
        return Optional.empty();
    }

    @Override
    public void first() {
        preparedIndex = 0;
    }

    @Override
    public void previous() {
        if( preparedIndex >0 ){
            preparedIndex--;
        }
    }

    @Override
    public void next() {
        if( preparedIndex <slides.size()-1 ){
            preparedIndex++;
        }
    }

    @Override
    public void last() {
        preparedIndex = slides.size()-1;
    }

    @Override
    public Boolean isFirst() {
        return preparedIndex.equals(0);
    }

    @Override
    public Boolean isLast() {
        return preparedIndex.equals(slides.size()-1);
    }

    @Override
    public void show() {
        shownIndex = preparedIndex;
        if( preparedIndex < slides.size()-1 ) {
            preparedIndex++;
        }
    }

}
