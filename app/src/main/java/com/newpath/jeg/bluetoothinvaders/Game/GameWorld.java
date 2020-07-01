package com.newpath.jeg.bluetoothinvaders.Game;

import com.newpath.jeg.bluetoothinvaders.Game.WorldObjects.BaseObject;
import com.newpath.jeg.bluetoothinvaders.Game.WorldObjects.EnemyShipSmall;

/**
 * Holds all world objects and their position
 */
public class GameWorld {

    private BaseObject[][] worldGrid;      //where all objects will be placed in the world. Stored as bytes
    private final int WORLD_HEIGHT = 50;
    private final int WORLD_WIDTH = 70;

    public GameWorld(){

        //initiate worldGrid;
        for (int i = 0; i<WORLD_HEIGHT; i++){
            for (int j = 0; j<WORLD_WIDTH; j++){
                worldGrid[i][j] = null;
            }
        }
    }

    public BaseObject[][] getWorldGrid(){
        return worldGrid;
    }

    public void updateWorldGrid(int x, int y, BaseObject object){
        worldGrid[x][y] = object;
    }

    public void instantiateEnemies(){
        for(int i = 0; i<10; i++){
            updateWorldGrid(i,1, new EnemyShipSmall());
        }
    }


}
