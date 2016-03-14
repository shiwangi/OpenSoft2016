import org.opencv.core.Mat;

import java.util.List;


/**
 * This Class bundles the necessary information to give to the UI classes
 */
public class GraphData {
    String xLabel,yLabel,caption;
    List<Double> minmaxValues;
    List<Mat> plotImages;
    Mat ScaleMat,xScale,legend;

    public GraphData(List<Double> minmaxValues, List<String> labels,Mat graphImage) {
        this.minmaxValues = minmaxValues;
        this.xLabel = labels.get(1);
        this.yLabel = labels.get(3);
        this.caption = labels.get(4);
        this.ScaleMat = graphImage;
    }
}
