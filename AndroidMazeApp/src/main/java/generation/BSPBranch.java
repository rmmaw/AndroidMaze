package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;



public class BSPBranch extends BSPNode implements Serializable{
    // left and right branches of the binary tree
    public BSPNode lbranch, rbranch;
    // (x,y) coordinates and (dx,dy) direction
    public int x, y, dx, dy;

    /**
     * Constructor with values for all internal fields
     *
     */
    public BSPBranch(int px, int py, int pdx, int pdy, BSPNode l, BSPNode r) {
        lbranch = l;
        rbranch = r;
        isleaf = false;
        x = px;
        y = py;
        dx = pdx;
        dy = pdy;
        xl = Math.min(l.xl, r.xl);
        xu = Math.max(l.xu, r.xu);
        yl = Math.min(l.yl, r.yl);
        yu = Math.max(l.yu, r.yu);
    }

   /** public BSPNode getLeftBranch(){
        return lbranch;
    }

    public BSPNode getRightBranch(){
        return rbranch;
    }
*/

}