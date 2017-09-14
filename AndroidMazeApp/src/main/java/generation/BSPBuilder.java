package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;
import java.util.ArrayList;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Constants;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazeController;


public class BSPBuilder implements Serializable{
    private final int width ;
    private final int height ;
    private final MazeController maze ;
    private final Distance dists ;
    private final Cells cells ;
    private final int colchange ;

    int partiters = 0 ;
    private final int expectedPartiters ;

    /**
     * Constructor
     */
    public BSPBuilder(MazeController maze, Distance dists, Cells cells, int width, int height, int colchange, int expectedPartiters) {
        super() ;
        this.maze = maze ;
        this.dists = dists ;
        this.cells = cells ;
        this.width = width ;
        this.height = height ;
        this.colchange = colchange ;
        this.expectedPartiters = expectedPartiters ;

        partiters = 0 ;
    }

    /**
     * It generates the nodes. In every node, it has two section, left and right. It chooses the segment
     * which has the minimum grade value and then split this node into two nodes through this segment.
     * If all the segments in one node are partitioned, it will stop to split.
     */
    private BSPNode genNodes(ArrayList<Seg> sl) {

        if (countNonPartitions(sl) == 0)
            return new BSPLeaf(sl);

        Seg pe = findPartitionCandidate(sl);
        pe.partition = true;
        final int x  = pe.x;
        final int y  = pe.y;
        final int dx = pe.dx;
        final int dy = pe.dy;
        final ArrayList<Seg> lsl = new ArrayList<Seg>();
        final ArrayList<Seg> rsl = new ArrayList<Seg>();
        for (int i = 0; i != sl.size(); i++) {
            Seg se = (Seg) sl.get(i);
            int df1x = se.x - x;
            int df1y = se.y - y;
            int sendx = se.x + se.dx;
            int sendy = se.y + se.dy;
            int df2x = sendx - x;
            int df2y = sendy - y;
            int nx = dy;
            int ny = -dx;
            int dot1 = df1x * nx + df1y * ny;
            int dot2 = df2x * nx + df2y * ny;
            if (getSign(dot1) != getSign(dot2)) {
                if (dot1 == 0)
                    dot1 = dot2;
                else if (dot2 != 0) {
                    // we need to split this
                    int spx = se.x;
                    int spy = se.y;
                    if (dx == 0)
                        spx = x;
                    else
                        spy = y;
                    Seg sps1 = new Seg(se.x, se.y, spx-se.x, spy-se.y, se.dist, colchange);
                    Seg sps2 = new Seg(spx, spy, sendx-spx, sendy-spy, se.dist, colchange);
                    if (dot1 > 0) {
                        rsl.add(sps1);
                        lsl.add(sps2);
                    } else {
                        rsl.add(sps2);
                        lsl.add(sps1);
                    }
                    sps1.partition = sps2.partition = se.partition;
                    continue;
                }
            }
            if (dot1 > 0 || (dot1 == 0 && se.getDir() == pe.getDir())) {
                rsl.add(se);
                if (dot1 == 0)
                    se.partition = true;
            } else if (dot1 < 0 || (dot1 == 0 && se.getDir() == -pe.getDir())) {
                lsl.add(se);
                if (dot1 == 0)
                    se.partition = true;
            } else {
                dbg("error xx 1 "+dot1);
            }
        }
        if (lsl.size() == 0)
            return new BSPLeaf(rsl);
        if (rsl.size() == 0)
            return new BSPLeaf(lsl);
        return new BSPBranch(x, y, dx, dy, genNodes(lsl), genNodes(rsl)); // recursion on both branches
    }
    /**
     * Counts how many elements in the segment vector have their partition bit set to false
     */
    private static int countNonPartitions(ArrayList<Seg> sl) {
        int result = 0 ;
        for (int i = 0; i != sl.size(); i++)
        {
            if (!(sl.get(i)).partition)
                result++;
        }
        return result;
    }

