package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;
import java.util.Vector;


public class RangeSet implements Serializable{
    private Vector<RangeSetElement> ranges;

    /**
     * Constructor
     */
    RangeSet() {
        ranges = new Vector<RangeSetElement>();
    }

    /**
     * Tells if the set is empty.
     */
    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    /**
     * Clears the set and fills it with a single new element as specified
     */
    public void set(int mn, int mx) {
        ranges.removeAllElements();
        ranges.addElement(new RangeSetElement(mn, mx));
    }

    /**
     * Removes interval [fx,tx] from existing set such that none of its elements intersects with it anymore.
     * Existing intervals are reduced if they intersect, split into two or fully removed if they are contained in [fx,tx]
     */
    public void remove(int fx, int tx) {
        // make sure fx <= tx
        if (tx < fx) {
            int jj = tx;
            tx = fx;
            fx = jj;
        }
        for (int i = 0; i != ranges.size(); i++) {
            RangeSetElement rse =
                    (RangeSetElement) ranges.elementAt(i);
            if (rse.max < fx)
                continue;
            if (rse.min > tx)
                return;
            if (fx <= rse.min) {
                if (rse.max <= tx) {
                    ranges.removeElementAt(i--);
                    continue;
                }
                rse.min = tx+1;
                return;
            }
            if (fx <= rse.max && tx >= rse.max) { // rse.min < fx <= rse.max <= tx
                rse.max = fx-1;
                continue;
            }

            RangeSetElement nrse = new RangeSetElement(rse.min, fx-1);
            ranges.insertElementAt(nrse, i);
            rse.min = tx+1;
            return;
        }
    }
    /**
     * Tells if there is at least one interval that intersects with [p.x,p.y]. It modifies attributes of p in case of an intersection.
     * A point is used as a quick hack to communicate two integers and being able to return modified values in a boolean method.
     */
    public boolean intersect(int[] interP) {


        int min = interP[0];
        int max = interP[1];

        for (int i = 0; i != ranges.size(); i++) {
            RangeSetElement rse =
                    (RangeSetElement) ranges.elementAt(i);
            if (rse.max < min)
                continue;
            if (rse.min > max)
                return false;

            if (rse.min > min)
                interP[0] = rse.min;
            if (rse.max < max)
                interP[1] = rse.max;
            return true;
        }
        return false;
    }
}