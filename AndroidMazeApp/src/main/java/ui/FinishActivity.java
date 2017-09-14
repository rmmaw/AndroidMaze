package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui;

/**
 * Created by jeskay on 11/13/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.R;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazeController;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.MazePanel;

public class FinishActivity extends Activity {
    public Intent intentFromTitle;
    public MazePanel panel;
    public String solver;
    public int builder;
    public int difficulty;
    boolean fromFile;
    private int saveMazeIndex;
    public MazeController maze;
    private static final String TAG = "FinishActivity";

    /**
     * Initializes this activity and sets all the appropriate widgets.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntents();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView successText = (TextView) findViewById(R.id.textViewSuccess);
        TextView failureText = (TextView) findViewById(R.id.textViewFailure);
        TextView batteryText = (TextView) findViewById(R.id.batteryLevel);
        TextView pathlenText = (TextView) findViewById(R.id.pathLength);
        TextView startovText = (TextView) findViewById(R.id.textView3);
        LinearLayout finishScreen = (LinearLayout) findViewById(R.id.finishScreen);

        //win
        if (getIntent().getBooleanExtra("Outcome", true)){
            successText.setBackgroundColor(Color.WHITE);
            batteryText.setBackgroundColor(Color.WHITE);
            pathlenText.setBackgroundColor(Color.WHITE);
            failureText.setBackgroundColor(Color.WHITE);
            startovText.setBackgroundColor(Color.WHITE);

            Log.v(TAG, "Game Win");
            failureText.setVisibility(View.INVISIBLE);
            successText.setVisibility(View.VISIBLE);;

        }
        //loss
        else{
            Log.v(TAG, "Game Loss");
            successText.setVisibility(View.INVISIBLE);
            failureText.setVisibility(View.VISIBLE);

        }

        String batteryLevel = Float.toString(getIntent().getFloatExtra("BatteryLevel", 0));
        String pathLength = Integer.toString(getIntent().getIntExtra("PathLength", 0));

        batteryText.setText("Battery Level: " + batteryLevel);
        pathlenText.setText("Path Length: " + pathLength + "\n");


    }



    public void toStart(View view) {

        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);

    }
    public void toRevisit(View view) {
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("Solver", solver);
        intent.putExtra("Maze", maze);
        intent.putExtra("Builder", builder);
        intent.putExtra("Difficulty", saveMazeIndex);

        startActivity(intent);

    }
    /**
     * Get the intents from the title activity.
     */
    private void getIntents(){
        Log.v(TAG, "Receiving intents from TitleActivity");
        intentFromTitle = getIntent();
        solver = intentFromTitle.getStringExtra("Solver");
        builder = intentFromTitle.getIntExtra("Builder", builder);
        difficulty = intentFromTitle.getIntExtra("Difficulty", difficulty);
        fromFile = intentFromTitle.getBooleanExtra("FromFile", fromFile);
        saveMazeIndex = intentFromTitle.getIntExtra("SaveMazeIndex", saveMazeIndex);
    }



}