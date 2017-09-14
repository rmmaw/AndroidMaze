package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.R;

public class AMazeActivity extends Activity {

    private static final String TAG = "AMazeActivity";
    public String builderSelected = "Default";
    public String solverSelected = "Manual";
    public int difficulty = 0;


    /**
     * Initializes this activity and sets all the appropriate widgets.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        Spinner builderSpinner = (Spinner) findViewById(R.id.spinner1);
        Spinner driverSpinner = (Spinner) findViewById(R.id.spinner2);
        CheckBox genMaze = (CheckBox) findViewById(R.id.checkBox2);
        LinearLayout titleScreen = (LinearLayout) findViewById(R.id.titleScreen);


        builderSpinner.setOnItemSelectedListener(new builderSelectedListener());
        driverSpinner.setOnItemSelectedListener(new solverSelectedListener());

        genMaze.setChecked(true);
        setBuilderSpinner(builderSpinner);
        setdriverSpinner(driverSpinner);



    }






    /**
     * Called when the build Explore is clicked.
     */
    public void buildMaze(View view){
        Log.v(TAG,"Sending the user's choices to generatingActivity");
        //Start next activity and start building the maze.
        CheckBox saveMaze = (CheckBox) findViewById(R.id.saveMaze);
        SeekBar difficulty = (SeekBar) findViewById(R.id.seekBar1);


        System.out.println(solverSelected);
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("Solver", solverSelected);


        //intent for Builder
        if (builderSelected.equals("Eller")){
            intent.putExtra("Builder", 2);}
        if (builderSelected.equals("Prim")){
            intent.putExtra("Builder", 1);}
        else
            intent.putExtra("Builder", 0);


        intent.putExtra("Difficulty", difficulty.getProgress());



        intent.putExtra("SaveMaze", saveMaze.isChecked());

        intent.putExtra("SaveMazeIndex", difficulty.getProgress());
        startActivity(intent);
    }



    /**
     * Passed the selection through to code, also will change screen to show spinners needed.
     */
    public void boxCheck(View view){
        Log.v(TAG, "Box was checked");
        Spinner builders = (Spinner) findViewById(R.id.spinner1);
        CheckBox genMaze = (CheckBox) findViewById(R.id.checkBox2);
        SeekBar difficulty = (SeekBar) findViewById(R.id.seekBar1);
        CheckBox saveMaze = (CheckBox) findViewById(R.id.saveMaze);


        Boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()){



            case (R.id.checkBox2):
                if (checked) {
                    builders.setVisibility(View.VISIBLE);
                    saveMaze.setVisibility(View.VISIBLE);
                    difficulty.setMax(9);}
                else{
                    builders.setVisibility(View.INVISIBLE);
                    saveMaze.setVisibility(View.INVISIBLE);
                    difficulty.setMax(4);
                }
                break;

            case (R.id.saveMaze):
                if (checked) {
                    difficulty.setMax(4);
                }
                else{
                    difficulty.setMax(9);
                }
                break;

        }

    }



    /**
     * Populate the builder spinner with a array of strings.
     */
    private void setBuilderSpinner(Spinner builderSpinner) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        builderSpinner.setAdapter(adapter);
        Log.v(TAG, "Populated builderspinner");
    }




    /**
     * Populate the driver spinner with a array of strings.
     */
    private void setdriverSpinner(Spinner driverSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.drivers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        driverSpinner.setAdapter(adapter);
        Log.v(TAG, "Populated builderspinner");

    }



    /**
     * Used to listen to when the builder spinner has a selection made. Sets a field variable to be used by
     * start maze.
     */
    public class builderSelectedListener implements OnItemSelectedListener {

        /**
         * Sets a field variable with the item selected.
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            builderSelected = parent.getItemAtPosition(pos).toString();
        }

        /**
         * Necessary override that is currently empty
         */
        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }



    /**
     * Used to listen to when the solver spinner has a selection made. Sets a field variable to be used by
     * start maze.
     */
    public class solverSelectedListener implements OnItemSelectedListener {

        /**
         * Sets a field variable with the item selected.
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            solverSelected = parent.getItemAtPosition(pos).toString();
        }

        /**
         * Necessary override that is currently empty
         */
        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }





}
