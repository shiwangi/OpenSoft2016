import org.opencv.core.Mat;

/**
 * Created by shiwangi on 3/3/16.
 */
public class PlotValue {

    private static int rangeY,dy;
    private static int rangeX,dx;
    public ImageUtils imageUtils;
    Mat graph;
    PlotValue(Mat graph,int minX,int maxX,int minY,int maxY){
        rangeX = maxX-minX;
        this.graph = graph;
        rangeY = maxY-minY;
        imageUtils = new ImageUtils();
        int xPixels = graph.cols();
        int yPixels = graph.rows();
        dx = (int) (rangeX/100.0 * xPixels);
        dy = (int) (rangeY/100.0 * yPixels);
        dx = (dx==0)?1:dx;
        dy =(dy==0)?1:dy;
    }

    public void populateTable() {

        imageUtils.displayImage(graph);

    }
}
