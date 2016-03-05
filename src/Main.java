import org.opencv.core.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    static ImageUtils imageUtils;


    public static void main(String args[]) throws IOException {

        imageUtils = new ImageUtils();
        String fname = "./resources/image3.png";


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }

        imageUtils.displayImage(mRgba);

        Mat mIntermediateMat = imageUtils.convertToBinary(mRgba);
        Point pt = findfirstBlackRowwAndCol(mIntermediateMat);
        int x = (int) pt.x;
        int y = (int) pt.y;

        Point pt2 = findLastBlackRowAndCol(mIntermediateMat);
        int lastx = (int) pt2.x;
        int lasty = (int) pt2.y;
        Rect rectCrop = new Rect(0, 0, x, mIntermediateMat.rows());
        Mat YscaleImage = new Mat(mRgba, rectCrop);
        imageUtils.displayImage(YscaleImage);


        rectCrop = new Rect(x - 3, y + 1, mIntermediateMat.cols() - x + 3, mIntermediateMat.rows() - y - 1);
        Mat XscaleImage = new Mat(mRgba, rectCrop);
        imwrite("/home/rajitha/Desktop/result.png", XscaleImage);
        imageUtils.displayImage(XscaleImage);

        Mat graphImageBnW = getGraphImage(x, y, mIntermediateMat);

        //detect the axes
        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, graphImageBnW);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
//            imageUtils.drawContoursOnImage(contours, mRgba);
//            imageUtils.displayImage(mRgba);
            List<Point> corners = getCornersFromRect(contour);
            AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
            List<String> labels = axisDetection.getAxis(corners, mRgba);

//            List<Double> minmaxValues = getminmaxValues(labels);
//            System.out.println(labels.toString());
        } else {
            System.out.println("Could not find border axes");

        }

        rectCrop = new Rect(x + 5, lasty + 5, lastx - x - 10, y - lasty - 10);
        Mat graphImage = new Mat(mRgba, rectCrop);
        imageUtils.displayImage(graphImage);

//        PlotValue plotValue = new PlotValue(graphImage, 0, 100, 0, 100);
//        plotValue.populateTable();




        LegendDetection legendDetection = new LegendDetection(graphImage);
        String legend = legendDetection.detectLegend();
        //System.out.println(legend);
    }

    private static List<Double> getminmaxValues(List<String> labels) {
        List<Double> values = new ArrayList<>();

        String[] xscale = labels.get(0).split(" ");
        try {
            values.add(Double.parseDouble(xscale[0]));
            values.add(Double.parseDouble(xscale[xscale.length - 1]));
        } catch (NumberFormatException e) {
            values.add(0.0);
            values.add(100.0);
        }

        String[] yscale = labels.get(2).split(" ");
        try {
            values.add(Double.parseDouble(yscale[yscale.length - 1]));
            values.add(Double.parseDouble(yscale[0]));
        } catch (NumberFormatException e) {
            values.add(0.0);
            values.add(100.0);
        }
        return values;

    }

    private static boolean isAP(String[] scale) {
        if (!isValidscale(scale)) return false;
        ArrayList<Double> scaleNum = new ArrayList<>();
        for (int i = 0; i < scale.length; i++) {
            if (isDouble(scale[i])) {
                double num = Double.parseDouble(scale[i]);
                scaleNum.add(num);
            }
        }

        List<Double> possibleRValues = new ArrayList<>();
        int i = 0;
        double num = scaleNum.get(i);
        for (i = 1; i < scaleNum.size(); i++) {
            double r = scaleNum.get(i) - num;
            num = scaleNum.get(i);
            possibleRValues.add(r);
        }
        return true;


    }

    private static boolean isValidscale(String[] scale) { //checks very bad scales.
        int count = 0;
        for (int i = 0; i < scale.length; i++) {
            if (count > scale.length / 3) return false;
            if (!isDouble(scale[i])) count++;

        }
        return true;
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Point findLastBlackRowAndCol(Mat mIntermediateMat) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < mIntermediateMat.cols(); i++) {
            if (imageUtils.isColBlack(mIntermediateMat, i)) {
                x = i;
            }
        }
        for (int i = mIntermediateMat.rows() - 1; i >= 0; i--) {
            if (imageUtils.isRowBlack(mIntermediateMat, i)) {
                y = i;
            }
        }
        return new Point(x, y);
    }

    private static Mat getGraphImage(int x, int y, Mat mIntermediateMat) {
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

    private static Point findfirstBlackRowwAndCol(Mat mIntermediateMat) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < mIntermediateMat.cols(); i++) {
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

    private static List<Point> getCornersFromRect(MatOfPoint contour) {
        List<Point> lines = contour.toList();
        List<Point> corners = new ArrayList<>();
        int sz = lines.size();
        Point pt = lines.get(0);
        corners.add(pt);
        for (int i = 1; i < sz; i++) {
            if (dist(pt, lines.get(i)) < 10) {

            } else {
                pt = lines.get(i);
                corners.add(pt);
            }
        }
        return corners;

    }


    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }


}
