import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by shiwangi on 26/2/16.
 */
public class DataExtractor {
    static ImageUtils imageUtils;


    static JMagick jMagick;
    static RectangleDetection rectangleDetection;
    static String RPATH = "./resources";
    static ArrayList<String> imageFileList;
    public ArrayList<String> getGraphImages(String name){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        jMagick = new JMagick(name);
        imageFileList = jMagick.convert();
        return imageFileList;
    }
    public void extractData() {
        for(String imageFile:imageFileList){
           String fName = RPATH +imageFile;
           extractDataForImage(fName);
        }

        return;

    }

    private void extractDataForImage(String fname) {
        imageUtils = new ImageUtils();

        rectangleDetection = new RectangleDetection();

        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }

        //trim whitespaces
        mRgba = imageUtils.getCroppedImage((mRgba));

        boolean hasScalesInBox = false;

        List<MatOfPoint> largeContours = jMagick.getLargeContours(imageUtils.convertToBinary(mRgba, 0), mRgba, 0, false);
//        imageUtils.drawContoursOnImage(largeContours,mRgba);
//        imageUtils.displayImage(mRgba);
        if (rectangleDetection.getSquareContours(largeContours) == null) {
            hasScalesInBox = true;
        }

        //imageUtils.displayImage(mRgba);

        //clipping for Scales and Plots
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipContour(mRgba, largeContours.get(0), hasScalesInBox);
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

        graphImage = imageUtils.increaseSaturation(graphImage);
        LegendDetection legendDetection = new LegendDetection(graphImage);

        Mat legendMat = null;
        List<Mat> legendAndPlot;


        imageClipper = new ImageClipper(graphImage);

        //image enhanced check if we can see the contours
        // legendMat = imageUtils.increaseSaturation(legendMat);

        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, imageUtils.convertToBinary(graphImage, 255));
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
            //legend detection gets easier
//            imageUtils.drawContoursOnImage(contours, graphImage);
//            imageUtils.displayImage(graphImage);
            legendAndPlot = imageClipper.clipContourM(graphImage, contour);
            if (legendAndPlot.size() == 2) {
                legendMat = legendAndPlot.get(0);
                graphImage = legendAndPlot.get(1);

                imageUtils.displayImage(legendMat);
                String label = legendDetection.detectLegend(legendMat);

                System.out.println(label);
            }

        } else {
            //so we ll image-match :)
            System.out.println("Could not find scale box - So the legend i wont print :P");
            //Mat img = imageUtils.removeColorPixels(graphImage);
            imageUtils.displayImage(graphImage);
        }
        PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), minmaxValues.get(2), minmaxValues.get(3));
        List<Colour> colourListFromPlot = new ArrayList<Colour>(plotValue.populateTable().keySet());
        legendDetection.getColourSequence(legendMat, colourListFromPlot);
    }


}
