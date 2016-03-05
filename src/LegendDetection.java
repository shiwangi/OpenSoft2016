import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by shiwangi on 5/3/16.
 */
public class LegendDetection {
    Mat graphImage;
    Mat binaryGraphImage;
    ImageUtils imageUtils;
    List<Colour> colourOfPlotsHSV;

    public LegendDetection(Mat graphImage, List<Colour> colourOfPlotsHSV) {
        this.graphImage = graphImage;
        imageUtils = new ImageUtils();
        this.colourOfPlotsHSV = colourOfPlotsHSV;
    }

    public String detectLegend() {
        Mat cleanlegend = imageUtils.removecolorpixels(graphImage);
         imageUtils.displayImage(cleanlegend);
        cleanlegend = imageUtils.convertToBinary(cleanlegend);
        imageUtils.displayImage(cleanlegend);

        String legend = imageUtils.ocrOnImage((cleanlegend),2);

        imageUtils.displayImage(graphImage);
        return legend;
    }
}
