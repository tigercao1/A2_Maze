package com.example.yizhangcao.maze;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    /*
    * All Static constants are declared in the States class
    * */

    private static MainActivity instance;

    private final String TAG = this.getClass().getSimpleName() + " @" + System.identityHashCode(this); // For logging purpose
    private MazeTile[][] mazeTiles; // 2D Array keeping track each tile of the maze

    /*
    * The Maze Layout is designed using a Vertical Linear Layout filled with MAX_Y number of Horizontal Linear Layout
    * And each horizontal Linear Layout contains MAX_X number of MazeTiles
    * */
    private LinearLayout verticalMazeLayout;

    private MazeTile start;
    private MazeTile end;

    private Button solveButton;
    private boolean buttonState = true;

    private int editMode; // Maze Editing modes *See States class for Modes

    private PathFindingThread pathFindingThread; // Thread to find path of the maze

    private Handler handler; // Handler for running on UI thread while in PathFindingThread

    public static MainActivity getInstance() {return instance;}

    public Handler getHandler() {return handler;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializations
        handler = new Handler();

        instance = this;

        verticalMazeLayout = (LinearLayout) findViewById(R.id.maze_vertical);
        mazeTiles = new MazeTile[States.MAX_X][States.MAX_Y];
        editMode = States.FREE_MODE;

        solveButton = (Button) findViewById(R.id.button_solve);
        // End of Initializations

        // Setting up the maze tiles programmatically
        for (int y=0; y<States.MAX_Y; y++){
            // Layout Params for each Button
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            params.setMargins(1,1,1,1);
            // Setting up Horizontal Layout
            LinearLayout horizontalMazeLayout = new LinearLayout(this);
            horizontalMazeLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalMazeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            for (int x=0; x<States.MAX_X; x++){
                final int xCord = x;
                final int yCord = y;

                // Setting up the text size and style for every maze tile
                MazeTile button = new MazeTile(this, x, y);
                button.setBackgroundColor(Color.GRAY);
                button.setTextSize(7);
                button.setText(String.valueOf(x+1));
                button.setLayoutParams(params);

                // Set onClickListener for each maze tile
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleClickBasedOnIndex(xCord, yCord);
                    }
                });

                // Add each maze tile to each horizontal linear layout
                horizontalMazeLayout.addView(button);

                // Keep track of each maze tile
                mazeTiles[x][y] = button;
            }
            // Add each horizontal linear layout to the vertical linear layout
            verticalMazeLayout.addView(horizontalMazeLayout);
        }


        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSolveButton();
            }
        });

        // Initialize the Entrance and Exit
        start = getTile(0,0);
        end = getTile(States.MAX_X-1, States.MAX_Y-1);
        start.setCurrentState(States.START_STATE);
        end.setCurrentState(States.END_STATE);

        updateUI();
    }

    private void handleClickBasedOnIndex(int x, int y){
        MazeTile current = getTile(x,y);
        // Free mode handles pressing on any tile
        if (editMode == States.FREE_MODE) {
            // Entrance Tile
            if (current.getCurrentState() == States.START_STATE) {
                current.setCurrentState(States.NO_STATE);
                editMode = States.EDIT_START_MODE;
            // Exit Tile
            } else if (current.getCurrentState() == States.END_STATE) {
                current.setCurrentState(States.NO_STATE);
                editMode = States.EDIT_END_MODE;
            // Wall Tile
            } else {
                if (current.getCurrentState() == States.WALL_STATE) {
                    current.setCurrentState(States.NO_STATE);
                } else {
                    current.setCurrentState(States.WALL_STATE);
                }
            }
        // Start Mode will force user to place the Entrance Down if removed
        } else if (editMode == States.EDIT_START_MODE) {
            if (current.getCurrentState()==States.END_STATE){
                Toast.makeText(MainActivity.this,
                        "This is the Exit!", Toast.LENGTH_SHORT).show(); // Entrance Tile and Exit Tile are designed so that they cannot be on top of each other
            } else {
                current.setCurrentState(States.START_STATE);
                start = current; // update entrance tile
                editMode = States.FREE_MODE; // Reset the mode back to free when tile is placed
            }
        // End Mode will force user to place the Entrance Down if removed
        } else {
            if (current.getCurrentState()==States.START_STATE){
                Toast.makeText(MainActivity.this,
                        "This is the Entrance!", Toast.LENGTH_SHORT).show(); // Entrance Tile and Exit Tile are designed so that they cannot be on top of each other
            } else {
                current.setCurrentState(States.END_STATE);
                end = current; // update exit tile
                editMode = States.FREE_MODE; // Reset the mode back to free when tile is placed
            }
        }

        updateUI();
    }

    // When Solve button is pressed will start a new Thread for maze path finding
    private void handleSolveButton(){
        pathFindingThread = new PathFindingThread(mazeTiles, start, end);
        pathFindingThread.start();
        changeButtonState(); // Disable all tiles while the thread is running
    }

    // Disable All the tiles
    public void changeButtonState(){
        buttonState = !buttonState;
        for (int y=0; y<States.MAX_Y; y++) {
            for (int x = 0; x < States.MAX_X; x++) {
                MazeTile obj = getTile(x,y);
                obj.setEnabled(buttonState);
            }
        }
        solveButton.setEnabled(buttonState);
    }

    // Reset Maze Path
    public void reset(){
        for (int y=0; y<States.MAX_Y; y++) {
            for (int x = 0; x < States.MAX_X; x++) {
                MazeTile currentTile = getTile(x,y);
                currentTile.unVisit();
                currentTile.unRoute();
                currentTile.setNeighbours(new ArrayList<MazeTile>());
            }
        }
    }

    private MazeTile getTile (int x, int y){
        return mazeTiles[x][y];
    }

    private void updateUI(){
        for (int y=0; y<States.MAX_Y; y++){
            for (int x=0; x<States.MAX_X; x++){
                MazeTile currentTile = getTile(x,y);
                if (currentTile.getCurrentState()==States.WALL_STATE)
                    currentTile.setBackgroundColor(Color.BLACK);
                else if (currentTile.getCurrentState()==States.START_STATE)
                    currentTile.setBackgroundColor(Color.GREEN);
                else if (currentTile.getCurrentState()==States.END_STATE)
                    currentTile.setBackgroundColor(Color.RED);
                else
                    currentTile.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
