package it.unitn.lode2.slidejuicer.pdf;

import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlSlideImpl;
import it.unitn.lode2.slidejuicer.Juicer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tiziano on 21/04/15.
 */
public class PdfJuicerImpl implements Juicer {

    private File slide;
    private String path = "./";

    PDDocument document = null;
    List<PDPage> pages;

    public PdfJuicerImpl() {
    }

    public static Juicer build(){
        return new PdfJuicerImpl();
    }

    @Override
    public Juicer slide(File slide){
        this.slide = slide;
        try {
            document = PDDocument.load(slide);
            pages = document.getDocumentCatalog().getAllPages();
        } catch (IOException e) {
            pages = new ArrayList<>();
        }
        return this;
    }

    @Override
    public Juicer output(String path) {
        this.path = path;
        return this;
    }

    @Override
    public List<Slide> extract(){
        List<Slide> slides = new ArrayList<>();
        if( slide.exists() ){
            try {
                PDDocument document = PDDocument.load(slide);
                List<PDPage> pages = document.getDocumentCatalog().getAllPages();
                Integer i=0;
                for( PDPage page: pages ){
                    i++;
                    BufferedImage bufferedImage = page.convertToImage();
                    String fileName = i + ".png";
                    File outputfile = new File(path + fileName);
                    ImageIO.write(bufferedImage, "png", outputfile);
                    Slide slide = new XmlSlideImpl("Slides/" + fileName, "title", "text");
                    slides.add(slide);
                }
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                return slides;
            }

        }
        return slides;
    }

    @Override
    public Integer size() {
        return pages.size();
    }

    @Override
    public Iterator<Slide> iterator() {


        Iterator<Slide> myIterator = new Iterator<Slide>() {


            Integer i = 0;

            @Override
            public boolean hasNext() {
                return i < pages.size() - 1;
            }

            @Override
            public Slide next() {
                Slide slide = null;
                try {
                    System.out.println(i);
                    PDPage page = pages.get(i);
                    i++;
                    BufferedImage bufferedImage = page.convertToImage();
                    String fileName = i + ".png";
                    File outputfile = new File(path + fileName);
                    ImageIO.write(bufferedImage, "png", outputfile);
                    slide = new XmlSlideImpl("Slides/" + fileName, "title", "text");
                } catch (IOException e) {

                }
                if (!hasNext()) {
                    try {
                        document.close();
                    } catch (IOException e) {

                    }
                }
                return slide;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return myIterator;
    }



}
