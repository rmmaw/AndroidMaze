package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui;

/**
 * Created by jeskay on 11/13/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.R;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.AutomaticDriver;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.BasicRobot;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.ManualDriver;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazeController;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazePanel;


public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";
    Button upButton;
    Button downButton;
    Button leftButton;
    Button rightButton;
    Button playPause;
    Button mapButton;
    Button wallsButton;
    Button solButton;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Boolean paused = false;
    private Bitmap bitmap;
    private Canvas canvas;
    private RelativeLayout relativelayout;
    private ProgressBar batteryBar;
    private ManualDriver manualDriver;
    private BasicRobot robot;
    private AutomaticDriver automaticDriver;
    public Intent intentFromTitle;
    public MazeController maze;
    public MazePanel panel;
    public String solver;
    public int builder;
    public int difficulty;
    boolean fromFile;
  public int saveMazeIndex;

//===================================Init Methods and Activity Handlers================================//

    /**
     * Initializes this activity, sets all the appropriate widgets.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         getIntents();

        //Global/Shared Data implementation
        maze = ((MazeController) getApplicationContext());

        //Message Passing / Intent Serialization implementation
        maze = (MazeController) intentFromTitle.getSerializableExtra("Maze");


        maze.makeGUIWrapper();
        maze.setPlayActivity(this);
        initVisuals();
        considerDPad();
        maze.beginGraphics();
        initDrivers();
    }






    /**
     * Create the appropriate robot drivers for the maze - manual if the user wants to drive the robot themselves,
     * or a different automatic driver they may have chosen. Establish references between all of these drivers, robots, and
     * the maze so that they may communicate appropriately.
     */
    public void initDrivers() {

        if (solver.equals("Manual")){
            manualDriver = new ManualDriver();
            Log.v(TAG,"Using Manual algorithm.");
            robot = new BasicRobot();
            robot.setMaze(maze);

            try {
                manualDriver.setRobot(robot);
                manualDriver.setRobotNotifier(robot);   } catch (Exception e){;}

        }
        else{
            Log.v(TAG, "Using automatic Driver");
            automaticDriver = new AutomaticDriver(solver);
            automaticDriver.setDistance(maze.getMazeDists());
            robot = new BasicRobot();
            robot.setMaze(maze);


            try {
                automaticDriver.setRobot(robot);
                automaticDriver.setBasicRobot(robot);
                automaticDriver.start();
            } catch (Exception e) {e.printStackTrace();}

        }
    }

    protected void getIntents(){
        Log.v(TAG, "Receiving intents from TitleActivity");
        intentFromTitle = getIntent();
        solver = intentFromTitle.getStringExtra("Solver");
        builder = intentFromTitle.getIntExtra("Builder", builder);
        difficulty = intentFromTitle.getIntExtra("Difficulty", difficulty);
        fromFile = intentFromTitle.getBooleanExtra("FromFile", fromFile);
        saveMazeIndex = intentFromTitle.getIntExtra("SaveMazeIndex", saveMazeIndex);
    }
    /**
     * At the click of one of the two shortcut buttons, changes to the next activity
     */
    public void endGame(Boolean outcome, float batteryLevel, int pathLength){
        Intent intent = new Intent(this, FinishActivity.class);
        intent.putExtra("Outcome", outcome);
        intent.putExtra("BatteryLevel", batteryLevel);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("Solver", solver);
        intent.putExtra("Builder", builder);
        //intent.putExtra("FromFile", false);
        intent.putExtra("SaveMazeIndex",saveMazeIndex );
        startActivity(intent);
    }

    /**
     * End the game early by killing the automatic thread if it is still going.
     */
    public void endGameEarly(){
        maze.killDriver();
    }

    /**
     * Pause the driver
     */
    public void pauseDriver(){
        maze.pauseDriver();
    }

    /**
     * Resume the driver
     */
    public void resumeDriver(){
        maze.resumeDriver();
    }

    /**
     * Create all of the GUI objects that appear on screen
     */
    public void initVisuals(){
        Log.v(TAG, "Populating the play screen with visuals");
        setContentView(R.layout.activity_play);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        upButton = (Button) findViewById(R.id.buttonUp);
        downButton = (Button) findViewById(R.id.buttonDown);
        leftButton = (Button) findViewById(R.id.buttonLeft);
        rightButton = (Button) findViewById(R.id.buttonRight);
        playPause = (Button) findViewById(R.id.playpause);
        mapButton = (Button) findViewById(R.id.buttonMap);
        wallsButton = (Button) findViewById(R.id.buttonWalls);
        solButton = (Button) findViewById(R.id.buttonSol);
        batteryBar = (ProgressBar) findViewById(R.id.batterybar);
        batteryBar.setMax(2500);
        batteryBar.setProgress(2500);
        createGraphics();
    }

    /**
     * Establish a bitmap and a canvas for the MazePanel to draw to.
     */
    public void createGraphics(){

        bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        maze.getGUIWrapper().setCanvas(canvas);
        maze.getGUIWrapper().setPlayActivity(this);
        relativelayout = (RelativeLayout) findViewById(R.id.playActivity);
        relativelayout.setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    /**
     * This method is called to update the visuals on screen after the canvas has been modified.
     */
    public void updateGraphics(){
        relativelayout.setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    /**
     * Update the battery bar to the amount remaining.

     */
    public void updateBatteryBar(float batteryLevel){
        if (batteryLevel >= 0)
            batteryBar.setProgress((int)batteryLevel);
        else
            batteryBar.setProgress(0);
    }

    /**
     * Hide the D-pad if in automatic mode, make sure it is showing in manual mode.
     */
    public void considerDPad(){
        solver = intentFromTitle.getStringExtra("Solver");
        if (solver.equals("Manual")){
            showDPad();
        }
        else{
            hideDPad();
        }
    }



    public void hideDPad(){
        Log.v(TAG, "DPad made invisible");
        upButton.setVisibility(View.INVISIBLE);
        downButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
        playPause.setVisibility(View.VISIBLE);
    }

    public void showDPad(){
        upButton.setVisibility(View.VISIBLE);
        downButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        playPause.setVisibility(View.INVISIBLE);
    }


    /**
     * Toggles the view of the map.
     */
    public void map(View view){
        Log.v(TAG, "Show Map");
        maze.showMap();
    }

    /**
     * Toggles the view of the surround walls in the map.
     */
    public void walls(View view){
        Log.v(TAG, "Show Walls");
        maze.showWalls();
    }

    /**
     * Toggles the view of the solution.
     */
    public void sol(View view){
        Log.v(TAG, "Show Solution");
        maze.showSolution();
    }

    /**
     * Will move the robot forward
     * @param view
     */
    public void up(View view){
        Log.v(TAG, "Up button pressed");
        maze.upButton();
    }

    /**
     * Will Turn the robot around.
     * @param view
     */
    public void down(View view){
        Log.v(TAG, "Down button pressed");
        maze.downButton();
    }

    /**
     * Will rotate the robot left.
     * @param view
     */
    public void left(View view){
        Log.v(TAG, "Left button pressed");
        maze.leftButton();
    }

    /**
     * Will rotate the robot right.
     * @param view
     */
    public void right(View view){
        Log.v(TAG, "Right button pressed");
        maze.rightButton();
    }

    /**
     * If the back button is pressed, end the game.
     */
    @Override
    public void onBackPressed() {
        endGameEarly();
        super.onBackPressed();
    }
    /**
     * Either play or pause the automatic driver thread.
     */
    public void playPause(View view){
        if (paused){
            resumeDriver();
            playPause.setText("Pause");
        }
        else{
            pauseDriver();
            playPause.setText("Play");
        }
        paused = !paused;
    }




    public Handler getHandler() {
        return handler;
    }


}