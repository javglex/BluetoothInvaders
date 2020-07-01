package com.newpath.jeg.bluetoothinvaders.Game;


/**
 * This class will be in charge of synchronizing client and server world, cross checking and updating them.
 */
public class GameServer {

    private static GameServer sGameServer;

    private GameServer(){

    }

    public static GameServer getInstance(){
        if (sGameServer==null)
            sGameServer = new GameServer();

        return sGameServer;
    }




}
