import org.junit.Test;

import static org.junit.Assert.*;

public class ColourOrderTest {

    @Test
    public void testCompareTo() throws Exception {
    Colour c1 = new Colour(100,200,100);
    Colour c2 = new Colour(107,200,100);
    double d1=130 ,d2 =140 ;
            ColourOrder q1,q2;
            q1=new ColourOrder(c1,d1);
            q2=new ColourOrder(c2,d2);
           int t = q1.compareTo(q2);
    assertTrue(t==(d1-d2));
    }
}