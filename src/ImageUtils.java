import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;

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
public Mat bufferImageToMat(BufferedImage image, int type){
    int rows = image.getWidth();
    int cols = image.getHeight();

    Mat newMat = new Mat(rows,cols,type);

    int type2 = image.getType();
    byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer())
            .getData();
    Mat mat = new Mat(image.getHeight(), image.getWidth(), type);
    mat.put(0, 0, data);
    return mat;
}
    public  BufferedImage mat2BufferedImage(Mat m) {

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
        BufferedImage img2 = mat2BufferedImage(mRgba);
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
        BufferedImage bimage = mat2BufferedImage(convertToBinary(img));
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
    public boolean isPixelBlack(double[] color) {
        if (color.length == 3) {
            if (color[0] < 10) {
                return true;
            } else {
                return false;
            }
        }
        if (color[0] == 0) return true;
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
        bimage = mat2BufferedImage(convertToBinary(image_roi));
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        instance.setDatapath("/usr/share/tesseract-ocr");
        if (i == 0) instance.setTessVariable("tessedit_char_whitelist", ".0123456789");
        if (i == 1)
            instance.setTessVariable("tessedit_char_whitelist", "()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-/%");
        instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT);
        try {
            String result = instance.doOCR(bimage);
            System.out.println("Vertical Text \n"+result);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }
        //instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);


    }

    public BufferedImage getCroppedImage(BufferedImage source, double tolerance) {
        // Get our top-left pixel color as our "baseline" for cropping
        int baseColor = source.getRGB(0, 0);

        int width = source.getWidth();
        int height = source.getHeight();

        int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
        int bottomY = -1, bottomX = -1;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if (colorWithinTolerance(baseColor, source.getRGB(x, y), tolerance)) {
                    if (x < topX) topX = x;
                    if (y < topY) topY = y;
                    if (x > bottomX) bottomX = x;
                    if (y > bottomY) bottomY = y;
                }
            }
        }

        BufferedImage destination = new BufferedImage( (bottomX-topX+1),
                (bottomY-topY+1), BufferedImage.TYPE_3BYTE_BGR);

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                topX, topY, bottomX, bottomY, null);

        return destination;
    }

    private boolean colorWithinTolerance(int a, int b, double tolerance) {
        int aAlpha  = (int)((a & 0xFF000000) >>> 24);   // Alpha level
        int aRed    = (int)((a & 0x00FF0000) >>> 16);   // Red level
        int aGreen  = (int)((a & 0x0000FF00) >>> 8);    // Green level
        int aBlue   = (int)(a & 0x000000FF);            // Blue level

        int bAlpha  = (int)((b & 0xFF000000) >>> 24);   // Alpha level
        int bRed    = (int)((b & 0x00FF0000) >>> 16);   // Red level
        int bGreen  = (int)((b & 0x0000FF00) >>> 8);    // Green level
        int bBlue   = (int)(b & 0x000000FF);            // Blue level

        double distance = Math.sqrt((aAlpha-bAlpha)*(aAlpha-bAlpha) +
                (aRed-bRed)*(aRed-bRed) +
                (aGreen-bGreen)*(aGreen-bGreen) +
                (aBlue-bBlue)*(aBlue-bBlue));

        // 510.0 is the maximum distance between two colors
        // (0,0,0,0 -> 255,255,255,255)
        double percentAway = distance / 510.0d;

        return (percentAway > tolerance);
    }
}
