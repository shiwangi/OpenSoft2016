import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import static org.junit.Assert.*;

/**
 * Created by rajitha on 11/3/16.
 */
public class ImageUtilsTest {
    ImageUtils imageUtils = new ImageUtils();

    @Test
    public void testIncreaseSaturation() throws Exception {

    }

    @Test
    public void testMat2BufferedImage() throws Exception {

    }

    @Test
    public void testIsColWhite() throws Exception {

    }

    @Test
    public void testIsRowWhite() throws Exception {
        int rownum = 3;
        Mat newmat = new Mat();
        int ans = imageUtils.isRowWhite(rownum,newmat);
        assertTrue(ans==0);
    }

    @Test
    public void testIsPixelWhite() throws Exception {
        double[] color = {255,255,255};
        boolean ans = imageUtils.isPixelWhite(color);
        assertTrue(ans);
        double[] color1 = {0,0,0};
        assertFalse(imageUtils.isPixelWhite(color1));

    }

    @Test
    public void testDrawContoursOnImage() throws Exception {

    }

    @Test
    public void testDisplayImage() throws Exception {

    }

    @Test
    public void testDisplaybuffImage() throws Exception {

    }

    @Test
    public void testOcrOnImage() throws Exception {

    }

    @Test
    public void testConvertToBinary() throws Exception {

    }

    @Test
    public void testIsColBlack() throws Exception {

    }

    @Test
    public void testIsPixelBlack() throws Exception {
        double[] color = {255,255,255};
        boolean ans = imageUtils.isPixelBlack(color);
        assertFalse("Color is not black",ans);
        double[] color1 = {0,0,0};
        assertTrue("Color is black",imageUtils.isPixelBlack(color1));

    }

    @Test
    public void testIsRowBlack() throws Exception {

    }

    @Test
    public void testDist() throws Exception {

    }

    @Test
    public void testOcrOnImageForYScale() throws Exception {

    }

    @Test
    public void testGetCroppedImage() throws Exception {

    }

    @Test
    public void testCleanborders() throws Exception {

    }

    @Test
    public void testRemoveColorPixels() throws Exception {

    }
}