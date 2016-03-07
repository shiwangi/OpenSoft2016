import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;


/**
 * Created by shiwangi on 6/3/16.
 */
public class JMagick {
    public void convert() {
        String p_inFile = "/home/shiwangi/Downloads/SCANNEDOPactual.pdf";
        String p_outFile = "./resources/images";
        ImageInfo imageinfo = null;
        try {

            imageinfo = new ImageInfo(p_inFile);
            imageinfo.setCompression(CompressionType.LosslessJPEGCompression);
            imageinfo.setDensity("200");
//            System.out.println(imageinfo.getDepth());
            imageinfo.setDepth(1400);
            MagickImage mainImage = new MagickImage(imageinfo);
          //  mainImage.scaleImage(600, 1000);

           MagickImage[] subImages = mainImage.breakFrames();
            int i=0;
           for(MagickImage m:subImages) {

               m.setFileName(p_outFile + i + ".png");
               i++;
               System.out.println(imageinfo.getQuality());
               m.writeImage(imageinfo);
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
    }
