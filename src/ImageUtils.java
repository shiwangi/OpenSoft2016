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
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.equalizeHist;

/**
 * Created by shiwangi on 3/3/16.
 */
public class ImageUtils {
    public Mat bufferImageToMat(BufferedImage image, int type) {
        int rows = image.getWidth();
        int cols = image.getHeight();

        Mat newMat = new Mat(rows, cols, type);

        int type2 = image.getType();
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer())
                .getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), type);
        mat.put(0, 0, data);
        return mat;
    }

    Mat equalizeIntensity( Mat inputImage)
    {
        if(inputImage.channels() >= 3)
        {
            Mat ycrcb=inputImage.clone();

            cvtColor(inputImage,ycrcb,Imgproc.COLOR_BGR2YCrCb);

            List<Mat> channels=new ArrayList<>();

            split(ycrcb,channels);

            equalizeHist(channels.get(0), channels.get(0));

            Mat result=inputImage.clone();
            merge(channels,ycrcb);

            cvtColor(ycrcb,result,Imgproc.COLOR_YCrCb2BGR);

            return result;
        }
        return new Mat();
    }
    public BufferedImage mat2BufferedImage(Mat m) {

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

    public static int isRowWhite(int i, Mat imgROI) {
        for (int j = 0; j < imgROI.cols(); j++) {
            double[] color = imgROI.get(i, j);
            if (!isPixelWhite(color)) return 0;
        }
        return 1;
    }

    public static boolean isPixelWhite(double[] color) {
        if(color.length==1){
            return color[0]>=250;
        }
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

        Image img2 = mat2BufferedImage(mRgba);
        img2= img2.getScaledInstance(Math.min(700,mRgba.rows()),Math.min(700,mRgba.cols()),1);
        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        frame.setSize((img2.getWidth(null)) + 50, img2.getHeight(null) + 50);
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
        BufferedImage bimage = mat2BufferedImage(convertToBinary(img,255));
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        instance.setDatapath("/usr/share/tesseract-ocr");
        if (i == 0) instance.setTessVariable("tessedit_char_whitelist", ".0123456789");
        if (i == 1)
            instance.setTessVariable("tessedit_char_whitelist", "()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-/%");

        //instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
        try {
            String result = instance.doOCR(bimage);


            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }

    }

    public Mat convertToBinary(Mat mRgba,int val) {
        if(val==255){
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
        Mat mIntermediateMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC3);

        Mat newMat = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1);
        //subtract from 255.
        for (int i = 0; i < mRgba.rows(); i++) {
            for (int j = 0; j < mRgba.cols(); j++) {

                double[] color = mRgba.get(i, j);
                double[] newC = {255 - color[0], 255 - color[1], 255 - color[2]};
                mIntermediateMat.put(i, j, newC);
            }
        }

        Mat hsvImage = mRgba.clone();

        //invert
        cvtColor(mIntermediateMat, hsvImage, Imgproc.COLOR_RGB2HSV, 3);
        for (int i = 0; i < mRgba.rows(); i++) {
            for (int j = 0; j < mRgba.cols(); j++) {

                double[] color = mIntermediateMat.get(i, j);
                if (color[0] < 20 && color[1] < 20 && color[2] < 20) {
                    double[] newC = {val};
                    newMat.put(i, j, newC);
                } else {
                    double[] newC = {255-val};
                    newMat.put(i, j, newC);
                }
            }
        }
        Mat Kernel = new Mat(new Size(2, 2), CvType.CV_8UC1, Scalar.all(255));
        Mat temp = newMat.clone();
       // morphologyEx(newMat, temp, Imgproc.MORPH_OPEN, Kernel);
     //   morphologyEx(temp, newMat, Imgproc.MORPH_CLOSE, Kernel);
        return newMat;
    }

    public boolean isColBlack(Mat mIntermediateMat, int i) {
        int reqBlackpixels = (int) (mIntermediateMat.rows() * .7);
        int count = 0;
        for (int j = 0; j < mIntermediateMat.rows(); j++) {
            double[] color = mIntermediateMat.get(j, i);
            if (isPixelBlack(color)) count++;
        }
        return count > reqBlackpixels;
    }

    /*

    * If 3-channel images provided , returns boolean on the basis of hue
    * Otherwise checks for exactly black in bImage
     */
    public boolean  isPixelBlack(double[] color) {

        if (color.length == 3) {
                         if(color[0]<100 &&color[1]<100 && color[2]<100){
                                    return true;
                                }
                           else{
                                return false;
                                }

//            if (color[2] < 10) {
//                return true;
//            } else {
//                return false;
//            }
        }
        //System.out.print("hello");
        if (color[0] <= 10) {
            return true;
        }
        return false;
    }

    public boolean isRowBlack(Mat mIntermediateMat, int i) {
        int reqBlackpixels = (int) (mIntermediateMat.cols() * .7);
        int count = 0;
        for (int j = 0; j < mIntermediateMat.cols(); j++) {
            double[] color = mIntermediateMat.get(i, j);
            if (isPixelBlack(color)) count++;
        }
        return count > reqBlackpixels;
    }

    public double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }

    public String ocrOnImageForYScale(Mat image_roi, int i) {
        BufferedImage bimage = mat2BufferedImage(image_roi);
        bimage = mat2BufferedImage(convertToBinary(image_roi,255));
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        instance.setDatapath("/usr/share/tesseract-ocr");
        if (i == 0) instance.setTessVariable("tessedit_char_whitelist", ".0123456789");
        if (i == 1)
            instance.setTessVariable("tessedit_char_whitelist", "()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-/%");
        instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT);
        try {
            String result = instance.doOCR(bimage);
            System.out.println("Vertical Text \n" + result);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }
        //instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);


    }

    public Mat getCroppedImage(Mat source, double tolerance) {
        // Get our top-left pixel color as our "baseline" for cropping
        double[] baseColor = source.get(0, 0);
        displayImage(source);

        int width = source.width();
        int height = source.height();

        int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
        int bottomY = -1, bottomX = -1;
        int flagX = 0;
        for (int x = 0; x < width; x++) {
            if (flagX == 0 && isColWhite(x, source) == 1) {
                topX = x;
            } else {
                flagX = 1;
            }
        }
        flagX = 0;
        for (int x = width - 1; x >= 0; x--) {
            if (flagX == 0 && isColWhite(x, source) == 1) {
                bottomX = x;
            } else {
                flagX = 1;
            }
        }
        flagX = 0;
        for (int x = 0; x < height; x++) {
            if (flagX == 0 && isRowWhite(x, source) == 1) {
                topY = x;
            } else {
                flagX = 1;
            }
        }
        flagX = 0;
        for (int x = height - 1; x >= 0; x--) {
            if (flagX == 0 && isRowWhite(x, source) == 1) {
                bottomY = x;
            } else {
                flagX = 1;
            }
        }
topX = (topX== Integer.MAX_VALUE)?0:topX;
        topY = (topY== Integer.MAX_VALUE)?0:topY;
        bottomY = (bottomY==-1)?source.rows()-1:bottomY;
        bottomX = (bottomX==-1)?source.cols()-1:bottomX;
        int startx = Math.max(topX - 10, 0);
        int starty = Math.max(topY - 10, 0);
        width = Math.min(bottomX + 10 - startx, width - startx);
        height = Math.min(bottomY + 10 - starty, height - starty);


        Rect rectCrop = new Rect(startx, starty, width, height);
        Mat destination = new Mat(source, rectCrop);


        return destination;
    }

    private boolean colorWithinTolerance(double[] a, double[] b, double tolerance) {

        return (Math.abs(a[0] - b[0]) < tolerance && Math.abs(a[1] - b[1]) < tolerance && Math.abs(a[2] - b[2]) < tolerance);

    }

    public Mat cleanborders(Mat binary) {
        int rows = binary.rows();
        int cols = binary.cols();
        double newc[]={0};
        for(int i=0;i<50;i++){
            for(int j=0;j<rows;j++){
                binary.put(j,i,newc);

                binary.put(j,cols-1-i,newc);
            }
        }
        for(int i=0;i<50;i++){
            for(int j=0;j<cols;j++){
                binary.put(i,j,newc);
                binary.put(rows-1-i,j,newc);
            }
        }

        return binary;
    }
}
