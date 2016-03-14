/**
 * This class is used in ordering of colors found in legend with the colors returned from color segmentation
 */
public class ColourOrder implements Comparable<ColourOrder>{
    Colour colour;
    double dist;
    ColourOrder(Colour colour,double dist){
        this.colour = colour;
        this.dist = dist;
    }

    @Override
    public int compareTo(ColourOrder o)
    {
        return (int) (dist-o.dist);
    }
}
