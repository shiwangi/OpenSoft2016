import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import sun.misc.Launcher;

import java.net.URL;
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
    static ImageUtils imageUtils;
    int countInvalid=0;
    static URL url = Launcher.class.getResource("/" + "resources");
    static String RPATH = url.getPath();
//    static String RPATH = null;
    public void convert() {

        String p_inFile = RPATH+"/SCANNEDOPactual.pdf";
        String p_outFile = RPATH +"/images";

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
                    String fName = p_outFile + i + ".jpg";
                    //    m.reduceNoiseImage(30);
                    m.setXResolution(20);
                    m.setYResolution(20);

                    m.setFileName(fName);
                    System.out.println(i);
                    i++;
                    m.writeImage(imageinfo);
                }
            }
            for (int i = 0; i < 5; i++) {
                String fName = p_outFile + i + ".jpg";
                Mat img = imread(fName);
                countInvalid=0;
                performImageMatching(img, i);
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

    private void performImageMatching(Mat img, int i) {

        imageUtils = new ImageUtils();

        if(img.rows()==0 && img.cols()==0){
            return;
        }
        Mat binary = imageUtils.convertToBinary(img, 0);
        binary = imageUtils.cleanborders(binary);
       // imageUtils.displayImage(binary);
        getLargeContours(binary, img, i, true);

//        imageUtils.drawContoursOnImage(largeones, img);
//        imwrite("./resources/contour_large"+i+".png",img);

    }

    public List<MatOfPoint> getLargeContours(Mat binary, Mat img, int i, boolean isROI) {

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> largeones = new ArrayList<>();
        int j = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > 10000) {
                Rect rect = Imgproc.boundingRect(contour);
                if (rect.height > 300 && rect.width > 300) {
                    j++;
                    if (isROI == true) {
                        createRoi(contour, img, i, j, rect);
                    }
                    //  }
                    largeones.add(contour);
                }
            }
            // System.out.print(area+"\n");
        }

        //imageUtils.drawContoursOnImage(largeones,img);
       // imageUtils.displayImage(img);
        return largeones;
    }

    private void createRoi(MatOfPoint contour, Mat img, int i, int j, Rect rect) {

        Mat roi = new Mat();
        int offsetX = rect.width / 4;
        int offsetY = rect.height / 4;
        int x = max(rect.x - offsetX, 0);
        int y = max(rect.y - offsetY, 0);
        int xl = min(img.cols(), rect.x + rect.width + 20);
        int yl = min(img.rows(), rect.y + rect.height + offsetY);

        //rectangle(img, new Point(rect.x, rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 0, 255));
        roi = img.submat(y, yl, x, xl);
   //     imageUtils.displayImage(roi);
        Mat cropped = imageUtils.getCroppedImage(roi);
    //    imageUtils.displayImage(cropped);
        if (isGraphImageValid(cropped, rect)) {
            String path="/roi" + i * 10 + j + ".png";

            imwrite(RPATH+path, roi);
            return;
        } else {
            countInvalid++;

                //
                // imageUtils.displayImage(roi);
            if(countInvalid<4) {
                roi = img.submat(rect.y+10, rect.y+rect.height-10, rect.x+10, rect.x+rect.width-10);
                performImageMatching(roi, i);
            }
            else{
               // imwrite("./resources/roi" + i * 10 + j + ".png", roi);
                System.out.println("Image invalid !");
            }
        }


    }

    private static boolean isGraphImageValid(Mat mat, Rect img) {
        //If the Mat has two vertical lines parallel to each other, it is not a graph?

        if ((img.height + 100) >( mat.height() ) && (img.width + 100)> (mat.width()))
            return false;
        return true;

    }
}
