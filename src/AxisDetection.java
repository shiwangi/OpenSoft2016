import org.opencv.core.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.core.Core.addWeighted;
import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by rajitha on 3/3/16.
 */
public class AxisDetection {
    static double maxY = Double.MIN_VALUE;
    static ImageUtils imageUtils;
    static Mat xscaleImage,yscaleImage;

    public AxisDetection(Mat xscaleImage, Mat yscaleImage){
        imageUtils = new ImageUtils();
        this.xscaleImage = xscaleImage;
        this.yscaleImage = yscaleImage;
    }

    public List<String> getAxis(List<Point> corners, Mat mRgba) {

        //Find lower x-line
        List<String> labels = getXaxislabels(corners, mRgba);

        //Find left y-line
        List<String> ylabels = getYaxisLabels(corners, mRgba);

        labels.addAll(ylabels);
        return labels;
    }

    private List<String> getYaxisLabels(List<Point> corners, Mat mRgba) {
        List<String> labels = new ArrayList<>();


        Mat image_roi = yscaleImage;
        //displayImage(Mat2BufferedImage(image_roi));
        int count = 0;
        double min_idx = image_roi.cols() + 1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < image_roi.cols(); i++) {
            if (imageUtils.isColWhite(i, image_roi) == 0) {
                if (max < count) {
                    max = count;
                    min_idx = i - 1;
                    count = 0;
                }
            } else count = count + 1;
        }

        Rect rectCrop = new Rect(0, 0, (int) (min_idx - max / 2), image_roi.rows());
        Mat labelImage_roi = new Mat(image_roi, rectCrop);

        rectCrop = new Rect((int) (min_idx - max / 2), 0, (image_roi.cols() - (int) (min_idx - max / 2)), image_roi.rows());
        Mat scaleImage = new Mat(image_roi, rectCrop);
        scaleImage =  imageUtils.convertToBinary(scaleImage);
//        GaussianBlur(scaleImage, scaleImage, new Size(0, 0), 3);
//        addWeighted(scaleImage, 1.5, scaleImage, -0.5, 0, scaleImage);
       // fourierTransform(scaleImage);
        //bilateralFilter(scaleImage, scaleImage,5,20,20,BORDER_DEFAULT);

        imageUtils.displayImage(scaleImage);
        String YScale = imageUtils.ocrOnImage(scaleImage,0);
        YScale = YScale.replaceAll("\n", " ");

        labels.add(YScale);

        Mat rotatedImage = getRotated(labelImage_roi);
        imageUtils.displayImage(rotatedImage);
        String Ylabel = imageUtils.ocrOnImage(rotatedImage,1);
        Ylabel = Ylabel.replaceAll("\n", " ");
        labels.add(Ylabel);

        return labels;

    }


    private Mat getRotated(Mat labelImage_roi) {

        double len = max(labelImage_roi.cols(), labelImage_roi.rows());
        Point center = new Point(len / 2.0, len / 2.0);

        Mat rot = getRotationMatrix2D(center, -90, 1.0);
        warpAffine(labelImage_roi, labelImage_roi, rot, new Size(len, len));
        return labelImage_roi;

    }

    private List<String> getXaxislabels(List<Point> corners, Mat mRgba) {
        List<String> labels = new ArrayList<>();


        Mat image_roi = xscaleImage;
        //displayImage(Mat2BufferedImage(image_roi));
        Mat resizeimage = new Mat();
        Size sz = new Size(1600,100);
        resize( image_roi, resizeimage, sz );
        //imageUtils.displayImage(resizeimage);
        resizeimage = imageUtils.convertToBinary(resizeimage);
        Mat dest_img_roi = new Mat(image_roi.rows()*2,image_roi.cols()*2,image_roi.type());
        resize(image_roi,dest_img_roi,dest_img_roi.size(),2,2,INTER_NEAREST);
        imageUtils.displayImage(dest_img_roi);


        Mat destination = new Mat(resizeimage.rows(),resizeimage.cols(),resizeimage.type());

        Mat kernel = new Mat(3,3, CvType.CV_8S){
            {
                put(0,-1,0);
                put(-1,6,-1);
                put(0,-1,0);

//                put(1,0-1);
//                put(1,1,4);
//                put(1,2,-1);
//
//                put(2,0,0);
//                put(2,1,-1);
//                put(2,2,0);
            }
        };

        filter2D(dest_img_roi, destination, -1, kernel);

        imageUtils.displayImage(destination);
        String Xpart = imageUtils.ocrOnImage(destination,0);
        String Xscale = Xpart.split("\n")[0];


        Xpart = imageUtils.ocrOnImage(image_roi,1);
        String Xlabel = Xpart.substring(Xpart.indexOf('\n') + 1);
        Xlabel = Xlabel.replaceAll("\n"," ");
        labels.add(Xscale);
        labels.add(Xlabel);

        return labels;

    }




    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }

}
