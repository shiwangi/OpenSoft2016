import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 * Created by shiwangi on 2/3/16.
 */
public class RectangleDetection {

    public MatOfPoint detectRectangle(Mat mRgba) {

        //convert the image to black and white does (8 bit)



        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);
        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2GRAY);
        threshold(mIntermediateMat, mIntermediateMat, 0.1 * 255, 255.0, THRESH_BINARY); //GRAY 2 Binary based on threshold


        //  displayImage(Mat2BufferedImage(mIntermediateMat));

        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mIntermediateMat, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        int i=0;
//        for(MatOfPoint cont:contours){
//            drawContours(mRgba, contours, i, new Scalar(0, 255, 0), 10);
//            i++;
//        }

        //get Square Contours
        List<MatOfPoint> squareContours = getSquareContours(contours);

        // Filter contours by area and resize to fit the original image size
        List<MatOfPoint> mContours = new ArrayList<>();

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        //Find the Border contour and draw it on the image
        each = squareContours.iterator();
        int idx = 0;
        double secondMax = 0;
        MatOfPoint borderContour = null;
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double area = Imgproc.contourArea(contour);
            if (area > secondMax && Imgproc.contourArea(contour) < maxArea) {
                mContours.add(contour);
                secondMax = area;
                borderContour = contour;
                idx++;
            }
        }
        return borderContour;
    }

    public boolean isContourSquare(MatOfPoint thisContour) {

        Rect ret = null;

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, 2, true);

        approxContour2f.convertTo(approxContour, CvType.CV_32S);

        if (approxContour.size().height == 4) {
            ret = Imgproc.boundingRect(approxContour);
        }

        return (ret != null);
    }

    public  List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {
        List<MatOfPoint> squares = null;

        for (MatOfPoint c : contours) {

            if ((isContourSquare(c))) {

                if (squares == null)
                    squares = new ArrayList<MatOfPoint>();
                squares.add(c);
            }
        }

        return squares;
    }

}
