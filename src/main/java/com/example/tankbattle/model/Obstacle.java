package com.example.tankbattle.model;

import com.example.tankbattle.HelloApplication;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Obstacle {

    private Canvas canvas;
    private GraphicsContext gc;
    private Image wall;
    public double x, y, v, v1;
    public int damage = 5;
    public Rectangle rectangle;

    public Obstacle(Canvas canvas, double x, double y, double v, double v1){
        this.x = x;
        this.y = y;
        this.v = v;
        this.v1 = v1;
        gc = canvas.getGraphicsContext2D();
        String uri = "file:"+ HelloApplication.class.getResource("images/wall.jpg").getPath();
        wall = new Image(uri);
        this.canvas = canvas;
    }

    public void draw(){
        gc.setFill(Color.GOLD);
        gc.fillRect(x,y,v,v1);
        gc.drawImage(wall, x, y, v, v1);
        rectangle = new Rectangle(x,y,v,v1);
    }

    public void setDamage(){
        damage = damage - 1;
    }

}
