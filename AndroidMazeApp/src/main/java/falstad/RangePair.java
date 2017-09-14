package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;


public class RangePair implements Serializable{
    public int x1, z1, x2, z2;
    RangePair(int xx1, int zz1, int xx2, int zz2) {
        x1 = xx1;
        z1 = zz1;
        x2 = xx2;
        z2 = zz2;
    }
}