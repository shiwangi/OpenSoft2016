import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.warpAffine;

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
        String YScale = imageUtils.ocrOnImage(imageUtils.Mat2BufferedImage(scaleImage));
        YScale = YScale.replaceAll("\n", " ");
        labels.add(YScale);

        Mat rotatedImage = getRotated(labelImage_roi);
        String Ylabel = imageUtils.ocrOnImage(imageUtils.Mat2BufferedImage(rotatedImage));
        Ylabel = Ylabel.replaceAll("\n", " ");
        labels.add(Ylabel);

        return labels;

    }

    private List<Point> getYAxisPoints(double minX, List<Point> corners) {
        List<Point> YAxis = new ArrayList<>();
        for (Point point : corners) {
            if (dist(point, new Point(minX, point.y)) < 10) {
                YAxis.add(point);
            }
        }
        return YAxis;
    }

    private double findMinX(List<Point> corners) {
        double minX = Double.MAX_VALUE;
        for (Point point : corners) {
            if (point.x < minX) {
                minX = point.x;
            }
        }
        return  minX;
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

        String Xpart = imageUtils.ocrOnImage(imageUtils.Mat2BufferedImage(image_roi));
        String Xscale = Xpart.split("\n")[0];
        String Xlabel = Xpart.substring(Xpart.indexOf('\n') + 1);
        Xlabel = Xlabel.replaceAll("\n"," ");
        labels.add(Xscale);
        labels.add(Xlabel);

        return labels;

    }

    private List<Point> getLowerAxisPoints(List<Point> corners) {

        for (Point point : corners) {
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        List<Point> lowerXAxis = new ArrayList<>();
        for (Point point : corners) {
            if (dist(point, new Point(point.x, maxY)) < 10) {
                lowerXAxis.add(point);
            }
        }
        return lowerXAxis;
    }


    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }

}