    /**
     * It finds the segment which has the minimum grade value.
     */
    private Seg findPartitionCandidate(ArrayList<Seg> sl) {
        Seg pe = null ;
        int bestgrade = 5000; // used to compute the minimum of all observed grade values, set to some high initial value
        final int maxtries = 50; // constant, only used to determine skip
        // consider a subset of segments proportional to the number of tries, here 50, seems to randomize the access a bit
        int skip = (sl.size() / maxtries);
        if (skip == 0)
            skip = 1;
        for (int i = 0; i < sl.size(); i += skip) {
            Seg pk = (Seg) sl.get(i);
            // skip segments where the partition flag was set
            if (pk.partition)
                continue;
            // provide feedback for progress bar every 32 iterations
            partiters++;
            if ((partiters & 31) == 0) {
                updateProgressBar(partiters); // side effect: update progress bar
            }
            // check grade and keep track of minimum
            int grade = grade_partition(sl, pk);
            if (grade < bestgrade) {
                bestgrade = grade;
                pe = pk; // determine segment with smallest grade
            }
        }
        return pe;
    }

    /**
     * Push information on progress into maze such that UI can update progress bar
     */
    private void updateProgressBar(int partiters) {
        // During maze generation, the most time consuming part needs to occasionally update the current screen
        //
        if (maze.increasePercentage(partiters*100/expectedPartiters))
        {
            // give main thread a chance to process keyboard events
            try {
                Thread.currentThread().sleep(10);
            } catch (Exception e) { }
        }
    }

    /**
     * Set the partition bit to true for segments on the border and where the direction is 0
     */
    private void setPartitionBitForCertainSegments(ArrayList<Seg> sl) {
        for (int i = 0; i != sl.size(); i++) {
            Seg se = sl.get(i);
            if (((se.x == 0 || se.x == width ) && se.dx == 0) ||
                    ((se.y == 0 || se.y == height) && se.dy == 0))
                se.partition = true;
        }
    }

    /**
     * Identifies segments of continuous walls on the maze and fills the segment list
     */
    private ArrayList<Seg> generateSegments() {
        ArrayList<Seg> sl = new ArrayList<Seg>();

        generateSegmentForHorizontalWalls(sl);

        generateSegmentsForVerticalWalls(sl);
        // starting positions for segments seem to be chosen such that segments represent top or left walls
        return sl ;
    }

    /**
     * Identify segments of continuous walls in a vertical direction
     */
    private void generateSegmentsForVerticalWalls(ArrayList<Seg> sl) {
        int x;
        int y;
        // we search for vertical walls, so for each row
        for (x = 0; x != width; x++) {
            y = 0;
            while (y < height) {
                // find the beginning of a segment
                if (cells.hasNoWallOnLeft(x, y)) {
                    y++;
                    continue;
                }
                int starty = y;
                // find the end of a segment
                while (cells.hasWallOnLeft(x, y)) {
                    y++;
                    if (y == height)
                        break;
                    if (cells.hasWallOnTop(x, y))
                        break;
                }
                // create segment with (x,starty) being being the actual start position of the segment, y-starty being the positive length
                sl.add(new Seg(x*Constants.MAP_UNIT, starty*Constants.MAP_UNIT,
                        0, (y-starty)*Constants.MAP_UNIT, dists.getDistance(x, starty), colchange));
            }
            y = 0;
            while (y < height) {
                // find the beginning of a segment
                if (cells.hasNoWallOnRight(x, y)) {
                    y++;
                    continue;
                }
                int starty = y;
                // find the end of a segment
                while (cells.hasWallOnRight(x, y)) {
                    y++;
                    if (y == height)
                        break;
                    if (cells.hasWallOnTop(x, y))
                        break;
                }
                // create segment with (x+1,y) being being one off in both directions from the last cell in this segment, starty-y being the negative length
                // since we are looking at right walls, one off in the right direction (x+1) are then cells that have this segment on its left hand side
                sl.add(new Seg((x+1)*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
                        0, (starty-y)*Constants.MAP_UNIT, dists.getDistance(x, starty), colchange));
            }
        }
    }
    /**
     * Identify segments of continuous walls in a horizontal direction
     */
    private void generateSegmentForHorizontalWalls(ArrayList<Seg> sl) {
        int x;
        int y;

        for (y = 0; y != height; y++) {

            x = 0;
            while (x < width) {

                if (cells.hasNoWallOnTop(x, y)) {
                    x++;
                    continue;
                }

                int startx = x;

                while (cells.hasWallOnTop(x, y)) {
                    x++;
                    if (x == width)
                        break;
                    if (cells.hasWallOnLeft(x, y))
                        break;
                }


                sl.add(new Seg(x*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
                        (startx-x)*Constants.MAP_UNIT, 0, dists.getDistance(startx, y), colchange));
            }

            x = 0;
            while (x < width) {

                if (cells.hasNoWallOnBottom(x, y)) {
                    x++;
                    continue;
                }
                int startx = x;

                while (cells.hasWallOnBottom(x, y)) {
                    x++;
                    if (x == width)
                        break;
                    if (cells.hasWallOnLeft(x, y))
                        break;
                }


                sl.add(new Seg(startx* Constants.MAP_UNIT, (y+1)*Constants.MAP_UNIT,
                        (x-startx)*Constants.MAP_UNIT, 0, dists.getDistance(startx, y), colchange));
            }
        }
    }

