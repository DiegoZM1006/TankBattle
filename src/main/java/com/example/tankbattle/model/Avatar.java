package com.example.tankbattle.model;

import com.example.tankbattle.HelloApplication;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Avatar {

    private Canvas canvas;
    private GraphicsContext gc;
    private Image tank;
    public Vector pos;
    public Vector direction;
    public String name;
    public String colorTank;
    public int wins;

    public Avatar(Canvas canvas, String colorTank, double xPos, double yPos, String name){
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        this.colorTank = colorTank;
        String uri = "file:"+ HelloApplication.class.getResource("images/tanks/tank" + colorTank + ".png").getPath();
        tank = new Image(uri);
        this.tank = tank;
        pos = new Vector(xPos, yPos);
        direction = new Vector(2,2);
        this.name = name;
        wins = 0;
    }

    public Avatar(String name) {
        this.name = name;
    }

    public Avatar(String name, int wins) {
        this.name = name;
        this.wins = wins;
    }

    public void draw(){
        gc.save();
        gc.translate(pos.x, pos.y);
        gc.rotate(90+direction.getAngle());
        gc.drawImage(tank, -25,-25, 50,50);
        gc.restore();
    }

    public String getName() {
        return name;
    }

    /*public void drawJ2() {
        gc.save();
        gc.translate(pos.x, pos.y);
        gc.rotate(90 + direction.getAngle());
        gc.drawImage(tank, -25,-25, 50,50);
        gc.restore();
    }*/

    public void setPosition(double x, double y) {
        pos.x = (int) x - 25;
        pos.y = (int) y - 25;
    }

    public void changeAngle(double a){
        double amp = direction.getAmplitude();
        double angle = direction.getAngle();
        angle += a;
        direction.x = amp*Math.cos(Math.toRadians(angle));
        direction.y = amp*Math.sin(Math.toRadians(angle));
    }

    public void moveForward(){
        pos.x += direction.x/2;
        pos.y += direction.y/2;
    }

    public void moveForward2(){
        pos.x = pos.x;
        pos.y = pos.y;
    }

    public void moveForwardCpu(){
        if(pos.y < 27 && direction.y < 0 || pos.y > 286 && direction.y > 0 || pos.x > 621 && direction.x > 0 || pos.x < 23 && direction.x<0){
            //nada
        }else{
            pos.x += direction.x/2;
            pos.y += direction.y/2;
        }
    }

    public void moveBackward() {
        pos.x -= direction.x/2;
        pos.y -= direction.y/2;
    }

    public void moveBackward2() {
        pos.x = pos.x;
        pos.y = pos.y;
    }

    public void setWins(int e){
        wins = wins + e;
    }

    public int getWins(){
        return wins;
    }


}
