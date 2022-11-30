package com.example.tankbattle.model;

import com.example.tankbattle.HelloApplication;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bullet {
    private Canvas canvas;
    private GraphicsContext gc;
    public Vector pos;
    public Vector direction;

    public Bullet(Canvas canvas, Vector pos, Vector direction){
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.pos = pos;
        this.direction = direction;
    }

    public void draw(){
        /*gc.setFill(Color.RED);
        gc.fillOval(pos.x-5, pos.y-5, 10, 10);*/
        Image bullet = new Image("file:"+ HelloApplication.class.getResource("images/bullet.png").getPath());
        gc.drawImage(bullet, pos.x-5, pos.y-5, 10, 10);


        pos.x += direction.x;
        pos.y += direction.y;
    }


}