import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static org.junit.Assert.*;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class AxisDetectionTest {

    @Test
    public void testGetAxis() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat xScale = imread("./test/resources/xscale.png");
        Mat yScale = imread("./test/resources/yscale.png");
        //imageUtils.displayImage("./resources/xscale.png");
        AxisDetection axisDetection = new AxisDetection(xScale,yScale);
        List<String> labels = axisDetection.getAxis();
        List<String> actuals = new ArrayList<>();
        actuals.add("500 1000 1500 2000 2500 3000 3500");
        actuals.add("Time (Sec)  ");
        labels.remove(2);
        labels.remove(2);
        assertArrayEquals(labels.toArray(),actuals.toArray());

    }

    @Test
    public void testGetMinMaxValues() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat xScale = imread("./test/resources/xscale.png");
        Mat yScale = imread("./test/resources/yscale.png");
        //imageUtils.displayImage("./resources/xscale.png");
        AxisDetection axisDetection = new AxisDetection(xScale,yScale);
        List<Double> minmaxValues =  axisDetection.getMinMaxValues(axisDetection.getAxis());
        minmaxValues.size();
        List<Double> actuals = new ArrayList<>();
        actuals.add(new Double(500.0));
        actuals.add(new Double(3500.0));
        actuals.add(new Double(5.0));
        actuals.add(new Double(35.0));
        actuals.add(new Double(500));
        assertArrayEquals(minmaxValues.toArray(),actuals.toArray());

    }
}