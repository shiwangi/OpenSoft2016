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

    /**
     * returns the arraylist of filepaths to the graph images extracted.
     * @param name The filpath of the input pdf
     * @return
     */
    public ArrayList<String> getGraphImages(String name){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        jMagick = new JMagick(name);
        imageFileList = jMagick.convert();
        return imageFileList;
    }


    /**
     * returns the basic graphdata given a imagefilepath.
     * @param fname the filepath to the input graphImage.
     * @return
     */
    public GraphData extractDataForImage(String fname) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        fname =RPATH+ fname;
        imageUtils = new ImageUtils();

        rectangleDetection = new RectangleDetection();

        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return null;
        }

        //trim whitespaces
        mRgba = imageUtils.getCroppedImage((mRgba));

        boolean hasScalesInBox = false;

        List<MatOfPoint> largeContours = jMagick.getLargeContours(imageUtils.convertToBinary(mRgba, 0), mRgba, 0, false);
        if (rectangleDetection.getSquareContours(largeContours) == null) {
            hasScalesInBox = true;
        }

        //clipping for Scales and Plots
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipContour(mRgba, largeContours.get(0), hasScalesInBox);
        Mat XscaleImage = images.get(2),
                YscaleImage = images.get(1),
                graphImage = images.get(0),
                captionImage = images.get(3);


        List<Double> minmaxValues = null;
        AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
        List<String> labels = axisDetection.getAxis();
        minmaxValues = axisDetection.getMinMaxValues(labels);
        System.out.println(minmaxValues.toString());
        System.out.println(labels.toString());
        String captionLabel = imageUtils.ocrOnImage(captionImage,2);
        labels.add(captionLabel);


      GraphData graphData = new GraphData(minmaxValues,labels,graphImage);
        return graphData;
    }

    /**
     * Finds all the different colored plots in the image,corresponding datapoints and stores in Output.pdf
     * @param graphImage The input graphImage
     * @param minmaxValues The min,max values of the scale which are needed to assign values to datapoints
     */
    void getPlotsAndLegend(Mat graphImage,ArrayList<Double> minmaxValues) {
        //Legend Detection

        graphImage = imageUtils.increaseSaturation(graphImage);
        LegendDetection legendDetection = new LegendDetection(graphImage);

        Mat legendMat = null;
        List<Mat> legendAndPlot;


        ImageClipper imageClipper = new ImageClipper(graphImage);


        MatOfPoint contour = rectangleDetection.detectRectangle(graphImage, imageUtils.convertToBinary(graphImage, 255));
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
            legendAndPlot = imageClipper.clipContourM(graphImage, contour);
            if (legendAndPlot.size() == 2) {
                legendMat = legendAndPlot.get(0);
                graphImage = legendAndPlot.get(1);

                imageUtils.displayImage(legendMat);
                String label = legendDetection.detectLegend(legendMat);

                System.out.println(label);
                PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), minmaxValues.get(2), minmaxValues.get(3));
                List<Colour> colourListFromPlot = new ArrayList<Colour>(plotValue.populateTable().keySet());
                legendDetection.getColourSequence(legendMat, colourListFromPlot);
            }

        } else {
            //so we ll image-match :)
            System.out.println("Could not find scale box ");
            imageUtils.displayImage(graphImage);
            PlotValue plotValue = new PlotValue(graphImage, minmaxValues.get(0), minmaxValues.get(1), 50, 95);
            List<Colour> colourListFromPlot = new ArrayList<Colour>(plotValue.populateTable().keySet());
        }
    }
}
