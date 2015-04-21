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
import java.util.List;

/**
 * Created by tiziano on 21/04/15.
 */
public class PdfJuicerImpl implements Juicer {

    private File slide;
    private String path = "./";

    public PdfJuicerImpl() {
    }

    public static Juicer build(){
        return new PdfJuicerImpl();
    }

    @Override
    public Juicer slide(File slide){
        this.slide = slide;
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

}
