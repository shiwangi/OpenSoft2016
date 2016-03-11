import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;


/**
 * Created by shiwangi on 3/3/16.
 */
public class PlotValue {

    public static double rangeY;
    public static double rangeX;
    public static int dx, dy;
    public ImageUtils imageUtils;

    Mat graph;
    double minX, minY;
    Map<Colour, Boolean> colourOfPlotsHSV;

    //public PdfCreator create = new PdfCreator("./output/graphValues.pdf");
    PlotValue(Mat graph, double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.minY = minY;
        rangeX = maxX - minX;
        rangeY = maxY - minY;
        imageUtils = new ImageUtils();
        this.graph =(graph);
        int xPixels = graph.cols();
        int yPixels = graph.rows();
        dx = (int) ((rangeX / 100.0) * (xPixels / rangeX));
        dy = (int) ((rangeY / 100.0) * (yPixels / rangeY));
        dx = (dx == 0) ? 1 : dx;
        dy = (dy == 0) ? 1 : dy;

        colourOfPlotsHSV = new TreeMap();
    }



    public Map populateTable() {

        //   graph = imageUtils.equalizeIntensity(graph);
        //imageUtils.displayImage(graph);

        int flag = 1;
        int i = 100+dx;
    //    dx = 1;
        Mat hsvImage = graph.clone();
      //  imageUtils.displayImage(graph);
        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV, 3);
        //find first color Pixel
        for (int j = 0; j < graph.rows(); j++) {
            double[] colorHSV = hsvImage.get(j, i);
            double[] color = graph.get(j, i);
            Colour colour = new Colour(colorHSV[0], colorHSV[1], colorHSV[2]);

            if (imageUtils.isPixelBlack(color)) {
                continue;
            }
            if (imageUtils.isPixelWhite(color)) {
                flag = 1;
                continue;
            } else if (flag == 1 && (!colourOfPlotsHSV.containsKey(colour))) {
                findGraphValues(colour);
                flag = 0;
                colourOfPlotsHSV.put(colour, true);
            }
        }
        return colourOfPlotsHSV;
    }


    private void findGraphValues(Colour colour) {

        Mat hsvImage = graph.clone();
        //Content for PDF
        List<List<String>> content = new ArrayList<>();
        List<String> heading = new ArrayList<>();
        heading.add("Color- " + colour.r + " " + colour.g + " " + colour.b);
        heading.add("X-Y values");//heading
        content.add(heading);


        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV, 3);
        System.out.println("Plot Points For Color " + colour.r + " " + colour.g + " " + colour.b);
        Mat img = graph.clone();
        for (int i = dx; i < graph.cols(); i += dx) {

            Point point = new Point(0, i);
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < graph.rows(); j += dy) {

                double[] colourCompare = hsvImage.get(j, i);
                Colour colour2 = new Colour(colourCompare[0], colourCompare[1], colourCompare[2]);
                double distance = Colour.dist(colour, colour2);
                if (distance < minDist) {
                    minDist = distance;
                    point = new Point(j, i);
                }
            }
            if (minDist < 20) {
                double[] newC = {0, 0, 0};
                circle(img, new Point(i, point.x), 10, new Scalar(0, 0, 0),-1);
                img.put((int) point.x, i, newC);
                List<String> element = new ArrayList<>();
                element.add(String.valueOf(minX + point.x * rangeX / graph.cols()));
                element.add(String.valueOf(minY + point.y * rangeY / graph.rows()));
                content.add(element);

                //  System.out.println(point.x + "\t" + point.y);
            } else {
                List<String> element = new ArrayList<>();
                element.add(String.valueOf(minX + point.x * rangeX / graph.cols()));
                element.add("-");
                content.add(element);
            }
        }
        imageUtils.displayImage(img);
//        try {
//            create.drawpdf(content);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }

        //imageUtils.displayImage(img);
    }

    private boolean isValidPixel(Mat hsvImage, int j, int i) {
        int startx = max(0, j - 10);
        int starty  = max(0,i-10);
        int endx = min(hsvImage.cols(),j+10);
        int endy  = min(hsvImage.rows(),i+10);
        int count= 0;
        Colour C1 = new Colour(hsvImage.get(j,i)[0],hsvImage.get(j,i)[1],hsvImage.get(j,i)[2]);
        for(int p = startx;p<endx;p++)
        {
            for(int q = starty;q<endy;q++)
            {
                Colour C2 = new Colour(hsvImage.get(q,p)[0],hsvImage.get(q,p)[1],hsvImage.get(q,p)[2]);
                if(C1.compareTo(C2)<10) count++;
                if(count>25) return true;
            }
        }
        return false;

    }

}

