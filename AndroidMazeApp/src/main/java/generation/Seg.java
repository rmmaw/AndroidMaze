package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazePanel;


public class Seg implements Serializable{
    public int x, y, dx, dy, dist;
    public String col;
    public int val1;
    public boolean partition, seen;
    public MazePanel panel;

    /**
     * Constructor

     */
    public Seg(int psx, int psy, int pdx, int pdy, int cl, int cc) {
        x = psx;
        y = psy;
        dx = pdx;
        dy = pdy;
        dist = cl;
        seen = false;
        cl /= 4;
        //this.panel = panel;
        int add = (dx != 0) ? 1 : 0;
        int part1 = cl & 7;
        int part2 = ((cl >> 3) ^ cc) % 6;
        val1 = ((part1 + 2 + add) * 70)/8 + 80;
        switch (part2) {
            case 0: col = "ColorOne"; break;
            case 1: col = "ColorTwo"; break;
            case 2: col = "ColorThree"; break;
            case 3: col = "ColorFour"; break;
            case 4: col = "ColorFive"; break;
            case 5: col = "ColorSix"; break;
            default: col = "ColorSeven"; break;
        }
    }

    int getDir() {
        if (dx != 0)
            return (dx < 0) ? 1 : -1;
        return (dy < 0) ? 2 : -2;
    }




}