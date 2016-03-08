import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.rectangle;


/**
 * Created by shiwangi on 6/3/16.
 */
public class JMagick {
    static final int TESTING = 1;

    public void convert() {
        String p_inFile = "./resources/SCANNEDOPactual.pdf";
        String p_outFile = "./resources/images";

        try {
            if (TESTING == 0) {
                ImageInfo imageinfo = null;
                imageinfo = new ImageInfo(p_inFile);
                //  imageinfo.setCompression(CompressionType.NoCompression);
                imageinfo.setDensity("400");
                imageinfo.setDepth(100);
                MagickImage mainImage = new MagickImage(imageinfo);

                MagickImage[] subImages = mainImage.breakFrames();
                int i = 0;
                for (MagickImage m : subImages) {
                    String fName = p_outFile + i + ".png";
                    m.setFileName(fName);
                    System.out.println(i);
                    i++;
                    m.writeImage(imageinfo);
                }
            }
            for (int i = 1; i < 5; i++) {
                String fName = p_outFile + i + ".png";

                performImageMatching(fName, i);
            }
            //  }

//        MagickImage image = new MagickImage(info);
//        image.setFileName(p_outFile);
//        image.setCompression(CompressionType.FaxCompression);
//        image.writeImage(info);
        } catch (MagickException e) {
            e.printStackTrace();
        }


    }

    private void performImageMatching(String fName, int i) {

        ImageUtils imageUtils = new ImageUtils();
        Mat img = imread(fName);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat binary = imageUtils.convertToBinary(img);
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
      //  RectangleDetection rectangleDetection = new RectangleDetection();
        Mat roi = null;
        for (int idx = 0; idx < contours.size(); idx++) {
            if(contours.size()<1000){
                continue;
            }
            Rect rect = Imgproc.boundingRect(contours.get(idx));
            if (rect.height >300) {

                rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));
                roi = img.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);
                imwrite("./resources/roi" + i*10+idx + ".png", roi);

            }
            //to get my target contour

            //get Square Contours
//        List<MatOfPoint> squareContours = rectangleDetection.getSquareContours(contours);
//       // List<MatOfPoint> smallContours = rectangleDetection.getSmallCountours(contours);
//        imageUtils.drawContoursOnImage(contours, img);
//       // imageUtils.displayImage(img);
//        for(MatOfPoint contour:squareContours){
//
//        }


        }
    }
}
