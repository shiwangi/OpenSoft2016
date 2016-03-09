import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.resize;

/**
 * Created by shiwangi on 5/3/16.
 */
public class LegendDetection {
    Mat graphImage;
    Mat binaryGraphImage;
    ImageUtils imageUtils;
    Map<Colour,Boolean> colourOfPlotsHSV;

    public LegendDetection(Mat graphImage, Map colourOfPlotsHSV) {
        this.graphImage = graphImage;
        imageUtils = new ImageUtils();
        this.colourOfPlotsHSV = colourOfPlotsHSV;
    }

    public Mat detectLegendImageMatch() {
        Mat img = graphImage.clone();
        Mat templ = imread("./resources/pic1.png");
        //System.out.print(imageUtils.ocrOnImage(templ, 2));
        resize(templ, templ, new Size(img.cols() / 4.0, img.rows() / 2));
        imageUtils.displayImage(templ);
        System.out.println("\nRunning Template Matching");

        // / Create the result matrix
        int result_cols = templ.cols() + 1;
        int result_rows = templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        int match_method = Imgproc.TM_CCORR_NORMED;
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
        //    Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        //imwrite("./resources/out2.png", result);

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF
                || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
            System.out.println(mmr.minVal);
        } else {
            matchLoc = mmr.maxLoc;
            System.out.println(mmr.maxVal);
        }

        // / Show me what you got
        rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols() + 15,
                matchLoc.y + templ.rows() + 15), new Scalar(0, 255, 0));

        // Save the visualized detection.
        imageUtils.displayImage(img);
        Rect rectCrop = new Rect((int) matchLoc.x, (int) matchLoc.y, (int) templ.cols(), (int) templ.rows());
        Mat legendimage = new Mat(img, rectCrop);
//        Mat cleanlegend = imageUtils.removecolorpixels(graphImage);
//         imageUtils.displayImage(cleanlegend);
//        cleanlegend = imageUtils.convertToBinary(cleanlegend);
//        imageUtils.displayImage(cleanlegend);
//
//        String legend = imageUtils.ocrOnImage((cleanlegend),2);
//
        imageUtils.displayImage(legendimage);
        return legendimage;
    }

    public String detectLegend(Mat legendMat) {
        String legend = imageUtils.ocrOnImage(legendMat, 2);
        System.out.print(legend);
        return legend;
    }
}
