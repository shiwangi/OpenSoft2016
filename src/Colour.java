/**
 * Created by shiwangi on 3/3/16.
 */
public class Colour implements Comparable<Colour>{
    double r,g,b;
    Colour(double r,double g,double b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public int compareTo(Colour o) {
        if(Math.abs(o.r-r)<10 )
        return 0;
        else return 1;
    }

    public static double dist(Colour colour, Colour colour2) {
        return Math.abs(colour.r-colour2.r) ;
    }
}
