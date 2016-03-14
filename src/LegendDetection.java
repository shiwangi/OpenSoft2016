import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static org.opencv.imgproc.Imgproc.cvtColor;
public class LegendDetection {
    Mat graphImage;
    ImageUtils imageUtils;
    Map<Colour, Boolean> colourOfPlotsHSV;

    public LegendDetection(Mat graphImage) {
        this.graphImage = graphImage;
        imageUtils = new ImageUtils();
        // this.colourOfPlotsHSV = colourOfPlotsHSV;
    }

    public List<Mat> detectLegendImageMatch() {
        List<Mat> resultList;
        Mat img = graphImage.clone();


         imageUtils.displayImage(img);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
// find contours:

        Mat mIntermediateMat = new Mat(graphImage.height(), graphImage.width(), CvType.CV_8UC1);
        Imgproc.findContours(imageUtils.convertToBinary(graphImage, 255), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
   //     Imgproc.connectedComponents(imageUtils.convertToBinary(graphImage, 0), mIntermediateMat, 8, CvType.CV_16U);
        int erosion_size = 25;
        int dilation_size = 5;

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2*dilation_size + 1, 2*erosion_size+1));
       // Imgproc.erode(source, destination, element);
        Imgproc.dilate(imageUtils.convertToBinary(graphImage, 0), mIntermediateMat,element);
       // imwrite("./resources/components.jpg", mIntermediateMat);
        //imageUtils.displayImage(mIntermediateMat);

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(graphImage, contours, contourIdx, new Scalar(0, 0, 255));
        }
        imageUtils.displayImage(graphImage);

        resultList = new ArrayList<>();
//        resultList.add(legendimage);
//        resultList.add(img);
//        imageUtils.displayImage(legendimage);
        return resultList;
    }

    public String detectLegend(Mat legendMat) {
        String legend = imageUtils.ocrOnImage(legendMat, 2);
        System.out.print(legend);
        return legend;
    }

    public List<Colour> getColourSequence(Mat legendMat, List<Colour> colourListFromPlot) {
        Mat hsvImage = legendMat.clone();
        cvtColor(legendMat, hsvImage, Imgproc.COLOR_RGB2HSV, 3);
        List<Colour> colours = new ArrayList<>();
        int sz  = colourListFromPlot.size();
        List<ColourOrder> orderColourInLegend = new ArrayList<>();
        for(int i=0;i<sz;i++){
            Colour c =colourListFromPlot.get(i);
            Point pt = getClosestPixel(c,legendMat,legendMat);
            orderColourInLegend.add(new ColourOrder(c,pt.y));

        }
        Collections.sort(orderColourInLegend);
        System.out.println("Colour in the legend : ");
        for(int i=0;i<orderColourInLegend.size();i++){
            Colour c =orderColourInLegend.get(i).colour;
            System.out.println(c.h +" "+c.s +" "+c.v);
        }
        return colours;

    }

    private Point getClosestPixel(Colour col, Mat hsvImage,Mat graph) {

        double minDist = Double.MAX_VALUE;
        Point pt = new Point(0, 0);
        for (int j = 0; j < hsvImage.rows(); j++) {
            for (int i = 0; i < hsvImage.cols(); i++) {
                double[] colorHSV = hsvImage.get(j, i);
                Colour colour = new Colour(colorHSV[0], colorHSV[1], colorHSV[2]);
                double currDistance = Colour.dist(col, colour);
                if (minDist > currDistance) {
                    minDist = currDistance;
                    pt = new Point(j, i);
                }
            }
        }

        return pt;
    }
}
