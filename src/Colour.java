import static java.lang.Math.sqrt;

/**
 * The class for HSV colorspace pixels.Used for mainly olor segmentation.
 */
public class Colour implements Comparable<Colour>{
    double h, s,v;
    Colour(double h, double s, double v){
        this.h = h;
        this.s = s;
        this.v = v;
    }

    @Override
    public int compareTo(Colour o) {
        if(Math.abs(o.h - h)<=20 )
        return 0;
        else return (int) (o.h - h);
    }

    @Override
    public String toString()
    {
        return (String.valueOf(h)+" "+String.valueOf(s)+" "+String.valueOf(v));
    }
    public static double dist(Colour colour, Colour colour2)
    {
        return Math.abs(colour.h -colour2.h) ;
    }
}
