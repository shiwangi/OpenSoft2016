import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shiwangi on 5/3/16.
 */
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
        imageUtils.displayImage(mIntermediateMat);

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
}
