package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.BSPNode;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Cells;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Distance;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.MazeBuilder;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.MazeBuilderEller;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.MazeBuilderPrim;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui.GeneratingActivity;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui.PlayActivity;


public class MazeController extends Application implements Serializable {


    final private ArrayList<Viewer> views = new ArrayList<Viewer>() ;
    MazePanel mazePanel;

    public BasicRobot robot;
    private int state;

    private int percentdone = 0;
    public boolean showMaze;
    public boolean showSolution;
    public boolean mapMode;



    int viewx, viewy, angle;
    int dx, dy;
    int px, py ;
    int walkStep;
    int viewdx, viewdy;



    boolean deepdebug = false;
    //boolean allVisible = false;
    //boolean newGame = false;


    public int mazew;
    public int mazeh;
    Cells mazecells ;
    Distance mazedists ;
    Cells seencells ;

    BSPNode rootnode ;
    public MazeBuilder mazebuilder;


    final int ESCAPE = 27;


    int method = 0 ;

    int zscale = Constants.VIEW_HEIGHT/2;


    boolean manualMode = true;

    private RangeSet rset;
    private GeneratingActivity generatingActivity;
    private PlayActivity playActivity;
    public boolean pauseDriver = false;
    public boolean killDriver = false;
    public int saveMazeIndex = -1;
    public FirstPersonDrawer fpd;

    /**
     * Constructor
     */
    public MazeController() {
        super() ;

    }
    /**
     * Constructor that also selects a particular generation method
     */
    public MazeController(int method)
    {
        super() ;

        if (1 == method)
            this.method = 1 ;
        mazePanel = new MazePanel() ;

        if (2 == method)
            this.method = 2 ;
        mazePanel = new MazePanel() ;
    }
    /**
     * Method to initialize internal attributes. Called separately from the constructor.
     */
    public void init() {
        setState(Constants.STATE_TITLE);
        rset = new RangeSet();
        killDriver = false;
        saveMazeIndex = -1;

    }

    /**
     * Method obtains a new Mazebuilder and has it compute new maze,
     * it is only used in keyDown()
     * @param skill level determines the width, height and number of rooms for the new maze
     */
    public void build(int skill) {

        setState(Constants.STATE_GENERATING);
        percentdone = 0;

        // select generation method
        switch(method){
            case 2 : mazebuilder = new MazeBuilderEller(); //generate with Eller algorithm
                System.out.println("Using Eller Algorithm to Build");
                break ;
            case 1 : mazebuilder = new MazeBuilderPrim(); // generate with Prim's algorithm
                System.out.println("Using Prim Algorithm to Build");
                break ;
            case 0 : mazebuilder = new MazeBuilder(); // generate with Falstad's original algorithm (0 and default), note the missing break statement
                System.out.println("Using Default Algorithm to Build");
                break;
            default : mazebuilder = new MazeBuilder();
                System.out.println("Using Default Algorithm to Build because something was read wrong.");
                break ;
        }
        mazew = Constants.SKILL_X[skill];

        mazeh = Constants.SKILL_Y[skill];
        mazebuilder.build(this, mazew, mazeh, Constants.SKILL_ROOMS[skill], Constants.SKILL_PARTCT[skill]);

    }

    /**
     * Call back method for MazeBuilder to communicate newly generated maze as reaction to a call to build()
     * @param root node for traversals, used for the first person perspective
     * @param c encodes the maze with its walls and border
     * @param dists encodes the solution by providing distances to the exit for each position in the maze
     * @param startx current position, x coordinate
     * @param starty current position, y coordinate
     */
    public void newMaze(BSPNode root, Cells c, Distance dists, int startx, int starty, boolean fromFile) {
        {

            c.saveLogFile(Cells.deepedebugWallFileName);
        }
        if (Cells.deepdebugWall)
        // adjust internal state of maze model
        showMaze = showSolution =  false;
        mazecells = c ;
        mazedists = dists;
        seencells = new Cells(mazew+1,mazeh+1) ;
        rootnode = root ;
        setCurrentDirection(1, 0) ;
        setCurrentPosition(startx,starty) ;
        walkStep = 0;
        viewdx = dx<<16;
        viewdy = dy<<16;
        angle = 0;
        mapMode = false;

        setState(Constants.STATE_PLAY);
        cleanViews() ;


        fpd = new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,
                Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, root, this) ;
        addView(fpd);

        addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,Constants.STEP_SIZE, mazecells, seencells, 10, mazedists.getDists(), mazew, mazeh, this)) ;

        }

   /**
     * Register a view
     */
    public void addView(Viewer view) {
        views.add(view) ;
    }
    /**
     * Unregister a view
     */
    public void removeView(Viewer view) {
        views.remove(view) ;
    }
    /**
     * Remove obsolete FirstPersonDrawer and MapDrawer
     */
    private void cleanViews() {

        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
            {

                it.remove() ;
            }
        }

    }
    /**
     * Notify all registered viewers to redraw their graphics
     */
    public void notifyViewerRedraw() {

        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;

            if (state == Constants.STATE_PLAY){
                v.redraw(mazePanel, getState(), px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;

            }
            if (state == Constants.STATE_FINISH){
                if (robot.getBatteryLevel() > 0)
                    playActivity.endGame(true, robot.getBatteryLevel(), robot.getPathLength());
                else
                    playActivity.endGame(false, 0, robot.getPathLength());
            }

            if (robot != null)
                playActivity.updateBatteryBar(robot.getBatteryLevel());
        }


        playActivity.runOnUiThread(new Runnable() {public void run() {
            playActivity.updateGraphics();}});


    }
    /**
     * Notify all registered viewers to increment the map scale
     */
    private void notifyViewerIncrementMapScale() {

        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            v.incrementMapScale() ;
        }
        mazePanel.update() ;
    }
    /**
     * Notify all registered viewers to decrement the map scale
     */
    private void notifyViewerDecrementMapScale() {

        Iterator<Viewer> it = views.iterator() ;
        while (it.hasNext())
        {
            Viewer v = it.next() ;
            v.decrementMapScale() ;
        }
        // update the screen with the buffer graphics
        mazePanel.update() ;
    }

    boolean isInMapMode() {
        return mapMode ;
    }
    boolean isInShowMazeMode() {
        return showMaze ;
    }
    boolean isInShowSolutionMode() {
        return showSolution ;
    }
    public String getPercentDone(){
        return String.valueOf(percentdone) ;
    }
    public MazePanel getGUIWrapper() {
        return mazePanel;
    }
    public float getBatteryLevel(){
        return this.robot.getBatteryLevel();
    }
    public int getPathLength(){
        return this.robot.getPathLength();
    }
    public int getState() {
        return state;
    }
    public Cells getCells() {
        if (mazecells == null)
            throw new NullPointerException();
        return this.mazecells;
    }
    public MazeBuilder getMazeBuilder() {
        return this.mazebuilder;
    }

    public Distance getMazeDists(){
        return mazedists;
    }
    public GeneratingActivity getGeneratingActivity() {
        return generatingActivity;
    }


    public void setGeneratingActivity(GeneratingActivity generatingActivity) {
        this.generatingActivity = generatingActivity;
    }
    public void setBuilder(int builder){
        this.method = builder;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setMethod(String builderAlgorithm) {
        if (builderAlgorithm == "Eller")
            this.method = 2;
        if (builderAlgorithm == "Prim")
            this.method = 1;
        if (builderAlgorithm == "Default")
            this.method = 0;
    }
    public void setPlayActivity(PlayActivity playactivity){
        this.playActivity = playactivity;
    }


    private void setCurrentPosition(int x, int y)
    {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y)
    {
        dx = x ;
        dy = y ;
    }



    final double radify(int x) {
        return x*Math.PI/180;
    }


    /**
     * Allows external increase to percentage in generating mode with subsequence graphics update
     * @param pc gives the new percentage on a range [0,100]
     * @return true if percentage was updated, false otherwise
     */
    public boolean increasePercentage(int pc) {
        if (percentdone < pc && pc < 100) {
            percentdone = pc;
            if (getState() == Constants.STATE_GENERATING)
            {
                generatingActivity.getProgressBarHandler().post(new Runnable() {public void run() {

                    generatingActivity.getProgressBar().setProgress(percentdone);
                    generatingActivity.getTxtProgress().setText(percentdone + " %");}});

            }
            else
                dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
            return true ;
        }
        return false ;
    }






    private void dbg(String str) {
        //System.out.println(str);
    }

    private void logPosition() {
        if (!deepdebug)
            return;
        dbg("x="+viewx/Constants.MAP_UNIT+" ("+
                viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
                angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
    }


    /**
     * Helper method for walk()
     * @param dir
     * @return true if there is no wall in this direction
     */
    private boolean checkMove(int dir) {
        // obtain appropriate index for direction (CW_BOT, CW_TOP ...)
        // for given direction parameter
        int a = angle/90;
        if (dir == -1)
            a = (a+2) & 3; // TODO: check why this works

        return mazecells.hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
    }



    private void rotateStep() {
        angle = (angle+1800) % 360;
        viewdx = (int) (Math.cos(radify(angle))*(1<<16));
        viewdy = (int) (Math.sin(radify(angle))*(1<<16));
        moveStep();
    }

    private void moveStep() {
        notifyViewerRedraw() ;
        try {
            Thread.currentThread();
            Thread.sleep(50);
        } catch (Exception e) { }
    }

    private void rotateFinish() {
        setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
        logPosition();
    }

    private void walkFinish(int dir) {

        setCurrentPosition(px + dir*dx, py + dir*dy) ;

        if (isEndPosition(px,py)) {
            setState(Constants.STATE_FINISH);
            notifyViewerRedraw() ; //Switch to final activity here
        }
        walkStep = 0;
        logPosition();
    }

    /**
     * checks if the given position is outside the maze
     * @param x
     * @param y
     * @return true if position is outside, false otherwise
     */
    public boolean isEndPosition(int x, int y) {
        return x < 0 || y < 0 || x >= mazew || y >= mazeh;
    }



    public synchronized void walk(int dir) {
        if (!checkMove(dir)){
            moveStep();
            fpd.drawDirectionArrow();
            return;
        }
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            moveStep();
        }
        walkFinish(dir);
        fpd.drawDirectionArrow();
    }

    public synchronized void rotate(int dir) {
        final int originalAngle = angle;
        final int steps = 4;

        for (int i = 0; i != steps; i++) {
            angle = originalAngle + dir*(90*(i+1))/steps;
            rotateStep();
        }
        rotateFinish();
        fpd.drawDirectionArrow();
    }




    public void dereferenceGeneratingActivity(){
        generatingActivity = null;
    }
    public void dereferenceMazeBuilder(){
        mazebuilder = null;
    }

    public void makeGUIWrapper() {
        mazePanel = new MazePanel();
    }
    public void pauseDriver(){
        pauseDriver = true;
    }
    public void resumeDriver(){
        pauseDriver = false;
    }
    public void killDriver(){
        killDriver = true;
    }



    public void upButton(){
        try {
            this.robot.notifyRobot(0);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void leftButton(){
        try {
            this.robot.notifyRobot(1);
        } catch (Exception e) {e.printStackTrace();}
    }


    public void rightButton(){
        try {
            this.robot.notifyRobot(2);
        } catch (Exception e) {e.printStackTrace();}
    }


    public void downButton(){
        try {
            this.robot.notifyRobot(3);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void showSolution(){
        System.out.println("Tried to toggle Solution");
        showSolution = !showSolution;
        notifyViewerRedraw() ;
    }
    public void showWalls(){
        System.out.println("Tried to toggle Walls");
        showMaze = !showMaze;
        notifyViewerRedraw() ;
    }
    public void showMap(){
        System.out.println("Tried to toggle Map");
        mapMode = !mapMode;
        notifyViewerRedraw() ;
    }
    public void incrementMapSize(){
        notifyViewerIncrementMapScale() ;
        notifyViewerRedraw() ;
    }
    public void decrementMapSize(){
        notifyViewerDecrementMapScale() ;
        notifyViewerRedraw() ;
    }
    public void beginGraphics(){
        notifyViewerRedraw();
        fpd.drawDirectionArrow();
    }



}
