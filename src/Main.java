import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

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
        //get rid of colourful elements
        //  removeColorFulPixel(mRgba);

        //detect the axes
        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
            imageUtils.drawContoursOnImage(contours, mRgba);
            List<Point> corners = getCornersFromRect(contour);
            AxisDetection axisDetection = new AxisDetection();
            List<String> labels = axisDetection.getAxis(corners, mRgba);
            System.out.println(labels.toString());
        } else {
            System.out.println("Could not find border axes");

        }
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
