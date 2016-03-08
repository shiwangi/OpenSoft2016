import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by rajitha on 3/3/16.
 */
public class AxisDetection {
    static ImageUtils imageUtils;
    static Mat xscaleImage, yscaleImage;

    public AxisDetection(Mat xscaleImage, Mat yscaleImage) {
        imageUtils = new ImageUtils();
        this.xscaleImage = xscaleImage;
        this.yscaleImage = yscaleImage;
    }

    public List<String> getAxis() {

        //Find lower x-line
        List<String> labels = getXaxislabels();

        //Find left y-line
        List<String> ylabels = getYaxisLabels();

        labels.addAll(ylabels);
        return labels;
    }

    private List<String> getYaxisLabels() {
        List<String> labels = new ArrayList<>();
        List<Mat> scaleAndLabelMat = getMatsByContourMatching();

        Mat legendimage = scaleAndLabelMat.get(0);
        Mat legendImage2 = scaleAndLabelMat.get(1);

        String YScale = imageUtils.ocrOnImage(legendimage, 0);
        YScale = YScale.replaceAll("\n", " ");
        labels.add(YScale);


        Mat rotated = getRotated(legendImage2);
        imageUtils.displayImage(rotated);
        YScale = imageUtils.ocrOnImage(rotated, 1);
        YScale = YScale.replaceAll("\n", " ");
        labels.add(YScale);
        return labels;

    }

    private List<Mat> getMatsByContourMatching() {
        Mat img2 = yscaleImage.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageUtils.convertToBinary(yscaleImage), contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        imageUtils.drawContoursOnImage(contours, yscaleImage);
        imageUtils.displayImage(yscaleImage);

        Mat img = yscaleImage.clone();

        Mat templ = imread("./resources/scalematch.png");
        resize(templ, templ, new Size(img.cols() / 2.0, img.rows()));
        imageUtils.displayImage(templ);
        System.out.println("\nRunning Template Matching");

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        int match_method = Imgproc.TM_CCORR_NORMED;
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF
                || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }


        Rect rectCrop = new Rect((int) matchLoc.x, (int) matchLoc.y, (int) templ.cols(), (int) templ.rows());
        Mat legendimage = new Mat(img2, rectCrop);

        //imageUtils.displayImage(legendimage);
        List<Mat> results = new ArrayList<>();
        results.add(legendimage);

        rectCrop = new Rect(0, 0, ((int) matchLoc.x), templ.rows());
        Mat legendimage2 = new Mat(img2, rectCrop);
        results.add(legendimage2);
        //imageUtils.displayImage(legendimage2);

        return results;
    }


    private Mat getRotated(Mat labelImage_roi) {

        double len = max(labelImage_roi.cols(), labelImage_roi.rows());
        double height = (len == labelImage_roi.cols()) ? labelImage_roi.rows() : labelImage_roi.cols();
        Point center = new Point(len / 2.0, len / 2.0);

        Mat rot = getRotationMatrix2D(center, -90, 1.0);
        warpAffine(labelImage_roi, labelImage_roi, rot, new Size(len, height));
        return labelImage_roi;

    }

    private List<String> getXaxislabels() {
        List<String> labels = new ArrayList<>();


        Mat image_roi = xscaleImage;

        //imageUtils.displayImage(xscaleImage);


        String Xpart = imageUtils.ocrOnImage(image_roi, 0);
        String Xscale = Xpart.split("\n")[0];


        Xpart = imageUtils.ocrOnImage(image_roi, 1);
        String Xlabel = Xpart.substring(Xpart.indexOf('\n') + 1);
        Xlabel = Xlabel.replaceAll("\n", " ");
        labels.add(Xscale);
        labels.add(Xlabel);

        return labels;

    }


    private static double dist(Point pt, Point point) {
        return (pt.x - point.x) * (pt.x - point.x) + (pt.y - point.y) * (pt.y - point.y);
    }

    public List<Double> getMinMaxValues(List<String> labels) {
        List<Double> values = new ArrayList<>();

        String[] xscale = labels.get(0).split(" ");
        try {
            values.add(Double.parseDouble(xscale[0]));
            values.add(Double.parseDouble(xscale[xscale.length - 1]));
        } catch (NumberFormatException e) {
            values.add(0.0);
            values.add(100.0);
        }

        String[] yscale = labels.get(2).split(" ");
        try {
            values.add(Double.parseDouble(yscale[yscale.length - 1]));
            values.add(Double.parseDouble(yscale[0]));
        } catch (NumberFormatException e) {
            values.add(0.0);
            values.add(100.0);
        }
        return values;
    }


    private static double getRIfAp(String[] scale) {
        if (!isValidscale(scale)) return -1;
        ArrayList<Double> scaleNum = new ArrayList<>();
        for (int i = 0; i < scale.length; i++) {
            if (isDouble(scale[i])) {
                double num = Double.parseDouble(scale[i]);
                scaleNum.add(num);
            }
        }

        List<Double> possibleRValues = new ArrayList<>();

        for (int i = 0; i < scaleNum.size(); i++) {

            for (int j = i + 1; j < scaleNum.size(); j++) {
                double r = scaleNum.get(j) - scaleNum.get(i);
                r = r / (j - i + 1);
                possibleRValues.add(r);
            }
        }
        Collections.sort(possibleRValues);
        int maxCount = 0;
        double mostProbableR = -1;
        int count = 0;
        int sz = possibleRValues.size();
        double num = 0;
        if (sz > 0) {
            num = possibleRValues.get(0);
            count++;
        }

        for (int j = 1; j < sz; j++) {
            if (possibleRValues.get(j) == num) {
                count++;
            } else {
                if (count >= maxCount) {
                    maxCount = count;
                    mostProbableR = num;
                }
                num = possibleRValues.get(j);
            }
        }
        //we have the most probable R - now we need to get the range

        return mostProbableR;


    }

    private static boolean isValidscale(String[] scale) { //checks very bad scales.
        int count = 0;
        for (int i = 0; i < scale.length; i++) {
            if (count > scale.length / 3) return false;
            if (!isDouble(scale[i])) count++;

        }
        return true;
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
