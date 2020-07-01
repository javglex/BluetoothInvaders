package com.newpath.jeg.bluetoothinvaders.Game.WorldObjects;

public class Position {
    int positionX;
    int positionY;

    public Position(){
        positionX = positionY = 0;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionX = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}
