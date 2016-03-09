import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    static ImageUtils imageUtils;


    static String FNAME =
            "./resources/roi306.png";
   //"/home/shiwangi/yo.png";

    public static void main(String args[]) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
        JMagick jMagick = new JMagick();
    //       jMagick.convert();

        imageUtils = new ImageUtils();

        //read the image file.
        Mat mRgba = imread(FNAME);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        mRgba = imageUtils.increaseSaturation(mRgba);
        //imageUtils.displayImage(mRgba);

        //trim whitespaces
        mRgba = imageUtils.getCroppedImage((mRgba), 250);
        // mRgba = imageUtils.bufferImageToMat(trimmedImage, mRgba.type());
       // imageUtils.displayImage(mRgba);
        List<MatOfPoint> largeContours = jMagick.getLargeContours(imageUtils.convertToBinary(mRgba, 0), mRgba, 0, false);




        //clipping for Scales and Plots
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipContour(mRgba, largeContours.get(0));
        Mat XscaleImage = images.get(2),
                YscaleImage = images.get(1),
                graphImage = images.get(0);

        //detect the axes and fetches labels
//        imageUtils.displayImage(XscaleImage);
//        imageUtils.displayImage(YscaleImage);

        List<Double> minmaxValues = null;
        AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
        List<String> labels = axisDetection.getAxis();
        minmaxValues = axisDetection.getMinMaxValues(labels);
        System.out.println(minmaxValues.toString());
        System.out.println(labels.toString());


        //Legend Detection

        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, imageUtils.convertToBinary(graphImage, 255));
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        imageUtils.displayImage(graphImage);
        LegendDetection legendDetection = new LegendDetection(graphImage);

        Mat legendMat = null;
        List<Mat> legendAndPlot;
        if (contour != null) {
            //legend detection get easier.
           // imageUtils.drawContoursOnImage(contours, graphImage);
            legendAndPlot = imageClipper.clipContourM(graphImage, contour);

        } else {
            System.out.println("Could not find scale box");
            legendAndPlot = legendDetection.detectLegendImageMatch();
            //so we ll image-match :)
        }
        graphImage = legendAndPlot.get(1);
        legendMat = legendAndPlot.get(0);

        PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), minmaxValues.get(2), minmaxValues.get(3));
        Map<Colour,Boolean> colourOfPlotsHSV = plotValue.populateTable();
        imageUtils.displayImage(graphImage);


        String label = legendDetection.detectLegend(legendMat);

    }




}
