import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;


/**
 * Created by shiwangi on 6/3/16.
 */
public class JMagick {
    static final int TESTING = 0;

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
                //    m.reduceNoiseImage(30);
m.enhanceImage();
                    m.setXResolution(20);
                    m.setYResolution(20);
                    System.out.println(m.getColors());
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

        Mat binary = imageUtils.convertToBinary(img,0);
        binary = imageUtils.cleanborders(binary);
        imwrite("./resources/binary" + i + ".png", binary);
getLargeContours(binary,img,i, true);

//        imageUtils.drawContoursOnImage(largeones, img);
//        imwrite("./resources/contour_large"+i+".png",img);

    }

    public List<MatOfPoint> getLargeContours(Mat binary, Mat img, int i, boolean isROI) {

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        List <MatOfPoint> largeones = new ArrayList<>();
        int j=0;
        for(MatOfPoint contour:contours){
            double area = Imgproc.contourArea(contour);
            if(area>10000)
            {
                Rect rect = Imgproc.boundingRect(contour);
                if(rect.height>300 &&rect.width>300) {
                    j++;
                    if (isROI == true) {
                        createRoi(contour, img, i, j,rect);
                    }
                    //  }
                    largeones.add(contour);
                }
            }
            // System.out.print(area+"\n");
        }
        return largeones;
    }

    private void createRoi(MatOfPoint contour, Mat img,int i,int j,Rect rect) {

        Mat roi = new Mat();
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
}
