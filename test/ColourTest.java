import org.junit.Test;

import static org.junit.Assert.*;

public class ColourTest {


    @Test
    public void testCompareTo() throws Exception {
         Colour c = new Colour(100,200,150);
        Colour c2 = new Colour(106,200,150);
  int t1 = c.compareTo(c2);
   assertTrue(t1==0);
        Colour c1 = new Colour(130,200,150);
        t1 = c.compareTo(c1);
        assertFalse(t1==0);


    }

    @Test
    public void testDist() throws Exception {
        Colour c = new Colour(100,200,150);
        Colour c2 = new Colour(106,200,150);
        double t1=Colour.dist(c,c2);
        assertTrue(t1 == 6);
        Colour c1 = new Colour(106,200,150);
         t1=Colour.dist(c1,c2);
        assertTrue(t1 == 0);

    }
}