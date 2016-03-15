import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;

/**
 * ImageUtils class is the widely used in the project.Almost every other class has an instance of ImageUtils.
 * This class has methods used mostly while dealing with Mat/image objects,writing,reading,displaying,etc.,
 */
public class ImageUtils {

    static String RPATH = "./resources";


    /**
     * This method is used to increase the saturation of an Image, generally scanned Pdfs have low quality.
     * helps in identifying colors easily.
     * @param graph input Mat Object.
     * @return The saturted mat.
     */
    Mat increaseSaturation(Mat graph) {
        //   Mat saturated = graph.clone();
        List<Point> whiteList = new ArrayList<>();
        for (int i = 0; i < graph.cols(); i += 1) {


            for (int j = 0; j < graph.rows(); j += 1) {

                double[] colourCompare = graph.get(j, i);
                if (isPixelWhite(colourCompare)) {
                    whiteList.add(new Point(j, i));
                }
            }
        }
        List<Point> blackList = new ArrayList<>();
        for (int i = 0; i < graph.cols(); i += 1) {


            for (int j = 0; j < graph.rows(); j += 1) {

                double[] colourCompare = graph.get(j, i);
                if (isPixelBlack(colourCompare)) {
                    blackList.add(new Point(j, i));
                }
            }
        }
        String path = "/input.png";
        String inputPath = RPATH + path;
        imwrite(inputPath, graph);
        ImagePlus im = new ImagePlus(inputPath);
        ContrastEnhancer enh = new ContrastEnhancer();

        enh.stretchHistogram(im, 0.5);

        BufferedImage res = im.getBufferedImage();
        path = "/enhanced.jpg";
        String outFile = RPATH + path;
        File outputfile = new File(outFile);
        try {
            ImageIO.write(res, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat readFile = imread(outFile);

        int sz = whiteList.size();
        for (int i = 0; i < sz; i++) {
            Point p = whiteList.get(i);
            double c[] = {255, 255, 255};
            readFile.put((int) p.x, (int) p.y, c);
        }
        sz = blackList.size();
        for (int i = 0; i < sz; i++) {
            Point p = blackList.get(i);
            double c[] = {0, 0, 0};
            readFile.put((int) p.x, (int) p.y, c);
        }
        return readFile;

    }

    /**
     * Returns a BufferedImage object for an input Mat object.
     * @param m input mat
     * @return The bufferedImage
     */
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

    /**
     * This method when given a column index, checks whether that column is white or not,
     * Used to trim white spaces.
     * @param i
     * @param imgROI
     * @return 0 if not white.
     */

    public static int isColWhite(int i, Mat imgROI) {
        for (int j = 0; j < imgROI.rows(); j++) {
            double[] color = imgROI.get(j, i);
            if (!isPixelWhite(color)) return 0;
        }
        return 1;
    }

    /**
     * This method when given a row index, checks whether that row is white or not,
     * Used to trim white spaces.
     * @param i
     * @param imgROI
     * @return 0 if not white.
     */
    public static int isRowWhite(int i, Mat imgROI) {
        for (int j = 0; j < imgROI.cols(); j++) {
            double[] color = imgROI.get(i, j);
            if (!isPixelWhite(color)) return 0;
        }
        return 1;
    }

    /**
     * Checks whether the given color triplet or binary color is white or not
     * @param color
     * @return false if not white
     */
    public static boolean isPixelWhite(double[] color) {
        if (color.length == 1) {
            return color[0] >= 250;
        }
        if (color[0] >= 200 && color[1] >= 200 && color[2] >= 200) return true;
        return false;
    }


    /**
     * Draws the contours on the input Mat object in green color
     * @param contours
     * @param mRgba
     */
    public void drawContoursOnImage(List<MatOfPoint> contours, Mat mRgba) {
        if (contours == null)
            return;
        int i = 0;
        for (MatOfPoint contour : contours) {
            drawContours(mRgba, contours, i, new Scalar(0, 255, 0), 3);
            i++;
        }
    }


    /**
     * Displays the mat object/Image in a jFrame.
     * @param mRgba
     */
    public void displayImage(Mat mRgba) {

        Image img2 = mat2BufferedImage(mRgba);

        if (mRgba.rows() > 800 && mRgba.cols() > 800)
            img2 = img2.getScaledInstance((int) (mRgba.rows() * .6), (int) (mRgba.cols() * .6), 1);
        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        frame.setSize((img2.getWidth(null)) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    /**
     * Displays a bufferedImage.
     * @param img2
     */
    public void displaybuffImage(BufferedImage img2) {

        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }


    /**
     * Given a Mat object, runs OCR on the image using tesseract-ocr.
     * Very Important method, the entire text recognition is done through this method.
     * @param image
     * @param i The flag which sets the acceptable characters.
     * @return the Output String
     */
    public String ocrOnImage(Mat image, int i) {
        //File imageFile = new File(fname);
        BufferedImage bimage = mat2BufferedImage(convertToBinary(image, 255));
        String path = "./resources/tessInput.png";
        //displayImage(image);
        File tessFile = new File(path);
        Process pr = null;
        String addConfig = " ";
        if(i==0)
        {
            addConfig = "-c tessedit_char_whitelist=\"-.0123556789\" ";
        }
        if(i==1)
        {
            addConfig = "-c tessedit_char_whitelist=\"%()abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZ\" ";
        }
       // String line = null;
        try {
            ImageIO.write(bimage, "png", tessFile);
            Runtime rt = Runtime.getRuntime();

            pr = rt.exec("tesseract " + addConfig +path + " ./resources/outputtext");

            String everything = "";
            int exitVal = 0;
            try {
                exitVal = pr.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(exitVal==0) {
                BufferedReader br = new BufferedReader(new FileReader("./resources/outputtext.txt"));
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    everything = sb.toString();
                   // everything.replaceAll("  "," ");
                } finally {
                    br.close();
                }
            }
            else {

                BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                String m, s = "";
                while ((m = error.readLine()) != null) {
                    s += "\n" + m;
                }
                System.out.println("Exited with error code " + s);
            }

            return everything;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method converts a 3 channel image to a binary image.(single channel)
     * @param mRgba
     * @param val
     * @return
     */
    public Mat convertToBinary(Mat mRgba, int val) {
        if (val == 255) {
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
                    double[] newC = {255 - val};
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

    /**
     * Checks whether that particular column is black, 70% of pixels black.
     * @param mIntermediateMat
     * @param i
     * @return
     */
    public boolean isColBlack(Mat mIntermediateMat, int i) {
        int reqBlackpixels = (int) (mIntermediateMat.rows() * .7);
        int count = 0;
        for (int j = 0; j < mIntermediateMat.rows(); j++) {
            double[] color = mIntermediateMat.get(j, i);
            if (isPixelBlack(color)) count++;
        }
        return count > reqBlackpixels;
    }



    /**
     * * If 3-channel images provided , returns boolean on the basis of hue
    * Otherwise checks for exactly black in bImage
     */

    public boolean isPixelBlack(double[] color) {

        if (color.length == 3) {
            if (color[0] < 100 && color[1] < 100 && color[2] < 100) {
                return true;
            } else {
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

    /**
     * Checks whether the row is Black in a Mat object
     * @param mIntermediateMat
     * @param i
     * @return
     */
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


    /**
     * Trims the white spaces of the input image along the edges.
     * @param source
     * @return
     */

    public Mat getCroppedImage(Mat source) {
        // Get our top-left pixel color as our "baseline" for cropping
        double[] baseColor = source.get(0, 0);
        // displayImage(source);

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
        topX = (topX == Integer.MAX_VALUE) ? 0 : topX;
        topY = (topY == Integer.MAX_VALUE) ? 0 : topY;
        bottomY = (bottomY == -1) ? source.rows() - 1 : bottomY;
        bottomX = (bottomX == -1) ? source.cols() - 1 : bottomX;
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

    /**
     * Cleans the borders of the given image, some times scanned pdfs hae loud borders.
     * making them uniform is necessary
     * @param binary
     * @return
     */
    public Mat cleanborders(Mat binary) {
        int rows = binary.rows();
        int cols = binary.cols();
        double newc[] = {0};
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < rows; j++) {
                binary.put(j, i, newc);

                binary.put(j, cols - 1 - i, newc);
            }
        }
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < cols; j++) {
                binary.put(i, j, newc);
                binary.put(rows - 1 - i, j, newc);
            }
        }

        return binary;
    }

    /**
     * removes the colored pixels in the image, used in legend detection.
     * @param img1
     * @return
     */
    public Mat removeColorPixels(Mat img1) {

        Mat img = img1.clone();
        double[] white = {255, 255, 255};
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {

                if (!isPixelBlack(img.get(i, j)) && !isPixelWhite(img.get(i, j))) img.put(i, j, white);
            }
        }

        return img;
    }
}
