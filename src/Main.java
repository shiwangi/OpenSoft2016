import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    static ImageUtils imageUtils;


    public static void main(String args[]) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);
        imageUtils = new ImageUtils();
        String fname = "./resources/image1.png";


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

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }


    static Point computeIntersect(double a[], double b[]) {
        double x1 = a[0], y1 = a[1], x2 = a[2], y2 = a[3];
        double x3 = b[0], y3 = b[1], x4 = b[2], y4 = b[3];
        float d = (float) ((x1 - x2) * (y3 - y4) - ((y1 - y2) * (x3 - x4)));
        if (d != 0) {
            Point pt = new Point(0, 0);
            pt.x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            pt.y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
            return pt;
        } else
            return new Point(-1, -1);
    }


    private static String ocrOnImage(BufferedImage bimage) {
        //File imageFile = new File(fname);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("/usr/share/tesseract-ocr");
        try {
            String result = instance.doOCR(bimage);
            //System.out.println(result);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }

    }


}
