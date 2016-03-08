import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    static ImageUtils imageUtils;


    static String FNAME = "./resources/try2.png";

    public static void main(String args[]) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        JMagick jMagick = new JMagick();
        jMagick.convert();

        imageUtils = new ImageUtils();

        //read the image file.
        Mat mRgba = imread(FNAME);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        imageUtils.displayImage(mRgba);

        //trim whitespaces
        mRgba = imageUtils.getCroppedImage((mRgba), 250);
        // mRgba = imageUtils.bufferImageToMat(trimmedImage, mRgba.type());
        imageUtils.displayImage(mRgba);

        //clipping for Scales and Plots
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipImage();
        Mat XscaleImage = images.get(0),
                YscaleImage = images.get(1),
                graphImageBnW = images.get(2),
                graphImage = images.get(3);

        //detect the axes and fetches labels
        List<Double> minmaxValues = null;
        AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
        List<String> labels = axisDetection.getAxis();
        minmaxValues = axisDetection.getMinMaxValues(labels);
        System.out.println(labels.toString());

        imageUtils.displayImage(graphImage);


        PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), minmaxValues.get(2), minmaxValues.get(3));
        List<Colour> colourOfPlotsHSV = plotValue.populateTable();

        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, imageUtils.convertToBinary(graphImage));
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        LegendDetection legendDetection = new LegendDetection(graphImage, colourOfPlotsHSV);

        Mat legendMat = null;
        if (contour != null) {
            //legend detection get easier.
            imageUtils.drawContoursOnImage(contours, graphImage);
            legendMat = imageClipper.clipContour(graphImage, contour);

        } else {
            System.out.println("Could not find scale box");
            legendMat = legendDetection.detectLegendImageMatch();
            //so we ll image-match :)
        }

        String label = legendDetection.detectLegend(legendMat);

    }


}
