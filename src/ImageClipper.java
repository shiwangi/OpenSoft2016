import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiwangi on 5/3/16.
 */
public class ImageClipper {
    Mat mRgba;
    ImageUtils imageUtils;

    public ImageClipper(Mat mRgba) {
        this.mRgba = mRgba;
        imageUtils = new ImageUtils();
    }

    public List<Mat> clipImage() {
        Mat mIntermediateMat = imageUtils.convertToBinary(mRgba, 255);
        imageUtils.displayImage(mIntermediateMat);
        Point pt = findfirstBlackRowwAndCol(mIntermediateMat);
        int x = (int) pt.x;
        int y = (int) pt.y;

        Point pt2 = findLastBlackRowAndCol(mIntermediateMat);
        int lastx = (int) pt2.x;
        int lasty = (int) pt2.y;

        Rect rectCrop = new Rect(0, 0, x, mIntermediateMat.rows());
        Mat YscaleImage = new Mat(mRgba, rectCrop);


        rectCrop = new Rect(x, y + 1, mIntermediateMat.cols() - x, mIntermediateMat.rows() - y - 1);
        Mat XscaleImage = new Mat(mRgba, rectCrop);

        Mat graphImageBnW = getGraphImage(x, y, mIntermediateMat);
        rectCrop = new Rect(x + 5, lasty + 5, lastx - x - 10, y - lasty - 10);
        Mat graphImage = new Mat(mRgba, rectCrop);

        List<Mat> result = new ArrayList<>();
        result.add(XscaleImage);
        result.add(YscaleImage);
        result.add(graphImageBnW);
        result.add(graphImage);

        return result;

    }

    private Mat getGraphImage(int x, int y, Mat mIntermediateMat) {
        Mat graphImage = mIntermediateMat.clone();
        for (int i = 0; i < graphImage.rows(); i++) {
            for (int j = 0; j < graphImage.cols(); j++) {
                if (j < x) {
                    double[] newC = {255};
                    graphImage.put(i, j, newC);
                }
                if (i > y) {
                    double[] newC = {255};
                    graphImage.put(i, j, newC);
                }
            }
        }
        return graphImage;
    }

    private Point findfirstBlackRowwAndCol(Mat mIntermediateMat) {
        imageUtils.displayImage(mIntermediateMat);
        int x = 0;
        int y = 0;
        for (int i = 0; i < mIntermediateMat.cols(); i++) {
            if (imageUtils.isColBlack(mIntermediateMat, i)) {
                x = i;
                break;
            }
        }
        for (int i = 0; i < mIntermediateMat.rows(); i++) {
            if (imageUtils.isRowBlack(mIntermediateMat, i)) {
                y = i;
                break;
            }
        }
        return new Point(x, y);
    }

    private Point findLastBlackRowAndCol(Mat mIntermediateMat) {
        int x = mIntermediateMat.cols();
        int y = mIntermediateMat.rows();
        for (int i = mIntermediateMat.cols() - 1; i >= 0; i--) {
            if (imageUtils.isColBlack(mIntermediateMat, i)) {
                x = i;
                break;
            }
        }
        for (int i = mIntermediateMat.rows() - 1; i >= 0; i--) {
            if (imageUtils.isRowBlack(mIntermediateMat, i)) {
                y = i;
                break;
            }
        }
        return new Point(x, y);
    }


    public List<Mat> clipContour(Mat graphImage, MatOfPoint contour, boolean hasScalesInBox) {

        Rect rect = Imgproc.boundingRect(contour);
        Mat roi = null;
        Mat yscale, xscale;
        List<Mat> images = new ArrayList<>();
        imageUtils.displayImage(graphImage);
        if(hasScalesInBox){
            roi = graphImage.submat(rect.y+5, (int) (rect.y + rect.height*.8), rect.x+ (int) (rect.width*.1), rect.x + rect.width-5);
            yscale = graphImage.submat(0, graphImage.height() - 1, 0,rect.x+ (int) (rect.width*.1));
            xscale = graphImage.submat((int) (rect.y + rect.height*.8), graphImage.height() - 1, 0, graphImage.width() - 1);
        }
        else{
            roi = graphImage.submat(rect.y+10, rect.y + rect.height-10, rect.x+10, rect.x + rect.width-10);
            yscale = graphImage.submat(0, graphImage.height() - 1, 0, rect.x);
            xscale = graphImage.submat(rect.y + rect.height - 1, graphImage.height() - 1, 0, graphImage.width() - 1);
        }


        images.add(roi);
        images.add(yscale);
        images.add(xscale);
        return images;
    }

    public List<Mat> clipContourM(Mat graphImage, MatOfPoint contour) {
List<Mat> result = new ArrayList<>();
        Rect rect = Imgproc.boundingRect(contour);
        Mat roi = null;

        imageUtils.displayImage(graphImage);
        roi = graphImage.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);
        for(int i=0;i<graphImage.rows();i++){
            for(int j=0;j<graphImage.cols();j++) {
                if (j>= rect.x && j <= rect.x+rect.width && i > rect.y && i <= rect.y + rect.height) {
                    double []col={255,255,255};
                    graphImage.put(i,j,col);
                }
            }
        }
        result.add(roi);
        result.add(graphImage);
        //return roi;
        return result;
    }
}
