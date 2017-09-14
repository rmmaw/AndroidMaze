package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;

/**
 * Basic class used to hold wall coordinates for Prims Maze Generation and for the logging mechanism.
 */
public class Wall implements Serializable{
    public int x;
    public int y;
    public int dx;
    public int dy;
    /**
     * Constructor
     */
    public Wall(int xx, int yy, int dxx, int dyy)
    {
        x=xx;
        y=yy;
        dx=dxx;
        dy=dyy;
    }

}