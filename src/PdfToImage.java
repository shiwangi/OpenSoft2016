import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public final class PdfToImage {
    static ImageUtils imageUtils = new ImageUtils();

    /**
     * This will remove all text from a PDF document.
     *
     * @param args The command line arguments.
     * @throws IOException If there is an error parsing the document.
     */
    public static void main(String[] args) throws IOException {


        PDDocument document = null;
//        try
//        {
//            document = PDDocument.load( new File("./resources/OpenSoft_Problem_February_23_2016.pdf") );
//            if( document.isEncrypted() )
//            {
//                System.err.println( "Error: Encrypted documents are not supported for this example." );
//                System.exit( 1 );
//            }
//            for( PDPage page : document.getPages() )
//            {
//                PDFStreamParser parser = new PDFStreamParser(page);
//                parser.parse();
//                List<Object> tokens = parser.getTokens();
//                List<Object> newTokens = new ArrayList<Object>();
//                for (Object token : tokens)
//                {
//                    if( token instanceof Operator)
//                    {
//                        Operator op = (Operator)token;
//                        if( op.getName().equals( "TJ") || op.getName().equals( "Tj" ))
//                        {
//                            //remove the one argument to this operator
//                            newTokens.remove( newTokens.size() -1 );
//                            continue;
//                        }
//                    }
//                    newTokens.add( token );
//                }
//                PDStream newContents = new PDStream( document );
//                OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE);
//                ContentStreamWriter writer = new ContentStreamWriter( out );
//                writer.writeTokens( newTokens );
//                out.close();
//                page.setContents( newContents );
//            }
//            document.save( "./output/output.pdf");
//        }
//        finally
//        {
//            if( document != null )
//            {
//                document.close();
//            }
//        }


        document = PDDocument.load(new File("/home/shiwangi/Downloads/SCANNEDOP.pdf"));
        PDFRenderer pren = new PDFRenderer(document);
        BufferedImage bfimg = pren.renderImageWithDPI(2, 00);
        imageUtils.displaybuffImage(bfimg);
        File outputfile = new File("./resources/image3.jpg");
        ImageIO.write(bfimg, "png", outputfile);
        String fname = "./resources/image3.jpg";


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //read the image file.
        Mat mRgba = imread(fname);
        if (mRgba.empty()) {
            System.out.println("Cannot load image!");
            return;
        }
        Mat bimage = imageUtils.convertToBinary(mRgba);
        imageUtils.displayImage(bimage);
        RectangleDetection rectangleDetection = new RectangleDetection();
        MatOfPoint contour = rectangleDetection.detectRectangle(mRgba, bimage);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        if (contour != null) {
            imageUtils.drawContoursOnImage(contours, mRgba);
            imageUtils.displayImage(mRgba);
            //List<org.opencv.core.Point> corners = getCornersFromRect(contour);
//            AxisDetection axisDetection = new AxisDetection(XscaleImage, YscaleImage);
//            List<String> labels = axisDetection.getAxis(corners, mRgba);
//
//            List<Double> minmaxValues = getminmaxValues(labels);
//            System.out.println(labels.toString());
        } else {
            System.out.println("Could not find border axes");

        }
        imageUtils.displayImage(mRgba);
        PlotValue plotValue = new PlotValue(mRgba, 0, 100, 0, 100);
        plotValue.populateTable();
    }


}