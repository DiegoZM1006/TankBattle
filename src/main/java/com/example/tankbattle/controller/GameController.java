package com.example.tankbattle.controller;

import com.example.tankbattle.HelloApplication;
import com.example.tankbattle.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private Label nameCpu;

    @FXML
    private Label namePlayer1;

    @FXML
    private Label namePlayer2;

    @FXML
    private ImageView lifePlayer1;

    @FXML
    private ImageView lifePlayer2;

    @FXML
    private ImageView lifeCpu;
    @FXML
    private ImageView bulletsCpu;

    @FXML
    private ImageView bulletsPlayer1;

    @FXML
    private ImageView bulletsPlayer2;

    // Variables globales de la ventana
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    private boolean isRunning = true;

    //Elementos gráficos
    private Avatar j1;
    private Avatar j2;
    private Avatar cpu;
    private final Image background = new Image("file:" + HelloApplication.class.getResource("images/backGround.png").getPath());
    private final Image wall = new Image("file:" + HelloApplication.class.getResource("images/wall.jpg").getPath());


    // private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bulletsJ1;
    private ArrayList<Bullet> bulletsJ2;
    private ArrayList<Bullet> bulletsCpuArr;


    private ArrayList<Obstacle> obstacles;


    // Estados de las teclas player1
    boolean Wpressed = false;
    boolean Apressed = false;
    boolean Spressed = false;
    boolean Dpressed = false;
    boolean Rpressed = false;
    boolean Sppressed = false;
    // Estados de las teclas player2
    boolean Leftpressed = false;
    boolean Rightpressed = false;
    boolean Uppressed = false;
    boolean Downpressed = false;
    boolean MinusPressed = false;
    boolean ShiftPressed = false;
    // Assets de los jugadores
    int contLifePlayer1 = 5;
    int contLifePlayer2 = 5;
    int contLifeCpu = 5;
    int contBulletJ1 = 0;
    int contBulletJ2 = 0;
    int contBulletCpu = 0;

    // Muertes de jugadores
    boolean diedPlayer1 = false;
    boolean diedPlayer2 = false;
    boolean diedCpu = false;

    int contBoard;

    private Clip clip;
    private Clip clipBullet;
    private Clip clipRecharge;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String uri = HelloApplication.class.getResource("audio/game.wav").getPath();
        System.out.println(uri);
        File musicPath = new File(uri);

        if(musicPath.exists()){

            try {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(3);

            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("No existe");
        }
        bulletsJ1 = new ArrayList<>();
        bulletsJ2 = new ArrayList<>();
        bulletsCpuArr = new ArrayList<>();
        obstacles = new ArrayList<>();
        dataPlayer();
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        L_SUPERIOR = 27;
        L_INFERIOR = canvas.getHeight() - 27;
        L_IZQUIERDO = 25;
        L_DERECHO = canvas.getWidth() - 31;

        /*enemies = new ArrayList<>();
        enemies.add(new Enemy(canvas, 300, 100));
        enemies.add(new Enemy(canvas, 300, 300));*/

        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);

        obstacles.add(new Obstacle(canvas, 165, 85, 65, 65));
        obstacles.add(new Obstacle(canvas, 355, 85, 165, 65));
        obstacles.add(new Obstacle(canvas, 645, 85, 65, 65));

        obstacles.add(new Obstacle(canvas, 165, 240, 65, 90));
        obstacles.add(new Obstacle(canvas, 400, 250, 65, 65));
        obstacles.add(new Obstacle(canvas, 645, 240, 65, 90));

        obstacles.add(new Obstacle(canvas, 165, 400, 65, 65));
        obstacles.add(new Obstacle(canvas, 355, 400, 165, 65));
        obstacles.add(new Obstacle(canvas, 645, 400, 65, 65));

        j1 = new Avatar(canvas, "Red", 100, 100, namePlayer1.getText());
        j2 = new Avatar(canvas, "Blue", 750, 500, namePlayer2.getText());
        cpu = new Avatar(canvas, "Green", 750, 100, nameCpu.getText());

        draw();
    }

    void dataPlayer() {
        namePlayer1.setText(Singleton.getInstance().getPlayer1().name);
        namePlayer2.setText(Singleton.getInstance().getPlayer2().name);
        nameCpu.setText(Singleton.getInstance().getCpu().name);
        drawAssets();
    }

    void drawAssets() {
        // Vidas
        if(contLifePlayer1 < 0) {
            contLifePlayer1 = 0;
        }
        if(contLifePlayer2 < 0) {
            contLifePlayer2 = 0;
        }
        if(contLifeCpu < 0) {
            contLifeCpu = 0;
        }
        lifePlayer1.setImage(new Image("file:" + HelloApplication.class.getResource("images/hearts/" + contLifePlayer1 + "Hearts.png").getPath()));
        lifePlayer2.setImage(new Image("file:" + HelloApplication.class.getResource("images/hearts/" + contLifePlayer2 + "Hearts.png").getPath()));
        lifeCpu.setImage(new Image("file:" + HelloApplication.class.getResource("images/hearts/" + contLifeCpu + "Hearts.png").getPath()));
        // Balas
        bulletsPlayer1.setImage(new Image("file:" + HelloApplication.class.getResource("images/bullets/" + (5 - contBulletJ1) + "Bullets.png").getPath()));
        bulletsPlayer2.setImage(new Image("file:" + HelloApplication.class.getResource("images/bullets/" + (5 - contBulletJ2) + "Bullets.png").getPath()));
        bulletsCpu.setImage(new Image("file:" + HelloApplication.class.getResource("images/bullets/" + (5 - contBulletCpu) + "Bullets.png").getPath()));
    }

    public void draw() {
        new Thread(
                () -> {
                    while (isRunning) {
                        Platform.runLater(() -> {
                            // gc.setFill(Color.BLACK);
                            // gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                            gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());

                            for (Obstacle o : obstacles) {
                                if (o.damage <= 0) {
                                    // obstacles.remove(o);
                                } else {
                                    o.draw();
                                }
                            }

                            if (contLifePlayer1 > 0 && j1 != null) {
                                j1.draw();
                                // Pintar balas J1

                                for (int i = 0; i < bulletsJ1.size(); i++) {
                                    bulletsJ1.get(i).draw();
                                    if (bulletsJ1.get(i).pos.x + 1 > canvas.getWidth() || bulletsJ1.get(i).pos.y > canvas.getHeight() + 15 || bulletsJ1.get(i).pos.y < -15 || bulletsJ1.get(i).pos.x < -15) {
                                        bulletsJ1.remove(i);
                                    }
                                }
                            } else {
                                diedPlayer1 = true;
                                j1 = null;
                            }
                            if (contLifePlayer2 > 0 && j2 != null) {
                                j2.draw();
                                //Pintar balas J2
                                for (int i = 0; i < bulletsJ2.size(); i++) {
                                    bulletsJ2.get(i).draw();
                                    if (bulletsJ2.get(i).pos.x + 1 > canvas.getWidth() || bulletsJ2.get(i).pos.y > canvas.getHeight() + 15 || bulletsJ2.get(i).pos.y < -15 || bulletsJ2.get(i).pos.x < -15) {
                                        bulletsJ2.remove(i);
                                    }
                                }
                            } else {
                                diedPlayer2 = true;
                                j2 = null;
                            }
                            if (contLifeCpu > 0 && cpu != null) {
                                cpu.draw();
                                //Pintar cpu
                                for (int i = 0; i < bulletsCpuArr.size(); i++) {
                                    bulletsCpuArr.get(i).draw();
                                    if (bulletsCpuArr.get(i).pos.x + 1 > canvas.getWidth() || bulletsCpuArr.get(i).pos.y > canvas.getHeight() + 15 || bulletsCpuArr.get(i).pos.y < -15 || bulletsCpuArr.get(i).pos.x < -15) {
                                        bulletsCpuArr.remove(i);
                                    }
                                }
                            } else {
                                diedCpu = true;
                                cpu = null;
                            }

                            //Colisisones entre balas y jugadores
                            if (j1 != null) {
                                detectColissionJ1fromJ2();
                            }
                            if (j1 != null) {
                                detectColissionJ1fromCpu();
                            }
                            if (j2 != null) {
                                detectColissionJ2fromJ1();

                            }
                            if (j2 != null) {
                                detectColissionJ2fromCpu();
                            }
                            if (cpu != null) {
                                detectColissionCpufromJ1();
                            }
                            if (cpu != null) {
                                detectColissionCpufromJ2();
                            }

                            //Deteccion de daño en paredes
                            detectDamageForWallsJ1();
                            detectDamageForWallsJ2();
                            detectDamageForWallsCpu();
                            /*for (int i=0;i<enemies.size();i++){
                                for (int j=0;j< bullets.size();j++){
                                    break;
                                }
                            }*/

                            doKeyboardActions();
                            drawAssets();

                            if ((diedCpu && diedPlayer2) || (diedPlayer1 && diedPlayer2) || (diedPlayer1 && diedCpu)) {

                                isRunning = false;

                                if (contLifePlayer1 > 0) {
                                    j1.setWins(1);
                                    Singleton.getInstance().setWinner(j1);
                                } else if (contLifePlayer2 > 0) {
                                    j2.setWins(1);
                                    Singleton.getInstance().setWinner(j2);
                                } else if (contLifeCpu > 0) {
                                    cpu.setWins(1);
                                    Singleton.getInstance().setWinner(cpu);
                                }

                                if(contBoard<1) {
                                    clip.stop();
                                    HelloApplication.showWindow("board.fxml");
                                    Stage currentStage = (Stage) nameCpu.getScene().getWindow();
                                    currentStage.hide();
                                    contBoard = contBoard+1;
                                }

                            }

                            if (contLifeCpu > 0 && cpu!=null) {
                                cpuIA();
                            }

                        });
                        //Sleep
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        ).start();
    }

    private boolean checkBulletsCpu() {
        if (bulletsCpuArr.size() > 4) {
            return false;
        } else {
            return true;
        }
    }


    // Daño del jugador 1 a las paredes
    private void detectDamageForWallsJ1() {
        for (int i = 0; i < obstacles.size(); i++) {
            for (int j = 0; j < bulletsJ1.size(); j++) {
                if (obstacles.get(i).rectangle.intersects(bulletsJ1.get(j).pos.x + bulletsJ1.get(j).direction.x - 5, bulletsJ1.get(j).pos.y + bulletsJ1.get(j).direction.y - 5, 10, 10)) {
                    obstacles.get(i).setDamage();
                    bulletsJ1.remove(bulletsJ1.get(j));
                    if (obstacles.get(i).damage <= 0) {
                        obstacles.remove(obstacles.get(i));
                    }
                }
            }
        }
    }

    // Daño del jugador 2 a las paredes
    private void detectDamageForWallsJ2() {
        for (int i = 0; i < obstacles.size(); i++) {
            for (int j = 0; j < bulletsJ2.size(); j++) {
                if (obstacles.get(i).rectangle.intersects(bulletsJ2.get(j).pos.x + bulletsJ2.get(j).direction.x - 5, bulletsJ2.get(j).pos.y + bulletsJ2.get(j).direction.y - 5, 10, 10)) {
                    obstacles.get(i).setDamage();
                    bulletsJ2.remove(bulletsJ2.get(j));
                    if (obstacles.get(i).damage <= 0) {
                        obstacles.remove(obstacles.get(i));
                    }
                }
            }
        }
    }

    // Daño del jugador 3 (Cpu) a las paredes
    private void detectDamageForWallsCpu() {
        for (int i = 0; i < obstacles.size(); i++) {
            for (int j = 0; j < bulletsCpuArr.size(); j++) {
                if (obstacles.get(i).rectangle.intersects(bulletsCpuArr.get(j).pos.x + bulletsCpuArr.get(j).direction.x - 5, bulletsCpuArr.get(j).pos.y + bulletsCpuArr.get(j).direction.y - 5, 10, 10)) {
                    obstacles.get(i).setDamage();
                    bulletsCpuArr.remove(bulletsCpuArr.get(j));
                    if (obstacles.get(i).damage <= 0) {
                        obstacles.remove(obstacles.get(i));
                    }
                }
            }
        }
    }

    // Balas del jugador j1 para matar al j2
    private void detectColissionJ2fromJ1() {
        for (int j = 0; j < bulletsJ1.size(); j++) {
            Bullet b = bulletsJ1.get(j);
            Avatar enemy = j2;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsJ1.remove(b);
                contLifePlayer2 -= 1;
                //enemies.remove(e);
            }
        }
    }

    // Balas del j3 (cpu) para matar al j2
    private void detectColissionJ2fromCpu() {
        for (int j = 0; j < bulletsCpuArr.size(); j++) {
            Bullet b = bulletsCpuArr.get(j);
            Avatar enemy = j2;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsCpuArr.remove(b);
                contLifePlayer2 -= 1;
                //enemies.remove(e);
            }
        }
    }

    // Balas del j2 para matar al j1
    private void detectColissionJ1fromJ2() {
        for (int j = 0; j < bulletsJ2.size(); j++) {
            Bullet b = bulletsJ2.get(j);
            Avatar enemy = j1;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsJ2.remove(b);
                contLifePlayer1 -= 1;
                //enemies.remove(e);
            }
        }
    }

    // Balas del j3 (Cpu) para matar al j1
    private void detectColissionJ1fromCpu() {
        for (int j = 0; j < bulletsCpuArr.size(); j++) {
            Bullet b = bulletsCpuArr.get(j);
            Avatar enemy = j1;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsCpuArr.remove(b);
                contLifePlayer1 -= 1;
                //enemies.remove(e);
            }
        }
    }

    // Balas del j1 para matar al j3 (Cpu)
    private void detectColissionCpufromJ1() {
        for (int j = 0; j < bulletsJ1.size(); j++) {
            Bullet b = bulletsJ1.get(j);
            Avatar enemy = cpu;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsJ1.remove(b);
                contLifeCpu -= 1;
                //enemies.remove(e);
            }
        }
    }

    // Balas del j2 para matar al j3 (Cpu)
    private void detectColissionCpufromJ2() {
        for (int j = 0; j < bulletsJ2.size(); j++) {
            Bullet b = bulletsJ2.get(j);
            Avatar enemy = cpu;
            double c1 = b.pos.x - enemy.pos.x;
            double c2 = b.pos.y - enemy.pos.y;
            double distance = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
            if (distance < 22.5) {
                bulletsJ2.remove(b);
                contLifeCpu -= 1;
                //enemies.remove(e);
            }
        }
    }

    public boolean detectColissionWallsJ1w() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(j1.pos.x + j1.direction.x - 24, j1.pos.y + j1.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    public boolean detectColissionWallsCpuDown() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(cpu.pos.x - cpu.direction.x - 24, cpu.pos.y + cpu.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    public boolean detectColissionWallsCpuUp() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(cpu.pos.x + cpu.direction.x - 24, cpu.pos.y + cpu.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    public boolean detectColissionWallsJ1s() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(j1.pos.x - j1.direction.x - 24, j1.pos.y + j1.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    public boolean detectColissionWallsJ2Up() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(j2.pos.x + j2.direction.x - 24, j2.pos.y + j2.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    public boolean detectColissionWallsJ2Down() {
        boolean flag = false;

        for (int i = 0; i < obstacles.size(); i++) {

            if (obstacles.get(i).rectangle.intersects(j2.pos.x - j2.direction.x - 24, j2.pos.y - j2.direction.y - 24, 50, 50)) {

                flag = true;

            }

        }
        return flag;
    }

    double L_SUPERIOR = 27;
    double L_INFERIOR = 286;
    double L_DERECHO = 621;
    double L_IZQUIERDO = 23;


    private void doKeyboardActions() {

        // Player1

        if (j1 != null) {
            if (Wpressed) {
                if (j1.pos.y < L_SUPERIOR && j1.direction.y < 0 || j1.pos.y > L_INFERIOR && j1.direction.y > 0 || j1.pos.x > L_DERECHO && j1.direction.x > 0 || j1.pos.x < L_IZQUIERDO && j1.direction.x < 0 || detectColissionWallsJ1w()) {
                    //System.out.println("SE METIO");
                    // System.out.println("Posicion " + j1.pos.y + " + " + j1.pos.x);
                    // System.out.println("Direccion : " + j1.direction.y + " + " + j1.direction.x);

                    j1.moveForward2();
                } else {
                    j1.moveForward();
                    // System.out.println( "Posicion " + j1.pos.y + " + " + j1.pos.x);
                    // System.out.println("Direccion : " + j1.direction.y + " + " + j1.direction.x);
                }
            }
            if (Apressed) {
                j1.changeAngle(-6);
            }
            if (Spressed) {
                if (j1.pos.y < L_SUPERIOR && j1.direction.y > 0 || j1.pos.y > L_INFERIOR && j1.direction.y < 0 || j1.pos.x > L_DERECHO && j1.direction.x < 0 || j1.pos.x < L_IZQUIERDO && j1.direction.x > 0 || detectColissionWallsJ1s()) {
                    //System.out.println("SE METIO");
                    //System.out.println(j1.direction.y + " + " + j1.direction.x);
                    j1.moveBackward2();
                } else {
                    j1.moveBackward();
                    //System.out.println(j1.direction.y + " + " + j1.direction.x);
                }
            }
            if (Dpressed) {
                j1.changeAngle(6);
            }
        }

        if (j2 != null) {
            // Player 2
            if (Uppressed) {
                if (j2.pos.y < L_SUPERIOR && j2.direction.y < 0 || j2.pos.y > L_INFERIOR && j2.direction.y > 0 || j2.pos.x > L_DERECHO && j2.direction.x > 0 || j2.pos.x < L_IZQUIERDO && j2.direction.x < 0 || detectColissionWallsJ2Up()) {
                    //System.out.println("SE METIO");
                    /*System.out.println( "Posicion " + j2.pos.y + " + " + j2.pos.x);
                    System.out.println( "Direccion : "+ j2.direction.y + " + " + j2.direction.x);*/

                    j2.moveForward2();
                } else {
                    j2.moveForward();
                    /*System.out.println( "Posicion " + j2.pos.y + " + " + j2.pos.x);
                    System.out.println( "Direccion : "+ j2.direction.y + " + " + j2.direction.x);*/
                }
            }
            if (Leftpressed) {
                j2.changeAngle(-6);
            }
            if (Downpressed) {
                if (j2.pos.y < L_SUPERIOR && j2.direction.y > 0 || j2.pos.y > L_INFERIOR && j2.direction.y < 0 || j2.pos.x > L_DERECHO && j2.direction.x < 0 || j2.pos.x < L_IZQUIERDO && j2.direction.x > 0 || detectColissionWallsJ2Down()) {
                    //System.out.println("SE METIO");
                    //System.out.println(j1.direction.y + " + " + j1.direction.x);
                    j2.moveBackward2();
                } else {
                    j2.moveBackward();
                    //System.out.println(j1.direction.y + " + " + j1.direction.x);
                }
            }
            if (Rightpressed) {
                j2.changeAngle(6);
            }
        }

    }

    private void onKeyReleased(KeyEvent keyEvent) {
        // Player1
        if (keyEvent.getCode() == KeyCode.W) {
            Wpressed = false;
        }
        if (keyEvent.getCode() == KeyCode.A) {
            Apressed = false;
        }
        if (keyEvent.getCode() == KeyCode.S) {
            Spressed = false;
        }
        if (keyEvent.getCode() == KeyCode.D) {
            Dpressed = false;
        }

        //J2
        if (keyEvent.getCode() == KeyCode.UP) {
            Uppressed = false;
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
            Leftpressed = false;
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            Downpressed = false;
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            Rightpressed = false;
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        // System.out.println(keyEvent.getCode());
        if (keyEvent.getCode() == KeyCode.W) {
            Wpressed = true;
        }
        if (keyEvent.getCode() == KeyCode.A) {
            Apressed = true;
        }
        if (keyEvent.getCode() == KeyCode.S) {
            Spressed = true;
        }
        if (keyEvent.getCode() == KeyCode.D) {
            Dpressed = true;
        }
        if (keyEvent.getCode() == KeyCode.R) {
            if (contLifePlayer1 > 0) {
                contBulletJ1 = 0;
                soundRecharge();
            }
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            if (contLifePlayer1 > 0) {
                if (contBulletJ1 < 5) {
                    Bullet bullet = new Bullet(canvas, new Vector(j1.pos.x, j1.pos.y), new Vector(j1.direction.x, j1.direction.y));
                    bulletsJ1.add(bullet);
                    contBulletJ1 = contBulletJ1 + 1;
                    soundBullet();
                }
            }
        }

        //Player 2
        if (keyEvent.getCode() == KeyCode.UP) {
            Uppressed = true;
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
            Leftpressed = true;
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            Downpressed = true;
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            Rightpressed = true;
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            if (contLifePlayer2 > 0) {
                contBulletJ2 = 0;
                soundRecharge();
            }
        }
        if (keyEvent.getCode() == KeyCode.MINUS) {
            if (contLifePlayer2 > 0) {
                if (contBulletJ2 < 5) {
                    Bullet bullet = new Bullet(canvas, new Vector(j2.pos.x, j2.pos.y), new Vector(j2.direction.x, j2.direction.y));
                    bulletsJ2.add(bullet);
                    contBulletJ2 = contBulletJ2 + 1;
                    soundBullet();
                }
            }
        }

    }

    public void cpuIA() {
        //while(contLifeCpu>0){
        double distancep1;
        if (contLifePlayer1 > 0) {
            double dxp1 = j1.pos.x - cpu.pos.x;
            double dyp1 = j1.pos.y - cpu.pos.y;
            distancep1 = Math.sqrt(Math.pow(dxp1, 2) + Math.pow(dyp1, 2));
        } else {
            distancep1 = Integer.MAX_VALUE;
        }
        double distancep2;
        if (contLifePlayer2 > 0) {
            double dxp2 = j2.pos.x - cpu.pos.x;
            double dyp2 = j2.pos.y - cpu.pos.y;
            distancep2 = Math.sqrt(Math.pow(dxp2, 2) + Math.pow(dyp2, 2));
        } else {
            distancep2 = Integer.MAX_VALUE;
        }
        double componentX, componentY;
        double rule = 0;
        if (distancep1 < distancep2) {
            // System.out.println("SE METO MATO AL J2");
            if (!detectColissionWallsCpuUp()) {
                componentX = (j1.pos.x - cpu.pos.x);
                componentY = (j1.pos.y - cpu.pos.y);
                rule = Math.sqrt(Math.pow(componentX, 2) + Math.pow(componentY, 2));
                cpu.direction.x = componentX / rule;
                cpu.direction.y = componentY / rule;
            }
            if (distancep1 < 180) {
                cpu.moveForward2();
                if (contLifeCpu > 0) {
                    if (checkBulletsCpu()) {
                        if (contBulletCpu < 5) {
                            Bullet bullet = new Bullet(canvas, new Vector(cpu.pos.x, cpu.pos.y), new Vector(cpu.direction.x, cpu.direction.y));
                            bulletsCpuArr.add(bullet);
                            contBulletCpu = contBulletCpu + 1;
                            soundBullet();
                        } else {
                            contBulletCpu = 4;
                            soundRecharge();
                        }
                    }
                }
            } else {
                if (detectColissionWallsCpuUp()) {
                    // System.out.println("UP");
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                } else if (detectColissionWallsCpuDown()) {
                    // System.out.println("DOWN");
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                } else {
                    // System.out.println("ELSE");
                    cpu.moveForward();
                }
            }
        } else if(distancep2 < distancep1) {
            if (!detectColissionWallsCpuUp()) {
                componentX = (j2.pos.x - cpu.pos.x);
                componentY = (j2.pos.y - cpu.pos.y);
                rule = Math.sqrt(Math.pow(componentX, 2) + Math.pow(componentY, 2));
                cpu.direction.x = componentX / rule;
                cpu.direction.y = componentY / rule;
            }
            if (distancep2 < 180) {
                cpu.moveForward2();
                if (contLifeCpu > 0) {
                    if (checkBulletsCpu()) {
                        if (contBulletCpu < 5) {
                            Bullet bullet = new Bullet(canvas, new Vector(cpu.pos.x, cpu.pos.y), new Vector(cpu.direction.x, cpu.direction.y));
                            bulletsCpuArr.add(bullet);
                            contBulletCpu = contBulletCpu + 1;
                            soundBullet();
                        } else {
                            contBulletCpu = 4;
                            soundRecharge();
                        }
                    }
                }
            } else {
                if (detectColissionWallsCpuUp()) {
                    // System.out.println("UP");
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                    cpu.changeAngle(6);
                    cpu.moveForward();
                } else if (detectColissionWallsCpuDown()) {
                    // System.out.println("DOWN");
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                    cpu.changeAngle(-6);
                    cpu.moveForward();
                } else {
                    // System.out.println("ELSE");
                    cpu.moveForward();
                }
            }

        }
        // cpu.moveForwardCpu();
    }
    //}

    public void soundRecharge() {

        String uri = HelloApplication.class.getResource("audio/recharge.wav").getPath();
        System.out.println(uri);
        File musicPath = new File(uri);

        if(musicPath.exists()){

            try {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clipBullet = AudioSystem.getClip();
                clipBullet.open(audioInput);
                clipBullet.start();
                // clipBullet.loop(3);

            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("No existe");
        }

    }

    public void soundBullet() {

        String uri = HelloApplication.class.getResource("audio/bullet.wav").getPath();
        System.out.println(uri);
        File musicPath = new File(uri);

        if(musicPath.exists()){

            try {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clipRecharge = AudioSystem.getClip();
                clipRecharge.open(audioInput);
                clipRecharge.start();
                // clipRecharge.loop(3);

            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("No existe");
        }

    }

}
