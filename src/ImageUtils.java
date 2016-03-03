import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import static org.opencv.imgproc.Imgproc.drawContours;

/**
 * Created by shiwangi on 3/3/16.
 */
public class ImageUtils {

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


    public static int isColWhite(int i, Mat imgROI) {
        for (int j = 0; j < imgROI.rows(); j++) {
            double[] color = imgROI.get(j, i);
            if (!isPixelWhite(color)) return 0;
        }
        return 1;
    }

    public static boolean isPixelWhite(double[] color) {
        if (color[0] >= 200 && color[1] >= 200 && color[2] >= 200) return true;
        return false;
    }


    public void drawContoursOnImage(List<MatOfPoint> contours, Mat mRgba) {
        int i = 0;
        for (MatOfPoint cont : contours) {
            drawContours(mRgba, contours, i, new Scalar(0, 255, 0), 10);
            i++;
        }
    }


    public void displayImage(Mat mRgba) {
        BufferedImage img2 = Mat2BufferedImage(mRgba);
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
    }

    public static String ocrOnImage(BufferedImage bimage) {
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

    public Mat convertToBinary(Mat mRgba) {
        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);
        for(int i=0;i<mRgba.rows();i++){
            for(int j=0;j<mRgba.cols();j++){

                double[] color = mRgba.get(i, j);
                if(isPixelWhite(color)){
                    double[] newC = {255};
                    mIntermediateMat.put(i, j, newC);
                }
                else{
                    double[] newC = {0};
                    mIntermediateMat.put(i, j, newC);
                }
            }
        }
        return mIntermediateMat;
    }
}
