package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation;

/**
 * Created by jeskay on 11/27/16.
 */

import android.os.AsyncTask;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Constants;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazeController;




public class MazeBuilder extends AsyncTask<Void,Void,Integer> {
    public int width, height ;
    public MazeController maze;
    public int rooms;
    public int expectedPartiters;


    public int startx, starty ;
    public Cells cells;
    public Distance dists ;


    protected SingleRandom random ;

    public Thread buildThread;



    /**
     * Constructor for a randomized maze generation
     */
    public MazeBuilder(){
        random = SingleRandom.getRandom();
    }
    /**
     * Constructor with option to make maze generation deterministic or random
     */
    public MazeBuilder(boolean deterministic){
        if (true == deterministic)
        {
            SingleRandom.setSeed(123);

        }
        random = SingleRandom.getRandom();
    }

    /**
     * Provides the sign of a given integer number

     */
    static int getSign(int num) {
        return (num < 0) ? -1 : (num > 0) ? 1 : 0;
    }

    /**
     * This method generates a maze.

     */
    protected void generate() {

        generatePathways();

        final int[] remote = dists.computeDistances(cells) ;


        final int[] pos = dists.getStartPosition();
        startx = pos[0] ;
        starty = pos[1] ;


        cells.setExitPosition(remote[0], remote[1]);
    }

    /**
     * This method generates pathways into the maze.
     */
    protected void generatePathways() {
        int[][] origdirs = new int[width][height] ;
        int x = random.nextIntWithinInterval(0, width-1) ;
        int y = 0;
        final int firstx = x ;
        final int firsty = y ;
        int dir = 0;
        int origdir = dir;
        cells.setCellAsVisited(x, y);
        while (true) {
            int dx = Constants.DIRS_X[dir];
            int dy = Constants.DIRS_Y[dir];
            if (!cells.canGo(x, y, dx, dy)) {
                dir = (dir+1) & 3;
                if (origdir == dir) {
                    if (x == firstx && y == firsty)
                        break;
                    int odr = origdirs[x][y];
                    dx = Constants.DIRS_X[odr];
                    dy = Constants.DIRS_Y[odr];
                    x -= dx;
                    y -= dy;
                    origdir = dir = random.nextIntWithinInterval(0, 3);
                }
            } else {
                cells.deleteWall(x, y, dx, dy);
                x += dx;
                y += dy;
                cells.setCellAsVisited(x, y);
                origdirs[x][y] = dir;
                origdir = dir = random.nextIntWithinInterval(0, 3);
            }
        }
    }

    static final int MIN_ROOM_DIMENSION = 3 ;
    static final int MAX_ROOM_DIMENSION = 8 ;
    /**
     * Allocates space for a room of random dimensions in the maze.
     */
    private boolean placeRoom() {

        final int rw = random.nextIntWithinInterval(MIN_ROOM_DIMENSION, MAX_ROOM_DIMENSION);
        if (rw >= width-4)
            return false;

        final int rh = random.nextIntWithinInterval(MIN_ROOM_DIMENSION, MAX_ROOM_DIMENSION);
        if (rh >= height-4)
            return false;


        final int rx = random.nextIntWithinInterval(1, width-rw-1);
        final int ry = random.nextIntWithinInterval(1, height-rh-1);
        final int rxl = rx+rw-1;
        final int ryl = ry+rh-1;

        if (cells.areaOverlapsWithRoom(rx, ry, rxl, ryl))
            return false ;

        cells.markAreaAsRoom(rw, rh, rx, ry, rxl, ryl);
        return true;
    }

    static void dbg(String str) {
        System.out.println("MazeBuilder: "+str);
    }



    /**
     * Fill the given maze object with a newly computed maze according to parameter settings

     */
    public void build(MazeController mz, int w, int h, int roomct, int pc) {
        init(mz, w, h, roomct, pc);
        this.execute();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        try {

            cells.initialize();

            generateRooms();

            Thread.sleep(SLEEP_INTERVAL) ;


            generate();

            Thread.sleep(SLEEP_INTERVAL) ;

            final int colchange = random.nextIntWithinInterval(0, 255);
            final BSPBuilder b = new BSPBuilder(maze, dists, cells, width, height, colchange, expectedPartiters) ;
            BSPNode root = b.generateBSPNodes();



            Thread.sleep(SLEEP_INTERVAL) ;


            maze.newMaze(root, cells, dists, startx, starty, false);
        }
        catch (InterruptedException ex) {

            dbg("Catching signal to stop") ;
        }
        return 1;
    }

    protected void onPostExecute(Integer returnVal){
        maze.getGeneratingActivity().progressBar.setProgress(100);
        maze.getGeneratingActivity().startPlay();
    }

    /**
     * Initialize internal attributes, method is called by build() when input parameters are provided

     */
    private void init(MazeController mz, int w, int h, int roomct, int pc) {

        maze = mz;
        width = w;
        height = h;
        rooms = roomct;
        expectedPartiters = pc;

        cells = new Cells(w,h) ;
        dists = new Distance(w,h) ;

    }

    static final long SLEEP_INTERVAL = 100 ; //unit is millisecond


    static final int MAX_TRIES = 250 ;

    /**
     * Generate all rooms in a given maze where initially all walls are up.
     */
    private int generateRooms() {

        int tries = 0 ;
        int result = 0 ;
        while (tries < MAX_TRIES && result <= rooms) {
            if (placeRoom())
                result++ ;
            else
                tries++ ;
        }
        return result ;
    }

    /**
     * Notify the maze builder thread to stop the creation of a maze and to terminate
     */
    public void interrupt() {
        buildThread.interrupt() ;
    }

    public Thread getBuildThread(){
        return buildThread;
    }








}