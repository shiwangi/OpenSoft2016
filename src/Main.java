import org.opencv.core.*;

import java.awt.image.BufferedImage;
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

        imageUtils = new ImageUtils();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //read the image file.
        Mat mRgba = imread(FNAME);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        imageUtils.displayImage(mRgba);
        BufferedImage trimmedImage = imageUtils.getCroppedImage(imageUtils.mat2BufferedImage(mRgba),.2);
        mRgba = imageUtils.bufferImageToMat(trimmedImage, mRgba.type());
        imageUtils.displayImage(mRgba);
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipImage();
        Mat XscaleImage = images.get(0),
                YscaleImage = images.get(1),
                graphImageBnW = images.get(2),
                graphImage = images.get(3);

        //detect the axes and fetches labels
        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, graphImageBnW);
        List<MatOfPoint> contours = new ArrayList<>();
        List<Double> minmaxValues = null;
        contours.add(contour);
        if (contour != null) {
            AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
            List<String> labels = axisDetection.getAxis();
            minmaxValues = axisDetection.getMinMaxValues(labels);
            System.out.println(labels.toString());
        } else {
            System.out.println("Could not find border axes");

        }


//        imageUtils.displayImage(graphImage);
//
//
//        PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), minmaxValues.get(2), minmaxValues.get(3));
//        List<Colour> colourOfPlotsHSV =  plotValue.populateTable();
//
//
//        LegendDetection legendDetection = new LegendDetection(graphImage,colourOfPlotsHSV);
//        legendDetection.detectLegend();
    }



}
