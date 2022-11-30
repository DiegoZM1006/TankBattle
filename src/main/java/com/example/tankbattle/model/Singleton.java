package com.example.tankbattle.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Singleton {

    // Formato del singleton

    private static Singleton instance;

    public Avatar player1;

    public Avatar player2;

    public Avatar cpu;
    public Avatar winner;
    public static ObservableList<Avatar> players = FXCollections.observableArrayList();

    private Singleton() {
        player1 = new Avatar("");
        player2 = new Avatar("");
        cpu = new Avatar("");
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
            String output = "";
            File f = new File("data.txt");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String line;
                while ((line = reader.readLine()) != null) {
                    output += line + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(output);
            if(!output.equals("")) {
                String[] lines = output.split("\n");
                String temp[];
                String name;
                int wins;
                Avatar newAvatar = new Avatar("", 0);
                for (int i = 0; i < lines.length; i += 1) {
                    temp = lines[i].split("\\+");
                    name = temp[0];
                    wins = Integer.parseInt(temp[1]);
                    newAvatar = new Avatar(name, wins);
                    players.add(newAvatar);
                }
            }
        }
        return instance;
    }

    public Avatar getPlayer1() {
        return player1;
    }

    public void setPlayer1(Avatar player1) {
        this.player1 = player1;
    }

    public Avatar getPlayer2() {
        return player2;
    }

    public void setPlayer2(Avatar player2) {
        this.player2 = player2;
    }

    public Avatar getCpu() {
        return cpu;
    }

    public void setCpu(Avatar cpu) {
        this.cpu = cpu;
    }

    public Avatar getWinner() {
        return winner;
    }

    public void setWinner(Avatar winner) {
        this.winner = winner;
        boolean flag = false;
        String name = winner.name;
        int wins = winner.getWins();
        for (Avatar a : players) {
            if (winner.name.equals(a.name)) {
                a.setWins(1);
                flag = true;
                break;
            }
        }
        if(!flag){
            players.add(winner);
        }
        String output;
        output = FileUtil.readFile() + winner.name + "+" + winner.getWins();
        FileUtil.fillDb(output);
    }

    public static ObservableList<Avatar> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<Avatar> players) {
        Singleton.players = players;
    }
}