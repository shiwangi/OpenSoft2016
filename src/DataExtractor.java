import javafx.util.Pair;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.imread;
public class DataExtractor {

    //public PdfCreator create = new PdfCreator("./output/graphValues.pdf");
    static ImageUtils imageUtils;

    PDFSample pdfSample = new PDFSample();

    static PdfToImage pdfToImage;
    static RectangleDetection rectangleDetection;
    static String RPATH = "./resources";
    static ArrayList<String> imageFileList;
    public JFrame plotJframe;
    public List<Table> tableList;
    DataExtractor(){
        tableList = new ArrayList<>();
    }

    /**
     * returns the arraylist of filepaths to the graph images extracted.
     *
     * @param name The filpath of the input pdf
     * @return
     */
    public ArrayList<String> getGraphImages(String name) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        System.loadLibrary("opencv_core");
//        System.loadLibrary("opencv_java300");
        pdfToImage = new PdfToImage(name);
        imageFileList = pdfToImage.convert();
        return imageFileList;
    }


    /**
     * returns the basic graphdata given a imagefilepath.
     *
     * @param fname the filepath to the input graphImage.
     * @return
     */
    public GraphData extractDataForImage(String fname) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        fname = RPATH + fname;
        imageUtils = new ImageUtils();

        rectangleDetection = new RectangleDetection();

        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return null;
        }

        //trim whitespaces
        mRgba = imageUtils.getCroppedImage((mRgba));

        boolean hasScalesInBox = false;

        List<MatOfPoint> largeContours = pdfToImage.getLargeContours(imageUtils.convertToBinary(mRgba, 0), mRgba, 0, false);
        if (rectangleDetection.getSquareContours(largeContours) == null) {
            hasScalesInBox = true;
        }

        //clipping for Scales and Plots
        ImageClipper imageClipper = new ImageClipper(mRgba);
        List<Mat> images = imageClipper.clipContour(mRgba, largeContours.get(0), hasScalesInBox);
        Mat XscaleImage = images.get(2),
                YscaleImage = images.get(1),
                graphImage = images.get(0),
                captionImage = images.get(3);


        List<Double> minmaxValues = null;
        AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
        List<String> labels = axisDetection.getAxis();
        minmaxValues = axisDetection.getMinMaxValues(labels);
        System.out.println(minmaxValues.toString());
        System.out.println(labels.toString());
        String captionLabel = imageUtils.ocrOnImage(captionImage, 2);
        labels.add(captionLabel);


        GraphData graphData = new GraphData(minmaxValues, labels, graphImage);
        return graphData;
    }

    /**
     * Finds all the different colored plots in the image,corresponding datapoints and stores in Output.pdf
     * @param graphData The input graphdata object
     */
    void getPlotsAndLegend(GraphData graphData) {
        //Legend Detection

        Mat graphImage = graphData.ScaleMat;
        String xScaleLabel = graphData.xLabel;
        String yScaleLabel = graphData.yLabel;
        String captionLabel = graphData.caption;
        ArrayList<Double> minmaxValues = (ArrayList<Double>) graphData.minmaxValues;
        graphImage = imageUtils.increaseSaturation(graphImage);
        LegendDetection legendDetection = new LegendDetection(graphImage);

        Mat legendMat = null;
        List<Mat> legendAndPlot;


        ImageClipper imageClipper = new ImageClipper(graphImage);


        MatOfPoint contour = rectangleDetection.detectRectangle(graphImage, imageUtils.convertToBinary(graphImage, 255));
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
            legendAndPlot = imageClipper.clipContourM(graphImage, contour);
            if (legendAndPlot.size() == 2) {
                legendMat = legendAndPlot.get(0);
                graphImage = legendAndPlot.get(1);

                //imageUtils.displayImage(legendMat);
                String[] label = legendDetection.detectLegend(legendMat).split("\n");

                System.out.println(label);
                PlotValue plotValue = new PlotValue(graphImage, minmaxValues);

                writeToTable(legendDetection, legendMat, plotValue, label);




            }

        } else {
            //so we ll image-match :)
            System.out.println("Could not find scale box ");
            //imageUtils.displayImage(graphImage);
            PlotValue plotValue = new PlotValue(graphImage, minmaxValues);
            //List<Colour> colourListFromPlot = new ArrayList<Colour>(plotValue.populateTable(plotJframe).getValue().keySet());
            writeToTable(null, null, plotValue, null);
            // plotJframe = plotValue.jFrame;
        }

    }

    private void writeToTable(LegendDetection legendDetection, Mat legendMat, PlotValue plotValue, String[] label) {

        Pair<List<List<String>>, Map<Colour, Boolean>> newPair = plotValue.populateTable(plotJframe);
        List<Colour> colourListFromPlot = new ArrayList<Colour>(newPair.getValue().keySet());
        plotJframe = plotValue.jframe;
        if (legendDetection != null)
            colourListFromPlot = legendDetection.getColourSequence(legendMat, colourListFromPlot);
        //remove empty lines from legend.
        List<String> fineLabels = new ArrayList<>();

        List<List<String>> content = newPair.getKey();
        if (label != null) {
            for(int i=0;i<label.length;i++)
            {
                label[i] = label[i].replaceAll("\\s+", " ").trim();
                label[i] = label[i].replaceAll("[ ]+"," ").trim();
                //System.
                if(!label[i].isEmpty()) fineLabels.add(label[i]);
            }
            for (int i = 1; i < content.get(0).size(); i++) {
                String colour = content.get(0).get(i);
                for (int j = 0; j < colourListFromPlot.size(); j++) {
                    colour = colour.replaceAll("Color", "");
                    if (colour.equals(colourListFromPlot.get(j).toString())) {
                        if(j<fineLabels.size()) content.get(0).set(i, fineLabels.get(j));
                    }
                }
            }
        } else {
            for (int i = 0; i < content.get(0).size(); i++) {
                String colour = content.get(0).get(i);
                if (colour.contains("Color")) {
                    colour = colour.replaceAll("Color", " ");
                    content.get(0).set(i, colour.toString());

                }
            }
        }
        String[][] contentArray = new String[content.size()-1][content.get(0).size()];

        List<String> head = content.get(0);
        for(int i=1;i<content.size();i++) {

            List<String> list = content.get(i);
            int sz = list.size();
            for (int j = 0; j < contentArray[0].length; j++){
                if(j>=sz){
                    contentArray[i-1][j]="";
                }
                else{
                    contentArray[i-1][j]=list.get(j);
                }

        }

        }

        if(head.size()>0 && contentArray.length>0)
        tableList.add(pdfSample.createContent(head, contentArray));


    }

}
