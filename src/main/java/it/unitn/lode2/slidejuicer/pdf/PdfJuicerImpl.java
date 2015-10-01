package it.unitn.lode2.slidejuicer.pdf;

import it.unitn.lode2.asset.Slide;
import it.unitn.lode2.asset.xml.XmlSlideImpl;
import it.unitn.lode2.slidejuicer.Juicer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tiziano on 21/04/15.
 */
public class PdfJuicerImpl implements Juicer {

    private static final int MAX_TITLE_LENGTH = 40;

    private File slide;
    private String path = "./";

    PDDocument document = null;
    PDFTextStripper textStripper = null;
    List<PDPage> pages;

    public PdfJuicerImpl() {
        Logger.getLogger("org.apache.pdfbox.util.PDFStreamEngine").setLevel(Level.OFF);
        Logger.getLogger("org.apache.pdfbox.util").setLevel(Level.OFF);
        Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);
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
            textStripper = new PDFTextStripper();
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
        Iterator<Slide> iterator = iterator();
        while( iterator.hasNext() ){
            slides.add(iterator.next());
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
                    // text
                    StringWriter textWriter = new StringWriter();
                    textStripper.setStartPage(i);
                    textStripper.setEndPage(i);
                    textStripper.writeText(document, textWriter);
                    // image
                    PDPage page = pages.get(i);
                    i++;
                    BufferedImage bufferedImage = page.convertToImage();
                    String fileName = i + ".png";
                    File outputfile = new File(path + fileName);
                    ImageIO.write(bufferedImage, "png", outputfile);
                    // create slide
                    String[] rows = textWriter.toString().split("\n");
                    String title = "Slide " + i;
                    for( String row: rows ){
                        if( row.length()>2 ) {
                            if (row.length() > MAX_TITLE_LENGTH) {
                                title = row.substring(0, MAX_TITLE_LENGTH-1);
                            } else {
                                title = row;
                            }
                            break;
                        }
                    }
                    title = title.replace("\r", "");
                    slide = new XmlSlideImpl("Slides/" + fileName, title, textWriter.toString());
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
