package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;


public class FloatPair implements Serializable{
    public double p1, p2;

    /**
     * Constructor
     * @param pp1
     * @param pp2
     */
    FloatPair(double pp1, double pp2) {
        p1 = pp1;
        p2 = pp2;
    }
}