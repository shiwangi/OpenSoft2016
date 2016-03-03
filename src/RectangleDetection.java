import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shiwangi on 2/3/16.
 */
public class RectangleDetection {

    static ImageUtils imageUtils;

    public RectangleDetection(){
        imageUtils = new ImageUtils();
    }

    public MatOfPoint detectRectangle(Mat mRgba) {

        //convert the image to black and white does (8 bit)

        Mat mIntermediateMat = imageUtils.convertToBinary(mRgba);
        int x=0;
        int y=0;
        for(int i=0;i<mIntermediateMat.cols();i++){
            if(imageUtils.isColBlack(mIntermediateMat,i)){
                 x = i;
                break;
            }
        }
        Rect rectCrop = new Rect(0, 0, x,  mIntermediateMat.rows());
        Mat YscaleImage = new Mat(mRgba, rectCrop);
        imageUtils.displayImage(YscaleImage);

        for(int i=mIntermediateMat.rows()-1;i>=0;i--){
            if(imageUtils.isRowBlack(mIntermediateMat,i)){
                y = i;
                break;
            }
        }
        rectCrop = new Rect(0, y+1, mIntermediateMat.cols(),  mIntermediateMat.rows()-y-1);
        Mat XscaleImage = new Mat(mRgba, rectCrop);
        imageUtils.displayImage(XscaleImage);

        Mat graphImage = mIntermediateMat.clone();
        for(int i=0;i<graphImage.rows();i++){
            for(int j=0;j<graphImage.cols();j++){
                if(j<x){
                    double[] newC = {255};
                    graphImage.put(i, j, newC);
                }
                if(i>y){
                    double[] newC = {255};
                    graphImage.put(i, j, newC);
                }
            }
        }
//        imageUtils.displayImage(mIntermediateMat);

        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(graphImage, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        imageUtils.drawContoursOnImage(contours,mRgba);
//
//        imageUtils.displayImage(mRgba);

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
            if (area > maxArea) {
                maxArea = area;
            }
        }

       // Find the Border contour and draw it on the image
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

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, 25, true);

        approxContour2f.convertTo(approxContour, CvType.CV_32S);

        if (approxContour.size().height == 4) {
            ret = Imgproc.boundingRect(approxContour);
        }

        return (ret != null);
    }

    public List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {
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
