import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import static org.junit.Assert.*;

/**
 * Created by rajitha on 12/3/16.
 */
public class RectangleDetectionTest {

    @Test
    public void testDetectRectangle() throws Exception {
        RectangleDetection r = new RectangleDetection();
        Mat a,b ;
        a = new Mat();
        b = new Mat();
        MatOfPoint t = r.detectRectangle(a,b);
//assertTrue();
    }

    @Test
    public void testIsContourSquare() throws Exception {

    }

    @Test
    public void testGetSquareContours() throws Exception {

    }
}