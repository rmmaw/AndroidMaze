package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import java.io.Serializable;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Constants;


public class Distance implements Serializable{
    public int[][] dists; // matrix of dimension width x height with distance values to the exit of a maze
    int width ; 	// width of distance matrix == width of maze and cells
    int height ;	// height of distance matrix == height of maze and cells
    int[] exitposition = null ;
    int[] startposition = null ;
    int maxDistance = 0 ;
    /**
     * Constructor
     *
     */
    public Distance(int w, int h) {
        width = w ;
        height = h ;
        dists = new int[w][h] ;
    }
    /**
     * Constructor
     */
    public Distance(int[][] distances) {
        width = distances.length ;
        height = distances[0].length ;
        dists = distances ;
    }
    /**
     * Gets access to a width x height array of distances.

     */
    public int[][] getDists() {
        return dists;
    }

    /**
     * Sets the internal attribute to the given parameter value.
     *
     */
    public void setDists(int[][] dists) {
        this.dists = dists;
    }

    /**
     * Gets the distance value for the given (x,y) position
     */
    public int getDistance(int x, int y) {
        return dists[x][y] ;
    }

    /**
     * Finds the most remote point in the maze somewhere on the border.
     */
    private int[] getPositionWithMaxDistanceOnBorder() {
        // (x,y) gives the current position
        int x;
        int y;
        // find most remote point in maze somewhere on the border
        int remoteX = -1 ;
        int remoteY = -1 ;
        int remoteDist = 0;
        for (x = 0; x != width; x++) {
            y = 0 ;
            if (dists[x][y] > remoteDist) {
                remoteX = x;
                remoteY = y;
                remoteDist = dists[x][y];
            }
            y = height-1 ;
            if (dists[x][y] > remoteDist) {
                remoteX = x;
                remoteY = y;
                remoteDist = dists[x][y];
            }
        }
        for (y = 0; y != height; y++) {
            x = 0 ;
            if (dists[x][y] > remoteDist) {
                remoteX = x;
                remoteY = y;
                remoteDist = dists[x][y];
            }
            x = width-1 ;
            if (dists[x][y] > remoteDist) {
                remoteX = x;
                remoteY = y;
                remoteDist = dists[x][y];
            }
        }
        // return result in an array of length 2
        int[] result = new int[2] ;
        result[0] = remoteX ;
        result[1] = remoteY ;
        return result;
    }

    /**
     * Get the position of the entry with the highest value
     */
    private int[] getPositionWithMaxDistance() {
        int x = 0 ;
        int y = 0 ;
        int d = 0;
        int[] result = new int[2] ;
        for (x = 0; x != width; x++)
            for (y = 0; y != height; y++) {
                if (dists[x][y] > d) {
                    result[0] = x;
                    result[1] = y;
                    d = dists[x][y];
                }
            }
        maxDistance = d ; // memorize maximal distance for other purposes
        return result ;
    }
    /**
     * Get the position of the entry with the smallest value.
     */
    private int[] getPositionWithMinDistance() {
        int x = 0 ;
        int y = 0 ;
        int d = INFINITY ;
        int[] result = new int[2] ;
        for (x = 0; x != width; x++)
            for (y = 0; y != height; y++) {
                if (dists[x][y] < d) {
                    result[0] = x;
                    result[1] = y;
                    d = dists[x][y];
                }
            }
        return result ;
    }

    static final int INFINITY = Integer.MAX_VALUE;

    /**
     * Computes distances to the given position (ax,ay) for all cells in array dists.
     */
    private void computeDists(Cells cells, int ax, int ay) {
        int x, y;
        // initialize the distance array with a value for infinity
        for (x = 0; x != width; x++)
            for (y = 0; y != height; y++)
                dists[x][y] = INFINITY;
        // set the final distance at the exit position
        dists[ax][ay] = 1;
        boolean done;
        do {
            done = true;
            // check all entries in the distance array
            for (x = 0; x != width; x++)
                for (y = 0; y != height; y++)
                {
                    int sx = x;
                    int sy = y;
                    int d = dists[sx][sy];
                    if (d == INFINITY) { // found work to do.

                        done = false;
                        continue;
                    }


                    //int run = 0;
                    while (true) {

                        int n, nextn = -1;
                        // check all four directions
                        for (n = 0; n != 4; n++) {
                            int nx = sx+Constants.DIRS_X[n];
                            int ny = sy+Constants.DIRS_Y[n];
                            if (cells.hasMaskedBitsFalse(sx, sy, Constants.MASKS[n]) &&
                                    dists[nx][ny] > d+1) {
                                dists[nx][ny] = d+1;
                                nextn = n;
                            }
                        }
                        //run++;
                        if (nextn == -1)
                            break; // exit the loop if we cannot find another cell to proceed with

                        sx += Constants.DIRS_X[nextn];
                        sy += Constants.DIRS_Y[nextn];
                        // update distance for next cell
                        d++;
                    }
                }
        } while (!done);
    }

    /**
     * Compute distances for given cells object of a maze

     */
    public int[] computeDistances(Cells cells) {
        // compute temporary distances for a starting point (x,y) = (width/2,height/2)
        // which is located in the center of the maze
        computeDists(cells, width/2, height/2);
        // figure out which position is the furthest on the border to find an exit position
        exitposition = getPositionWithMaxDistanceOnBorder();
        // recompute distances for an exit point (x,y) = (remotex,remotey)
        computeDists(cells, exitposition[0], exitposition[1]);

        return exitposition ;
    }

    /**
     * Gets start position
     * @precondition computeDistances() was called before
     * @return start position somewhere within maze
     */
    public int[] getStartPosition() {
        if (null == startposition)
            startposition = getPositionWithMaxDistance() ;
        return startposition ;
    }
    /**
     * Gets exit position
     * @precondition computeDistances() was called before
     * @return exit position somewhere on the border
     */
    public int[] getExitPosition() {
        if (null == exitposition)
            exitposition = getPositionWithMinDistance() ;
        return exitposition ;
    }
    /**
     * Gets maximum distance present in maze
     * @precondition computeDistances() was called before
     * @return maximum distance
     */
    public int getMaxDistance() {
        return maxDistance ;
    }
    /**
     * Determines if given position is the exit position
     */
    public boolean isExitPosition(int x, int y){
        return ((x == exitposition[0]) && (y == exitposition[1])) ;
    }
}