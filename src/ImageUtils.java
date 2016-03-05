import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.rectangle;

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
        for (MatOfPoint contour : contours) {
            drawContours(mRgba, contours, i, new Scalar(0, 255, 0), 3);
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

    public void displaybuffImage(BufferedImage img2) {

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



    public String ocrOnImage(Mat img, int i) {
        //File imageFile = new File(fname);
        BufferedImage bimage = Mat2BufferedImage(img);
        bimage = Mat2BufferedImage(convertToBinary(img));
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        instance.setDatapath("/usr/share/tesseract-ocr");
        if(i==0) instance.setTessVariable("tessedit_char_whitelist", ".0123456789");
        if(i==1) instance.setTessVariable("tessedit_char_whitelist", "()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-/%");

        //instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
        try {
            String result = instance.doOCR(bimage);

            List<Rectangle> rects = instance.getSegmentedRegions(bimage, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
            for(Rectangle rect : rects){
                int h = rect.height;
                int w = rect.width;
                int x = (int) rect.getX();
                int y = (int) rect.getY();

               System.out.println(instance.doOCR(bimage,rect));
                rectangle(img, new Point(x,y), new Point(x+w,y+h),  new Scalar(0, 255, 255));
            }
            displayImage(img);
           // if(i!=0)
            //System.out.println(result);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }

    }




    public Mat convertToBinary(Mat mRgba) {
        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);
        for (int i = 0; i < mRgba.rows(); i++) {
            for (int j = 0; j < mRgba.cols(); j++) {

                double[] color = mRgba.get(i, j);
                if (isPixelWhite(color)) {
                    double[] newC = {255};
                    mIntermediateMat.put(i, j, newC);
                } else {
                    double[] newC = {0};
                    mIntermediateMat.put(i, j, newC);
                }
            }
        }
        return mIntermediateMat;
    }

    public boolean isColBlack(Mat mIntermediateMat, int i) {
        int reqBlackpixels = (int) (mIntermediateMat.rows()*.7);
        int count=0;
        for (int j = 0; j < mIntermediateMat.rows(); j++) {
            double[] color = mIntermediateMat.get(j, i);
            if (isPixelBlack(color)) count++;
        }
        return count>reqBlackpixels;
    }

    public boolean isPixelBlack(double[] color) {
        if(color.length==3){
            if(color[0]<10){
                return true;
            }
            else{
                return false;
            }
        }
        if (color[0] == 0) return true;
        return false;
    }

    public boolean isRowBlack(Mat mIntermediateMat, int i) {
        int reqBlackpixels = (int) (mIntermediateMat.cols()*.7);
        int count=0;
        for (int j = 0; j < mIntermediateMat.cols(); j++) {
            double[] color = mIntermediateMat.get(i,j);
            if (isPixelBlack(color)) count++;
        }
        return count>reqBlackpixels;
    }

    public Mat removecolorpixels(Mat matbox)
    {
        Mat hsvImage = matbox.clone();
        cvtColor(matbox, hsvImage, Imgproc.COLOR_RGB2HSV,3);
//        int minHvalue = 255;
//        for(int i = 0; i < hsvImage.rows(); i++) {
//            for (int j = 0; j < hsvImage.cols(); j++) {
//
//
//            }
//
//        }
        Mat ans= matbox.clone();
        double[] white = {255,255,255};
        double[] black = {0,0,0};
        for(int i = 0; i < hsvImage.rows(); i++)
        {
            for(int j=0; j<hsvImage.cols();j++)
            {

                int thresh = 30;
                double[] color = hsvImage.get(i,j);
                if (color[0] > thresh)
                {
                    ans.put(i,j, white);
                    continue;
                }

            }
        }
        return ans;

    }
}
