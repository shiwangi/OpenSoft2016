import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by rajitha on 11/3/16.
 */
public class GraphData {
    String xLabel,yLabel,caption;
    List<Double> minmaxValues;
    List<Mat> plotImages;
    Mat ScaleMat,xScale,legend;

    public GraphData(List<Double> minmaxValues, List<String> labels) {
        this.minmaxValues = minmaxValues;
        this.xLabel = labels.get(1);
        this.yLabel = labels.get(3);
        this.caption = labels.get(4);
    }
}
