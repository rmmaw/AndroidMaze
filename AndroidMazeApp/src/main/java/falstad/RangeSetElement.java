package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;


public class RangeSetElement implements Serializable{
    public int min, max;

    /**
     * Constructor
     * @param mn
     * @param mx
     */
    RangeSetElement(int mn, int mx) {
        min = mn;
        max = mx;
    }
}

