package com.example.yizhangcao.maze;

import android.graphics.Color;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by yizhangcao on 2018-02-27.
 */

public class PathFindingThread extends Thread {

    private MazeTile[][] maze; // Maze that the path finding thread is working on
    private MazeTile start; // Entrance Tile
    private MazeTile end; // Exit Tile
    private ArrayList<MazeTile> neighbours; // Tiles around the given tile

    public PathFindingThread (MazeTile[][] maze, MazeTile start, MazeTile end){
        this.maze = maze;
        this.start = start;
        this.end = end;
        neighbours = null;
    }

    // Set the tiles around the given tile
    public void setNeighbours (MazeTile theTile)
    {
        //set the neighbour cells of this cell
        neighbours = theTile.getNeighbours();

        if (theTile.getxCord() != 0)
        {
            addNeighbour (theTile.getxCord()-1, theTile.getyCord());
        }

        if (theTile.getyCord() != 0)
        {
            addNeighbour (theTile.getxCord(), theTile.getyCord()-1);
        }

        if (theTile.getxCord() < States.MAX_X - 1)
        {
            addNeighbour (theTile.getxCord()+1, theTile.getyCord());
        }

        if (theTile.getyCord() < States.MAX_Y - 1)
        {
            addNeighbour (theTile.getxCord(), theTile.getyCord()+1);
        }
        theTile.setNeighbours(neighbours);
    }

    private void addNeighbour (int x, int y)
    {
        MazeTile neighbour = getTile(x,y);
        if (neighbour.getCurrentState() != States.WALL_STATE)
        {
            // Add the none wall tiles around the given tile to the list
            neighbours.add (neighbour);
        }
    }

    public MazeTile getAnUnvisitedNeighbour (MazeTile theTile)
    {
        //This methood returns one of the unvisited neigbours of this cell
        //if there is one.
        //This method is useful for recursively searching the maze

        if (theTile.getNeighbours() == null)
        {
            return null;
        }

        if (theTile.getNeighbours().size() == 0)
        {
            return null;
        }

        for (int i = 0; i < theTile.getNeighbours().size (); i++)
        {
            MazeTile next = (MazeTile) theTile.getNeighbours().get (i);
            if (!next.isVisited())
            {
                return next;
            }
        }

        return null;
    }

    private MazeTile getTile(int x, int y){
        return maze[x][y];
    }

    public boolean findPathFrom (final MazeTile aTile)
    {
        //Recursively determine if we can reach the finish cell from cell aCell
        //This method answers whether a route was found, and sets the cells along the
        //route, but invoking their setInRoute() method, so the route will be shown (in green)
        //when the maze is drawn

        aTile.setVisited(); //mark aCell as being visisted (so the problem will get smaller)

        //Basis Case
        //if aCell is the finish cell set it to be in the route and return true (we are done)

        //MISSING CODE --basis case
        if (aTile == start)
        {
//            aTile.setInRoute();
            return true;
        }
        //END BASIS CASE

        //Recursion

        MazeTile next = getAnUnvisitedNeighbour(aTile); //get an unvisited neighbour of aCell

        //Check each of aCell's unvisited neigbours. If you find a neighbour n for which
        //findPathFrom(n) is true, set the neighbour to be in the route and return true (we are done).
        //otherwise check the remaining unvisited neighbours.

        //If no unvisited neighbour can be found that allows a path, return false ---there is no path
        //possible.

        //MISSING CODE ----recursive case
        while (next != null)
        {
            if (findPathFrom (next))
            {
                if(aTile == end)
                    return true;
                aTile.setInRoute();
                // Delay by UDATE_SPEED *See States class
                try {
                        Thread.sleep(States.UPDATE_SPEED);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Run on UI thread
                MainActivity.getInstance().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        aTile.setBackgroundColor(Color.BLUE);
                    }
                });
                return true;
            }
            next = getAnUnvisitedNeighbour (aTile);
        }

        //END ---recursive case


        return false;

    }

    public void run(){
        // Find all the neighbours for all the tile
        for (int row=0; row<States.MAX_Y; row++){
            for (int column=0; column<States.MAX_X; column++){
                setNeighbours(getTile(column,row));
            }
        }

        // Calling the recursive path finding function backwards
        findPathFrom(end);
        // Run on UI thread after the path finding is done
        MainActivity.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.getInstance(),
                        "Solved!", Toast.LENGTH_SHORT).show();
                // Enable all the buttons
                MainActivity.getInstance().changeButtonState();
                // Reset Maze Path for next use
                MainActivity.getInstance().reset();
            }
        });
    }



}
