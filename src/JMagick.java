import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;


/**
 * Created by shiwangi on 6/3/16.
 */
public class JMagick {
    public void convert() {
        String p_inFile = "/home/shiwangi/Downloads/SCANNEDOPactual.pdf";
        String p_outFile = "./resources/images";
        System.out.print(java.io.File.pathSeparatorChar);
        ImageInfo imageinfo = null;
        try {

            imageinfo = new ImageInfo(p_inFile);
        //    imageinfo.setCompression(CompressionType.LosslessJPEGCompression);
            imageinfo.setCompression(CompressionType.NoCompression);
//            System.out.println(imageinfo.getDepth());
            imageinfo.setDensity("100");
            imageinfo.setDepth(100);
            MagickImage mainImage = new MagickImage(imageinfo);
          //  mainImage.scaleImage(600, 1000);

           MagickImage[] subImages = mainImage.breakFrames();
            int i=0;
           for(MagickImage m:subImages) {
                String fName = p_outFile + i + ".png";
               m.setFileName(fName);
               i++;
               System.out.println(imageinfo.getQuality());
               m.writeImage(imageinfo);

               performImageMatching(fName);
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

    private void performImageMatching(String fName) {

        ImageUtils imageUtils = new ImageUtils();
        Mat img = imread(fName);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat binary = imageUtils.convertToBinary(img);
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        imageUtils.drawContoursOnImage(contours,img);
        imageUtils.displayImage(img);

    }
}
