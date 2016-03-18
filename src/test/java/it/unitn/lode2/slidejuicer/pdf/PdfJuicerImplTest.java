package it.unitn.lode2.slidejuicer.pdf;

import it.unitn.lode2.slidejuicer.Juicer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by tiziano on 21/04/15.
 */
public class PdfJuicerImplTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testExtract() throws Exception {
        PdfJuicerImpl.build().slide(new File("test.pdf")).extract();
    }
}