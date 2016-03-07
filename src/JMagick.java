import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;


/**
 * Created by shiwangi on 6/3/16.
 */
public class JMagick {
    public void convert() {
    //    System.out.p
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
//        Mat img = imread(fName);
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(img, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        ImageUtils imageUtils = new ImageUtils();
//        imageUtils.drawContoursOnImage(contours,img);

    }
}
