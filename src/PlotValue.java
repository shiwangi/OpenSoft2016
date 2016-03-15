import javafx.util.Pair;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.util.*;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;


/**
 * The class which takes care of identifying the different colors and getting the datapoints.
 */
public class PlotValue {

    public static double rangeY;
    public static double rangeX;
    public static int dx, dy;
    public double rValue;
    public ImageUtils imageUtils;
    List<List<String>> content = new ArrayList<>();

    Mat graph;
    double minX, minY;
    Map<Colour, Boolean> colourOfPlotsHSV;
    public JFrame jframe;


    PlotValue(Mat graph, List<Double> minmaxValues) {
        this.minX = minmaxValues.get(0);
        this.minY = minmaxValues.get(2);
        this.rValue = minmaxValues.get(4);
        rangeX = minmaxValues.get(1) - minX;
        rangeY = minmaxValues.get(3) - minY;
        imageUtils = new ImageUtils();
        this.graph =(graph);
        int xPixels = graph.cols();
        int yPixels = graph.rows();
        dx = (int)((xPixels*rValue) / (rangeX*10));
        dy = (int)((yPixels*rValue) / (rangeY*10));
        dx = (dx == 0) ? 1 : dx;
        dy = (dy == 0) ? 1 : dy;

        colourOfPlotsHSV = new TreeMap();

    }


    /**
     * Identifies the different colors and calls the findgraphvaues method for each color.
     * returns the datapoints found and the colors.
     * @return
     * @param plotJframe
     */
    public Pair<List<List<String>>, Map<Colour,Boolean>> populateTable(JFrame plotJframe) {
        List<String> heading = new ArrayList<>();
        heading.add("Plot for blah");
        //heading.add("X-Y values");//heading
        content.add(heading);
        for(int i =0;i<graph.cols();i+=dx)
        {
            List<String> temp = new ArrayList<>();
            temp.add(String.valueOf(minX + (i * rangeX) / graph.cols()));
            content.add(temp);
        }



        int flag = 1;
        //int i = 100+dx;
        Mat hsvImage = graph.clone();
        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV, 3);

        List<Integer> checkPoint = new ArrayList<>();
        //checkPoint.add(dx,100,);
        for(int i =dx;i<graph.cols();i+=10*dx)
        {
            checkPoint.add(i);
        }
        //find first color Pixel
        List<Mat> listOfMats = new ArrayList<>();
        for(int i :checkPoint)
        {
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
                } else if (flag == 1 && !colourOfPlotsHSV.containsKey(colour)){
                    listOfMats.add(findGraphValues(colour));
                    flag = 0;
                    colourOfPlotsHSV.put(colour, true);
                }
            }
        }

        int i =0;
        ArrayList<String> listofFilepath = new ArrayList<>();
        for(Mat img : listOfMats)
        {
            i++;
            imwrite("./resources/plot"+i+".png",img);
            listofFilepath.add("/plot"+i+".png");
        }
        ImageGrid imageGrid = new ImageGrid(listofFilepath);
        jframe = imageGrid.createAndShowGui(listofFilepath,plotJframe);




        Pair<List<List<String>>,Map<Colour, Boolean>> returnpair = new Pair<>(content,colourOfPlotsHSV);

        return returnpair;
    }

    private boolean hasSimilarColor(Colour colour) {
        if(colourOfPlotsHSV.containsKey(colour)) return true;
        Colour c ;
        Iterator<Colour> iterator = colourOfPlotsHSV.keySet().iterator();
        while(iterator.hasNext())
        {
            c = iterator.next();
            if(c.compareTo(colour)==0) return true;
        }
        return false;
    }


    /**
     * Finds the datapoints corresponding to a particuar color in the graph.
     * @param colour
     * @return
     */
    private Mat findGraphValues(Colour colour) {

        Mat hsvImage = graph.clone();
        //Content for PDF
        List<String> heading = new ArrayList<>();
        cvtColor(graph, hsvImage, Imgproc.COLOR_RGB2HSV, 3);
        System.out.println("Plot Points For Color " + colour.h + " " + colour.s + " " + colour.v);
        Mat img = graph.clone();
        int k = 0;
        content.get(0).add(colour.h+" "+colour.s+" "+colour.v);
        for (int i = 0; i < graph.cols(); i += dx) {
            k++;
            Point point = new Point(0, i);
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < graph.rows(); j++) {

                double[] colourCompare = hsvImage.get(j, i);
                Colour colour2 = new Colour(colourCompare[0], colourCompare[1], colourCompare[2]);
                double distance = Colour.dist(colour, colour2);
                if (distance < minDist) {
                    minDist = distance;
                    point = new Point(j, i);
                }
            }
            if (minDist < 20 && point.x!=0) {
                double[] newC = {0, 0, 0};
                circle(img, new Point(i, point.x), 10, new Scalar(0, 0, 0),-1);
                img.put((int) point.x, i, newC);
                content.get(k).add(String.valueOf(minY + point.x * rangeY / graph.rows()));
            } else {
                content.get(k).add(String.valueOf(minY + point.x * rangeY / graph.rows()));
            }
        }
        //imageUtils.displayImage(img);
        return img;
    }


}

