import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import static org.junit.Assert.*;

/**
 * Created by rajitha on 12/3/16.
 */
public class ImageClipperTest {

    ImageClipper imageClipper ;

    @Test
    public void testClipImage() throws Exception {

    }

    @Test
    public void testFindfirstBlackRowwAndCol() throws Exception {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat testMat = new Mat(10,10, CvType.CV_8UC3);
        imageClipper = new ImageClipper(testMat);
        Point p = imageClipper.findfirstBlackRowwAndCol(testMat);
        assertTrue((p.x==0) &&(p.y==0));

        for(int i=0 ;i<testMat.cols();i++)
        {
            double m[] ={255,255,255};
            testMat.put(0,i,m);
            testMat.put(1,i,m);
        }
        p = imageClipper.findfirstBlackRowwAndCol(testMat);
        assertTrue((p.x==0) &&(p.y==2));
    }

    @Test
    public void testFindLastBlackRowAndCol() throws Exception {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat testMat = new Mat(10,10, CvType.CV_8UC3);
        imageClipper = new ImageClipper(testMat);
        Point p = imageClipper.findLastBlackRowAndCol(testMat);
        assertTrue((p.x==9) &&(p.y==9));

        for(int i=0 ;i<testMat.cols();i++)
        {
            double m[] ={255,255,255};
            testMat.put(testMat.rows()-1,i,m);
            testMat.put(testMat.rows()-2,i,m);
        }
        p = imageClipper.findLastBlackRowAndCol(testMat);
        assertTrue((p.x==9) &&(p.y==7));
    }

    @Test
    public void testClipContour() throws Exception {

    }

    @Test
    public void testClipContourM() throws Exception {

    }
}