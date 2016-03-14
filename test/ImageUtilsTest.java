import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import static org.junit.Assert.*;

/**
 * Created by user on 11/3/16.
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int colnum=5;
        Mat testMat=new Mat(10,10, CvType.CV_8UC3);
        int ans =imageUtils.isColWhite(colnum,testMat);
        assertTrue(ans==0);

        double[] color = {255,255,255}; //white
        for(int j=0;j < testMat.rows(); j++ ){
            testMat.put(j, colnum,  color);
        }
        int ans1 =imageUtils.isColWhite(colnum,testMat);
        assertTrue(ans1==1);
    }

    @Test
    public void testIsRowWhite() throws Exception {


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        int rownum=5;
        Mat testMat=new Mat(10,10, CvType.CV_8UC3);
        int ans =imageUtils.isRowWhite(rownum,testMat);
        assertTrue(ans==0);

        double[] color = {255,255,255};
        for(int j=0;j < testMat.cols(); j++ ){
            testMat.put(rownum, j, color);
        }
        int ans1 =imageUtils.isRowWhite(rownum,testMat);
        assertTrue(ans1==1);


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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int colnum=5;
        Mat testMat=new Mat(10,10, CvType.CV_8UC3);
        boolean ans =imageUtils.isColBlack(testMat,colnum);
        assertFalse(ans);

        double[] color = {255,255,255}; //white
        for(int j=0;j < testMat.rows(); j++ ){
            testMat.put(j, colnum,  color);
        }
        boolean ans1 =imageUtils.isColBlack(testMat,colnum);
        assertFalse(ans1);
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int rownum = 5;
        Mat testMat = new Mat(10,10, CvType.CV_8UC3);
        boolean ans = imageUtils.isRowBlack(testMat,rownum);
        assertTrue(ans);

        double[] color = {255,255,255}; //white
        for(int j=0;j < testMat.cols(); j++ ){
            testMat.put(rownum, j,  color);
        }
        boolean ans1 =imageUtils.isRowBlack(testMat,rownum);
        assertFalse(ans1);

    }

    @Test
    public void testDist() throws Exception {
        Point pt1=new Point(0, 0);
        Point pt2=new Point(10, 0);
        double ans=imageUtils.dist(pt1, pt2);
        assertTrue(ans==100);
        assertFalse(ans==0);
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat testMat = new Mat(10,10, CvType.CV_8UC3);
        Mat testMat1 = (imageUtils.removeColorPixels(testMat)).clone();
        int ans1=1;
        for(int j=0; j< testMat1.cols();j++){
            for(int i=0; i<testMat1.rows(); i++){
                if(!imageUtils.isPixelBlack(testMat1.get(i,j)) && !imageUtils.isPixelWhite(testMat1.get(i,j))) {
                    ans1=0;
                    break;
                }
            }

        }
        assertTrue(ans1==1);
    }
}
