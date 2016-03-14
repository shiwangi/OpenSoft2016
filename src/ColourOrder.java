
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
