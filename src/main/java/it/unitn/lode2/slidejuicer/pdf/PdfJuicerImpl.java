package it.unitn.lode2.slidejuicer.pdf;

import it.unitn.lode2.slidejuicer.Juicer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    public void extract(){
        if( slide.exists() ){
            try {
                PDDocument document = PDDocument.load(slide);
                List<PDPage> pages = document.getDocumentCatalog().getAllPages();
                Integer i=0;
                for( PDPage page: pages ){
                    i++;
                    BufferedImage bufferedImage = page.convertToImage();
                    File outputfile = new File(path + i + ".png");
                    ImageIO.write(bufferedImage, "png", outputfile);
                }
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }
    }

}
