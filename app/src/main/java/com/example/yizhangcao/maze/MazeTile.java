package com.example.yizhangcao.maze;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by yizhangcao on 2018-02-26.
 */

class MazeTile extends android.support.v7.widget.AppCompatButton{
    private int currentState; // keep track of its own state *See States class
    private boolean inRoute; // determine if this tile is a part of the solution path
    private boolean isVisited; // any checked tile will be set to visited
    // Keep track of this tile's location
    private final int x;
    private final int y;
    // Keep track of the tiles around this tile
    private ArrayList<MazeTile> neighbours;


    public MazeTile(Context context) {
        super(context);
        currentState = -1;
        inRoute = false;
        isVisited = false;
        x = -1;
        y = -1;
    }

    public MazeTile(Context context, int x, int y){
        super(context);
        currentState = -1;
        inRoute = false;
        isVisited = false;
        this.x = x;
        this.y = y;
        neighbours = new ArrayList<MazeTile>();
    }

    public MazeTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentState = -1;
        inRoute = false;
        isVisited = false;
        x = -1;
        y = -1;
    }

    public MazeTile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        currentState = -1;
        inRoute = false;
        isVisited = false;
        x = -1;
        y = -1;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int state) {
        currentState = state;
    }

    public boolean isInRoute() {
        return inRoute;
    }

    public void setInRoute() {
        inRoute = true;
    }

    public void unRoute() {
        inRoute = false;
    }

    public ArrayList<MazeTile> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<MazeTile> neighbours) {
        this.neighbours = neighbours;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited() {
        isVisited = true;
    }

    public void unVisit() {
        isVisited = false;
    }

    public int getxCord() {
        return x;
    }

    public int getyCord() {
        return y;
    }
}
