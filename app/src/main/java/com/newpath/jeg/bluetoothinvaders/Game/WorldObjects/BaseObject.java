package com.newpath.jeg.bluetoothinvaders.Game.WorldObjects;

public class BaseObject {

    private Position position;

    public BaseObject(){
        position = new Position();
    }

    public void updatePosition(int x, int y){
        position.setPositionX(x);
        position.setPositionY(y);
    }

    public Position getPosition(){
        return this.position;
    }

    public void shiftPosition(int shiftX, int shiftY){
        int currentX = position.getPositionX();
        int currentY = position.getPositionY();

        position.setPositionY(currentY + shiftY);
        position.setPositionX(currentX + shiftX);
    }



}
