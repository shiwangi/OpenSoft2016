import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;

import javax.sql.XAConnection;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.warpAffine;

/**
 * Created by rajitha on 3/3/16.
 */
public class AxisDetection {
    static double maxY = Double.MIN_VALUE;

    public List<String> getAxis(List<Point> corners, Mat mRgba) {
        //Find lower x-line
        List<String> labels = getXaxislabels(corners, mRgba);


        //Find left y-line
        List<String> ylabels = getYaxisLabels(corners, mRgba);
        labels.addAll(ylabels);

        return labels;
    }

    private List<String> getYaxisLabels(List<Point> corners, Mat mRgba) {
        List<String> labels = new ArrayList<>();
        double minX = Double.MAX_VALUE;
        for (Point point : corners) {
            if (point.x < minX) {
                minX = point.x;
            }
        }
        List<Point> YAxis = new ArrayList<>();
        for (Point point : corners) {
            if (dist(point, new Point(minX, point.y)) < 10) {
                YAxis.add(point);
            }
        }


        Point rightcorner = (YAxis.get(0).x > YAxis.get(1).x) ? YAxis.get(0) : YAxis.get(1);
        Rect rectCrop = new Rect(0, 0, (int) rightcorner.x, mRgba.rows());


        Mat image_roi = new Mat(mRgba, rectCrop);
        //displayImage(Mat2BufferedImage(image_roi));
        int count = 0;
        double min_idx = image_roi.cols() + 1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < image_roi.cols(); i++) {
            if (isColWhite(i, image_roi) == 0) {
                if (max < count) {
                    max = count;
                    min_idx = i - 1;
                    count = 0;
                }
            } else count = count + 1;
        }

        rectCrop = new Rect(0, 0, (int) (min_idx - max / 2), image_roi.rows());
        Mat labelImage_roi = new Mat(image_roi, rectCrop);

        rectCrop = new Rect((int) (min_idx - max / 2), 0, (image_roi.cols() - (int) (min_idx - max / 2)), image_roi.rows());
        Mat scaleImage = new Mat(image_roi, rectCrop);
        labels.add(ocrOnImage(Mat2BufferedImage(scaleImage)));

        Mat rotatedImage = getRotated(labelImage_roi);
        labels.add(ocrOnImage(Mat2BufferedImage(rotatedImage)));
        return labels;

    }

    private Mat getRotated(Mat labelImage_roi) {


        double len = max(labelImage_roi.cols(), labelImage_roi.rows());
        Point center = new Point(len / 2.0, len / 2.0);

        Mat rot = getRotationMatrix2D(center, -90, 1.0);
        warpAffine(labelImage_roi, labelImage_roi, rot, new Size(len, len));
        return labelImage_roi;

    }

    private List<String> getXaxislabels(List<Point> corners, Mat mRgba) {
        List<Point> lowerXAxis = getLowerAxisPoints(corners);
        List<String> labels = new ArrayList<>();

        //fetches roi for x-label ad scale
        Rect rect_Crop = null;
        Point left_corner = (lowerXAxis.get(0).x < lowerXAxis.get(1).x) ? lowerXAxis.get(0) : lowerXAxis.get(1);
        Rect rectCrop = new Rect((int) left_corner.x, (int) maxY, mRgba.cols() - (int) (left_corner.x) - 1, mRgba.rows() - (int) maxY);
        Mat image_roi = new Mat(mRgba, rectCrop);
        //displayImage(Mat2BufferedImage(image_roi));

        String Xpart = ocrOnImage(Mat2BufferedImage(image_roi));
        String Xscale = Xpart.split("\n")[0];
        String Xlabel = Xpart.substring(Xpart.charAt('\n') + 1);
        labels.add(Xscale);
        labels.add(Xlabel);


        System.out.println("Xlablel, Xsale" + Xlabel + Xscale);
        return labels;


    }

    private List<Point> getLowerAxisPoints(List<Point> corners) {

        for (Point point : corners) {
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        List<Point> lowerXAxis = new ArrayList<>();
        for (Point point : corners) {
            if (dist(point, new Point(point.x, maxY)) < 10) {
                lowerXAxis.add(point);
            }
        }
        return lowerXAxis;
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



    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }

    private static int isColWhite(int i, Mat imgROI) {
        for (int j = 0; j < imgROI.rows(); j++) {
            double[] color = imgROI.get(j, i);
            if (!isWhite(color)) return 0;
        }
        return 1;
    }

    private static boolean isWhite(double[] color) {
        if (color[0] == 255 && color[1] == 255 && color[2] == 255) return true;
        return false;
    }
}
