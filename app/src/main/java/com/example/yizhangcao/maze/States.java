package com.example.yizhangcao.maze;

/**
 * Created by yizhangcao on 2018-02-26.
 */

public class States {

    // Constants
    static final int MAX_X = 10;
    static final int MAX_Y = 10;

    // Modes
    static final int FREE_MODE = 0; // Can press on any tile
    static final int EDIT_START_MODE = 1; // Must place the Entrance to exit this mode
    static final int EDIT_END_MODE = 2; // Must place the Exit to exit this mode

    // States
    static final int NO_STATE = -1;
    static final int WALL_STATE = 3;
    static final int START_STATE = 4;
    static final int END_STATE = 5;

    // Update Speed
    static final int UPDATE_SPEED = 100 * (40/MAX_X);
}
