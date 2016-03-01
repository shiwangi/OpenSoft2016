

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 * Created by shiwangi on 26/2/16.
 */
public class Main {
    //Loads image and apply houghline detection.
    public static void main(String args[]) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);
        String fname = "/home/shiwangi/try3.png";


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
       // ocrOnImage(fname);

        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }

       findHouglines(mRgba);
    }

    private static void findHouglines(Mat mRgba) {
        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);

        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2GRAY);

        Imgproc.Canny(mIntermediateMat, mIntermediateMat, 80, 100);
        int rows = mRgba.rows();
        int cols = mRgba.cols();
//        displayImage(Mat2BufferedImage(mIntermediateMat));
        Mat lines = new Mat();
        int threshold = 10;

        //The minimum line size should be 80% of the width / height of the plot image
        int minLineSize =(int) (80.0/100.0 * Math.min(rows,cols));
        int lineGap = 30;


        Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

        for (int x = 0; x < lines.height(); x++)
        {
            double[] vec = lines.get(x,0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            line(mRgba, start, end, new Scalar(255,0,0), 3);

        }


        displayImage(Mat2BufferedImage(mRgba));
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

    public static void displayImage(Image img2) {

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
