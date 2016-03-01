

import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.logging.Log;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import sun.org.mozilla.javascript.tools.shell.Environment;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    //Loads image and apply houghline detection.
    public static void main(String args[]) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);
        String fname = "./resources/image3.png";


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // ocrOnImage(fname);

        //read the image file.
        Mat mRgba = imread(fname);
        displayImage(Mat2BufferedImage(mRgba));
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        //get rid of colourful elements
      //  removeColorFulPixel(mRgba);

       tryelse(mRgba);
      //  findHouglines(mRgba);
    }

    private static void tryelse(Mat mRgba) {
        //convert the image to black and white does (8 bit)

        //find the contours

        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);

        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2GRAY);
        threshold(mIntermediateMat, mIntermediateMat, 0.1 * 255, 255.0, THRESH_BINARY); //GRAY 2 Binary based on threshold
      //  displayImage(Mat2BufferedImage(mIntermediateMat));
        //    Imgproc.Canny(mIntermediateMat, mIntermediateMat, 80, 100);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mIntermediateMat, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

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
        each = squareContours.iterator();
        int idx = 0;
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > .1*maxArea)
            {
                mContours.add(contour);
                drawContours(mRgba, mContours, idx, new Scalar(0, 255, 0), 20);

                idx++;
            }



        }


        displayImage(Mat2BufferedImage(mRgba));
    }

    public static boolean isContourSquare(MatOfPoint thisContour) {

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

    private static List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {
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

    private static void removeColorFulPixel(Mat mRgba) {
        int rows = mRgba.rows();
        int cols = mRgba.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] color = mRgba.get(i, j);
                if (color[0] < 150 && color[1] < 150 && color[2] < 150) {

                } else {
                    double[] newC = {255, 255, 255};
                    mRgba.put(i, j, newC);
                }
            }
        }

        displayImage(Mat2BufferedImage(mRgba));
    }



    private static void findHouglines(Mat mRgba) {
        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);

        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2GRAY);

        Imgproc.Canny(mIntermediateMat, mIntermediateMat, 80, 100);
        int rows = mRgba.rows();
        int cols = mRgba.cols();
        displayImage(Mat2BufferedImage(mIntermediateMat));
        Mat lines = new Mat();
        int threshold = 10;

        //The minimum line size should be 80% of the width / height of the plot image
        int minLineSize =(int) (80.0/100.0 * Math.min(rows,cols));
        //int minLineSize = 30;
        int lineGap = 30;


        Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

//        for (int x = 0; x < lines.height(); x++)
//        {
//            double[] vec = lines.get(x,0);
//            double x1 = vec[0],
//                    y1 = vec[1],
//                    x2 = vec[2],
//                    y2 = vec[3];
//            if((y1-y2)/(x1-x2)<0.01 ||(x1-x2)==0|| (y1-y2)/(x1-x2)>100000) {
//                Point start = new Point(x1, y1);
//                Point end = new Point(x2, y2);
//
//                line(mRgba, start, end, new Scalar(255, 0, 0), 3);
//            }
//
//        }
        ArrayList<Point> corners = new ArrayList<>();
        for (int x = 0; x < lines.height(); x++) {

            for (int y = x + 1; y < lines.height(); y++) {
                double[] a = lines.get(x, 0);

                double b[] = lines.get(y, 0);


                Point pt=   computeIntersect(a,b);
                if (pt.x >= 0 && pt.y >= 0)
                    corners.add(pt);
                circle(mRgba,pt,4, new Scalar(255, 0, 0));
                }
            }
new MatOfPoint2f()
;

        displayImage(Mat2BufferedImage(mRgba));
    }

    static Point computeIntersect(double a[], double b[])
    {
        double x1 = a[0], y1 = a[1], x2 = a[2], y2 = a[3];
        double x3 = b[0], y3 = b[1], x4 = b[2], y4 = b[3];
        float d = (float) ((x1-x2) * (y3-y4) - ((y1-y2) * (x3-x4)));
        if (d!=0)
        {
            Point pt=new Point(0,0);
            pt.x = ((x1*y2 - y1*x2) * (x3-x4) - (x1-x2) * (x3*y4 - y3*x4)) / d;
            pt.y = ((x1*y2 - y1*x2) * (y3-y4) - (y1-y2) * (x3*y4 - y3*x4)) / d;
            return pt;
        }
        else
        return new Point(-1, -1);
    }



    private static double dist(double x1, double y1, double centroidX, double centroidY) {
        return (x1 - centroidX) * (x1 - centroidX) + (y1 - centroidY) * (y1 - centroidY);
    }

    private static void ocrOnImage(String fname) {
        File imageFile = new File(fname);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

    }

    public static BufferedImage Mat2BufferedImage(Mat m) {

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    public static void displayImage(BufferedImage img2) {

        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


}
