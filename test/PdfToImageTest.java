import org.junit.Test;
import org.opencv.core.Core;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by rajitha on 16/3/16.
 */
public class PdfToImageTest {

    PdfToImage pdfToImage = new PdfToImage("./test/resources/test1.pdf");
    @Test
    public void testConvert() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ArrayList<String> fileList = pdfToImage.convert();
        ArrayList<String> actuals = new ArrayList<>();
        actuals.add("/roi01.png");
        assertArrayEquals(fileList.toArray(),actuals.toArray());

    }

    @Test
    public void testGetLargeContours() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //pdfToImage.getLargeContours();

    }
}