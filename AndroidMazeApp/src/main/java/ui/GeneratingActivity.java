package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.R;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazeController;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazePanel;

/**
 * Created by jeskay on 11/13/16.
 */

public class GeneratingActivity extends Activity {

    private static final String TAG = "GeneratingActivity";
    public ProgressBar progressBar;
    public Handler progressBarHandler = new Handler();
    public Intent intentFromTitle;
    public MazeController maze;
    public MazePanel panel;
    public String solver;
    public int builder;
    public int difficulty;
    boolean fromFile;
    private TextView generatingText;
    private boolean saveMaze;
    public int saveMazeIndex;
    private TextView txtProgress;


    /**
     * Initializes this activity, sets all the appropriate widgets.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setIndeterminate(false);
        generatingText = (TextView) findViewById (R.id.generatingText);
        //LinearLayout generatingScreen = (LinearLayout) findViewById(R.id.generatingScreen);



        initMaze();
    }

    /*
     * Create or retrieve a maze object, and give it the information that the suer chose. Then build the maze.
     */
    private void initMaze() {
        Log.v(TAG,"Creating Maze Object");
        getIntents();
        if (!fromFile){

            maze = ((MazeController) getApplicationContext());


            maze = new MazeController();


            generatingText.setText("Generating Maze");
            maze.setBuilder(builder);
            maze.setGeneratingActivity(this);
            maze.init();

            if (saveMaze)
                maze.saveMazeIndex = difficulty;
                    this.saveMazeIndex = difficulty;
            maze.build(difficulty);
        }

    }

    /**
     * Get the intents from the title activity.
     */
    private void getIntents(){
        Log.v(TAG, "Receiving intents from TitleActivity");
        intentFromTitle= getIntent();
        solver = intentFromTitle.getStringExtra("Solver");
        builder = intentFromTitle.getIntExtra("Builder", builder);
        difficulty = intentFromTitle.getIntExtra("Difficulty", difficulty);
        fromFile = intentFromTitle.getBooleanExtra("FromFile", fromFile);
        saveMazeIndex = intentFromTitle.getIntExtra("SaveMazeIndex", saveMazeIndex);
        saveMaze = intentFromTitle.getBooleanExtra("SaveMaze", saveMaze);
    }



    /**
     * Starts the PlayActivity - dereferences unserializable objects that maze references so that it may be serialized and sent as an intent.
     */
    public void startPlay(){

        Log.v(TAG,"Starting PlayActivity");

        maze.dereferenceMazeBuilder();
        maze.dereferenceGeneratingActivity();


        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("Solver", solver);
        intent.putExtra("Maze", maze);
        intent.putExtra("SaveMazeIndex", saveMazeIndex);
        intent.putExtra("Builder",builder);
        startActivity(intent);
    }









    public Handler getProgressBarHandler() {
        return progressBarHandler;
    }


    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public TextView getTxtProgress() {
        return txtProgress;
    }
}
