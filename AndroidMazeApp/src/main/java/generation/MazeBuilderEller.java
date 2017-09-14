package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;


public class MazeBuilderEller extends MazeBuilder  {


    //public MazeConfiguration maze;
    public MazeBuilderEller() {
        super();
        System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
    }
    public MazeBuilderEller(boolean det){
        super(det);
        System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
    }

    /**
     * This is a method to generate pathways. The algorithm goes through rows, one-by-one, starting at the beginning. Walls are deleted horizontally using a random
     * number.
     */
    @Override
    protected void generatePathways() {
        int[][] cellSet = new int[width][height];
        int i;
        int newSet = width;
        int rowPtr = 0;
        int colPtr = 0;
        int rand = 0;

        while(rowPtr != height) {


            if(rowPtr == 0) {

                for(i=0; i< width ; i++)
                    cellSet[i][0] = i;
            }

            else {
                for(i = 0 ; i < width ; i++) {
                    if(cells.hasWallOnTop(i, rowPtr))
                        cellSet[i][rowPtr] = newSet++;
                    else
                        cellSet[i][rowPtr] = cellSet[i][rowPtr - 1];
                }
            }


            while(colPtr != width) {


                if(rowPtr == (height - 1)) {
                    for(i = 0 ; i < (width - 1) ; i++) {
                        if(cellSet[i][rowPtr] != cellSet[i+1][rowPtr])
                            cells.deleteWall(i, rowPtr, 1, 0);
                    }
                    break;
                }

                if(colPtr != (width - 1)) {

                    if(cellSet[colPtr][rowPtr] != cellSet[colPtr + 1][rowPtr]) {


                        rand =  random.nextIntWithinInterval(0, 1);
                    }
                    else {
                        colPtr++;
                        continue;
                    }
                }
                else{
                    colPtr++;
                    continue;
                }

                // remove a wall
                if(rand == 0) {
                    cells.deleteWall(colPtr, rowPtr, 1, 0);
                    cellSet[colPtr+1][rowPtr] = cellSet[colPtr][rowPtr];
                }
                else colPtr++;
            }

            colPtr = 0;
            // vertical wall checking
            while(colPtr != width)  {
                if(rowPtr == (height - 1))
                    break;

                int startCol = colPtr;

                if(colPtr == (width - 1)) {
                    cells.deleteWall(colPtr, rowPtr, 0, 1);
                    break;
                }

                while(true) {

                    if(colPtr == (width - 1))
                        break;
                    if(cellSet[colPtr][rowPtr] == cellSet[colPtr+1][rowPtr]) {
                        colPtr++;
                        continue;
                    }
                    else break;

                }
                // deletes one wall randomly from the bottom set
                rand = random.nextIntWithinInterval(startCol, colPtr);
                cells.deleteWall(rand, rowPtr, 0, 1);
                colPtr++;

            }
            // go to next row, and start again
            rowPtr++;
            colPtr = 0;
        }
    }
}




