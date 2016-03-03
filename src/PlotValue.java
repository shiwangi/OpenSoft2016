import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by shiwangi on 3/3/16.
 */
public class PlotValue {

    public static int rangeY,dy;
    public static int rangeX,dx;
    public ImageUtils imageUtils;
    Mat graph;
    PlotValue(Mat graph,int minX,int maxX,int minY,int maxY){
        rangeX = maxX-minX;
        this.graph = graph;
        rangeY = maxY-minY;
        imageUtils = new ImageUtils();
        int xPixels = graph.cols();
        int yPixels = graph.rows();
        dx = (int) ((rangeX/100.0) * (xPixels/rangeX));
        dy = (int) ((rangeY/100.0) * (yPixels/rangeY));
        dx = (dx==0)?1:dx;
        dy =(dy==0)?1:dy;
    }

    public void populateTable() {
        int flag=1;
       int i=2*dx;

        Mat hsvImage = graph.clone();
        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV,3);
           //find first color Pixel
           for(int j=0;j<graph.rows();j++) {
               double[] color = graph.get(j,i);
               if(imageUtils.isPixelBlack(color)){
                   continue;
               }
               if(imageUtils.isPixelWhite(color)) {
                   flag=1;
               }
               else if(flag==1) {
                   color = hsvImage.get(j,i);
                   Colour colour = new Colour(color[0],color[1],color[2]);
                   findGraphValues(colour);
                   flag=0;
               }
           }
       }

    private void findGraphValues(Colour colour) {

        Mat hsvImage = graph.clone();
        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV,3);
        System.out.println("For Color " + colour.r + " " + colour.g + " " + colour.b);
        Mat img = graph.clone();
        for(int i=dx;i<graph.cols();i+=dx) {

            Point point = new Point(0,i);
            double minDist = Double.MAX_VALUE;

            for(int j=0;j<graph.rows();j+=dy) {

                double[] colourCompare = hsvImage.get(j,i);
                Colour colour2 = new Colour(colourCompare[0],colourCompare[1],colourCompare[2]);
                double distance = Colour.dist(colour, colour2);
                if(distance<minDist) {
                    minDist = distance;
                    point = new Point(j,i);
                }
            }
            if(minDist<20) {
                double[] newC = {0, 0, 0};
                circle(img, new Point(i, point.x), 5, new Scalar(0, 0, 0));
                img.put((int) point.x, i, newC);
                System.out.println(point.x + "\t" + point.y);
            }
        }
        imageUtils.displayImage(img);
    }




    //  imageUtils.displayImage(newG);


    }

