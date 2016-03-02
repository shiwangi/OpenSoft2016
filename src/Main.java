import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.drawContours;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    public static void main(String args[]) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);
        String fname = "./resources/image1.png";


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //read the image file.
        Mat mRgba = imread(fname);
        displayImage(Mat2BufferedImage(mRgba));
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        //ocrOnImage(fname);
        //get rid of colourful elements
        //  removeColorFulPixel(mRgba);

        //detect the axes
        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if(contour!=null)
            drawContours(mRgba, contours, 0, new Scalar(0, 255, 0), 10);
        else{
            System.out.println("Could not find border axes");
        }
        List<Point> lines = contour.toList();
        List<Point> corners = new ArrayList<>();
        int sz = lines.size();
        Point pt = lines.get(0);
        corners.add(pt);
        for(int i=1;i<sz;i++){
            if(dist(pt,lines.get(i))<10){

            }
            else {
                pt = lines.get(i);
                corners.add(pt);
            }
        }

        //Find lower x-line
        double minY = Double.MAX_VALUE;
        for(Point point:corners){
            if(point.y<minY){
                minY = point.y;
            }
        }
        List<Point> lowerXAxis = new ArrayList<>();
        for(Point point:corners){
            if(dist(point,new Point(point.x,minY))<10){
                lowerXAxis.add(point);
            }
        }
        displayImage(Mat2BufferedImage(mRgba));

        //manipulate the axes found to clip the image

        //  findHouglines(mRgba);
    }

    private static double dist(Point pt, Point point) {
        return (pt.x-point.x)*(pt.x-point.x) + (pt.y-point.y)*(pt.y-point.y);
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


    private static void ocrOnImage(String fname) {
        File imageFile = new File(fname);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("/usr/share/tesseract-ocr");
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