    /**
     * Method called in genNodes to determine the minimum of all such grades.
     * The method is static, i.e. it does not update internal attributes and just calculates the returned value.
     */
    private int grade_partition(ArrayList<Seg> sl, Seg pe) {

        final int x  = pe.x;
        final int y  = pe.y;
        final int dx = pe.dx;
        final int dy = pe.dy;
        final int inc = (sl.size() >= 100) ? sl.size() / 50 : 1 ;

        int lcount = 0, rcount = 0, splits = 0;

        for (int i = 0; i < sl.size(); i += inc) {
            Seg se = (Seg) sl.get(i);
            int df1x = se.x-x;
            int df1y = se.y-y;
            int sendx = se.x + se.dx;
            int sendy = se.y + se.dy; // end of segment
            int df2x = sendx - x; // difference between end of segment and x
            int df2y = sendy - y; // difference between end of segment and y
            int nx = dy;
            int ny = -dx;
            int dot1 = df1x * nx + df1y * ny;
            int dot2 = df2x * nx + df2y * ny;
            if (getSign(dot1) != getSign(dot2)) {
                if (dot1 == 0)
                    dot1 = dot2;
                else if (dot2 != 0) {
                    splits++;
                    continue;
                }
            }
            if (dot1 > 0 ||
                    (dot1 == 0 && se.getDir() ==  pe.getDir())) {
                rcount++;
            } else if (dot1 < 0 ||
                    (dot1 == 0 && se.getDir() == -pe.getDir())) {
                lcount++;
            } else {
                dbg("grade_partition problem: dot1 = "+dot1+", dot2 = "+dot2);
            }
        }
        return Math.abs(lcount-rcount) + splits * 3;
    }
    /**
     * Generate tree of BSP nodes for a given maze
     */
    public BSPNode generateBSPNodes() {
        // determine segments, i.e. walls over multiple cells in a vertical or horizontal direction
        ArrayList<Seg> seglist = generateSegments();

        setPartitionBitForCertainSegments(seglist); // partition bit true means that those are not considered any further for node generation


        cells.setTopToOne(0, 0); // TODO: check why this is done. It creates a top wall on position (0,0). This may even corrupt a maze and block its exit!

        return genNodes(seglist); // creates a data structure to quickly search for segments
    }
    /**
     * Provides the sign of a given integer number

     */
    static int getSign(int num) {
        return (num < 0) ? -1 : (num > 0) ? 1 : 0;
    }
    /**
     * Produce output for debugging purposes
     * @param str
     */
    static void dbg(String str) {
        System.out.println("BSPBuilder: "+str);
    }
}
