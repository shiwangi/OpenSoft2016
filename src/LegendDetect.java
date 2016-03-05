import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajitha on 5/3/16.
 */
public class LegendDetect {


    static  ImageUtils imageUtils = new ImageUtils();
    public String  getLegendlabel(Mat mRgba) {

        //check for inner rectangle.


        Mat cleanlegend = imageUtils.removecolorpixels(mRgba);
       // imageUtils.displayImage(cleanlegend);
        cleanlegend = imageUtils.convertToBinary(cleanlegend);
        imageUtils.displayImage(cleanlegend);
//        RectangleDetection rectdetect  = new RectangleDetection();
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(cleanlegend, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        rectdetect.getSquareContours(contours);
//        Mat newmat = cleanlegend.clone();
//        imageUtils.drawContoursOnImage(contours, mRgba);
//        imageUtils.displayImage(mRgba);

        //find minx,miny and maxx maxy

//        Rect rectCrop = new Rect(0, 0, (int) (min_idx - max / 2), image_roi.rows());
//        Mat labelImage_roi = new Mat(image_roi, rectCrop);
//
//        rectCrop = new Rect((int) (min_idx - max / 2), 0, (image_roi.cols() - (int) (min_idx - max / 2)), image_roi.rows());
//        Mat scaleImage = new Mat(image_roi, rectCrop);
        String legend = imageUtils.ocrOnImage((cleanlegend),2);
        return legend;

    }
}
