import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
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
               // imageinfo.setDepth(50);
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

            System.out.print("done");
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

        Mat binary = imageUtils.convertToBinary(img,255);
        binary = imageUtils.cleanborders(binary);
        imwrite("./resources/binary" + i + ".png", binary);

        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        RectangleDetection rectangleDetection = new RectangleDetection();
        List <MatOfPoint> largeones = new ArrayList<>();
        int j=0;
        for(MatOfPoint contour:contours){
            double area = Imgproc.contourArea(contour);
            Mat mask = new Mat(img.rows(), img.cols(), CvType.CV_8UC1);
            if(area>10000)
            {
                j++;

                    Rect rect = Imgproc.boundingRect(contour);
                    Mat roi = new Mat();
                    if (rect.height >300) {
                        int offsetX =  rect.width/4;
                        int offsetY=  rect.height/4;
                        int x = max(rect.x -offsetX, 0);
                        int y = max(rect.y - offsetY, 0);
                        int xl = min(img.cols(),rect.x+rect.width+20);
                        int yl = min(img.rows(),rect.y+rect.height+offsetY);

                        //rectangle(img, new Point(rect.x, rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 0, 255));
                        roi = img.submat(y, yl, x, xl);
                        imwrite("./resources/roi" + i*10+j + ".png", roi);

                    }
              //  }
                largeones.add(contour);
            }
           // System.out.print(area+"\n");
        }

//        imageUtils.drawContoursOnImage(largeones, img);
//        imwrite("./resources/contour_large"+i+".png",img);

    }
}
